package onegame.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.Partita;
import onegame.modello.Mossa;
import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.Utente;

/**
 * StanzaPartita rappresenta una stanza di gioco
 */
public class StanzaPartita {

    private final String idStanza; // identificativo univoco
    private final String nomeStanza; // nome visualizzato
    private final int maxUtenti;
    private final SocketIOServer server; 
    private final GestoreConnessioni gestoreConnessioni;

    // token -> Utente
    private final Map<String, Utente> utenti = new ConcurrentHashMap<>();
    // token -> SocketIOClient (client attualmente connessi)
    private final Map<String, SocketIOClient> clientConnessi = new ConcurrentHashMap<>();
    // sessionId -> token (per ricavare token da client disconnect)
    private final Map<String, String> sessionIdToToken = new ConcurrentHashMap<>();

    // Oggetti partita
    private volatile Partita partita;
    private volatile boolean partitaIniziata = false;

    // Scheduler per timer dei turni
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Lock per serializzare gestione turno
    private final Object lockTurno = new Object();

    /**
	 * Costruttore della stanza
	 * 
	 * @param idStanza         Identificativo univoco della stanza
	 * @param nomeStanza       Nome della stanza
	 * @param maxUtenti        Numero massimo di utenti consentiti nella stanza
	 * @param server           Riferimento al server
	 * @param gestoreConnessioni Riferimento al gestore delle connessioni
	 */
    public StanzaPartita(String idStanza, String nomeStanza, int maxUtenti, SocketIOServer server,
            GestoreConnessioni gestoreConnessioni) {
        this.idStanza = idStanza;
        this.nomeStanza = nomeStanza;
        this.maxUtenti = Math.max(2, maxUtenti);
        this.server = server;
        this.gestoreConnessioni = gestoreConnessioni;
    }

   /** Aggiunge un utente alla stanza.
	 * Restituisce true se l'utente è stato aggiunto con successo.
	 * Se la partita è già iniziata o la stanza è piena, restituisce false.
	 * Se il token non è valido, crea un Utente "guest".
	 * @param token Il token di sessione dell'utente
	 * @param client Il client SocketIOClient associato (può essere null se non connesso)
	 * @return true se l'utente è stato aggiunto, false altrimenti
	 */
    public boolean aggiungiUtente(String token, SocketIOClient client) {
        if (token == null) return false;
        synchronized (this) {
            if (partitaIniziata) return false;
            if (utenti.size() >= maxUtenti) return false;
            Utente ut = gestoreConnessioni.getUtenteDaToken(token);
            if (ut == null) {
                // creazione minimale di Utente locale
                ut = new Utente("guest-" + token.substring(0, Math.min(6, token.length())), true);
                ut.setTokenSessione(token);
            }
            // se Utente non ha Giocatore domain, crealo e associa
            if (ut.getGiocatore() == null) {
                Giocatore g = new Giocatore(ut.getUsername());
                ut.setGiocatore(g);
            }
            utenti.put(token, ut);
            if (client != null) {
                clientConnessi.put(token, client);
                sessionIdToToken.put(client.getSessionId().toString(), token);
            }
            broadcastStato("UTENTE_ENTRATO", "Utente entrato: " + ut.getUsername());
            // se abbiamo almeno 2 utenti, avvia la partita
            if (utenti.size() >= 2) avviaPartitaSeNonIniziata();
            return true;
        }
    }

    /** Rimuove un utente dalla stanza in base al token.
     * @param token Il token di sessione dell'utente da rimuovere
     */
    public void rimuoviUtente(String token) {
        if (token == null) return;
        synchronized (this) {
            Utente u = utenti.remove(token);
            SocketIOClient c = clientConnessi.remove(token);
            if (c != null) sessionIdToToken.remove(c.getSessionId().toString());
            if (u != null) broadcastStato("UTENTE_USCITO", "Utente uscito: " + u.getUsername());
            // se partita in corso e rimane meno di 2 utenti, termina
            if (partitaIniziata && utenti.size() < 2) {
                terminaPartita("Numero utenti insufficiente");
            }
        }
    }

    /**
     * Associa un client esistente a un token (usato in fase di riconnessione)
     * @param token Il token di sessione dell'utente
     * @param client Il client SocketIOClient da associare
     */
    public void associaClient(String token, SocketIOClient client) {
        if (token == null || client == null) return;
        clientConnessi.put(token, client);
        sessionIdToToken.put(client.getSessionId().toString(), token);
    }

    /**
     * Avvia la partita se non è già partita
     */
    private void avviaPartitaSeNonIniziata() {
        synchronized (this) {
            if (partitaIniziata) return;
            partitaIniziata = true;
            // compongo lista Giocatore domain per Partita
            List<Giocatore> lista = new ArrayList<>();
            for (Utente ut : utenti.values()) {
                Giocatore g = ut.getGiocatore();
                // associo la partita al giocatore quando la partità viene creata
                lista.add(g);
            }
            partita = new Partita(lista);
            // associa la partita ai giocatori
            for (Giocatore g : lista) {
                g.setPartita(partita);
            }
            partita.eseguiPrePartita();
            broadcastPartitaIniziata();
            // inizia il ciclo dei turni
            scheduleProssimoTurno(0);
        }
    }

    /**
     * Schedule del prossimo turno dopo delay secondi
     */
    private void scheduleProssimoTurno(long delaySecondi) {
        scheduler.schedule(() -> {
            try {
                iniziaTurno();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySecondi, TimeUnit.SECONDS);
    }

    /**
     * Inizio del turno corrente: notifica i client e avvia timer 30s per mossa automatica.
     */
    private void iniziaTurno() {
        synchronized (lockTurno) {
            if (partita == null || partita.verificaFinePartita()) {
                terminaPartita("Partita finita");
                return;
            }
            Giocatore corrente = partita.getGiocatoreCorrente();
            String tokenCorrente = trovaTokenPerGiocatore(corrente);
            if (tokenCorrente == null) {
                // se non troviamo il token, consideriamo un passo di turno forzato
                partita.passaTurno();
                scheduleProssimoTurno(1);
                return;
            }
            // invia evento turno con tempo rimanente (30s)
            clientConnessi.values().forEach(c -> c.sendEvent("partita:turno",
                    new Object[] { tokenCorrente, corrente.getNome(), 30 }));
            // schedule mossa automatica in 30s se non arriva mossa valida
            scheduler.schedule(() -> {
                try {
                    eseguiMossaAutomaticaSeNecessario(tokenCorrente);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 30, TimeUnit.SECONDS);
        }
    }

    /**
     * Esegue la mossa automatica per il giocatore corrente se il turno non è già passato
     */
    private void eseguiMossaAutomaticaSeNecessario(String tokenCorrente) {
        synchronized (lockTurno) {
            if (partita == null || partita.verificaFinePartita()) return;
            Giocatore corrente = partita.getGiocatoreCorrente();
            String tokenAttuale = trovaTokenPerGiocatore(corrente);
            if (!Objects.equals(tokenCorrente, tokenAttuale)) {
                // il turno è già avanzato
                return;
            }
            // eseguo scelta automatica usando la logica del Giocatore domain
            Mossa mossa = corrente.scegliMossaAutomatica();
            broadcastMossa(tokenCorrente, corrente, mossa, true);
            partita.passaTurno();
            // verifica fine partita
            if (partita.verificaFinePartita()) {
                terminaPartita("Vittoria di " + partita.getVincitore().getNome());
            } else {
                scheduleProssimoTurno(1);
            }
        }
    }

    /**
     * Gestisce una mossa ricevuta da client: validazione e applicazione.
     * Metodo pubblico da registrare come handler evento socket (es. "partita:mossa").
     */
    public void riceviMossa(String token, Mossa mossa) {
        synchronized (lockTurno) {
            if (token == null || mossa == null || partita == null) return;
            Utente ut = utenti.get(token);
            if (ut == null) return;
            Giocatore g = ut.getGiocatore();
            try {
                // applicaMossaSafe verifica che sia il giocatore corrente
                Mossa risultato = partita.applicaMossaSafe(g, mossa);
                if (risultato == null) {
                    // mossa non valida: notifica solo al mittente
                    SocketIOClient mitt = clientConnessi.get(token);
                    if (mitt != null) {
                        mitt.sendEvent("partita:invalid", "Mossa non valida");
                    }
                    return;
                }
                // se la mossa è di tipo SCEGLI_COLORE (jolly), applica la scelta sul modello
                if (risultato.getTipoMossa() == Mossa.TipoMossa.SCEGLI_COLORE && risultato.getCartaScelta() != null) {
                    // la carta nera deve essere impostata col colore scelto
                    // il client dovrebbe aver impostato cartaScelta.colore via JSON prima di inviare
                } else if (risultato.getTipoMossa() == Mossa.TipoMossa.GIOCA_CARTA) {
                    // rimuovo la carta dalla mano del giocatore e aggiorno stato partita
                    g.rimuoveCarta(risultato.getCartaScelta());
                    partita.giocaCarta(risultato.getCartaScelta());
                }
                broadcastMossa(token, g, risultato, false);
                // passo turno
                partita.passaTurno();
                if (partita.verificaFinePartita()) {
                    terminaPartita("Vittoria di " + partita.getVincitore().getNome());
                } else {
                    scheduleProssimoTurno(1);
                }
            } catch (Exception e) {
                // errore nell'applicazione mossa (es. giocatore non corrente)
                SocketIOClient mitt = clientConnessi.get(token);
                if (mitt != null) {
                    mitt.sendEvent("partita:error", e.getMessage());
                }
            }
        }
    }

    /**
     * Trova il token corrispondente a un Giocatore domain
     */
    private String trovaTokenPerGiocatore(Giocatore g) {
        if (g == null) return null;
        for (Map.Entry<String, Utente> e : utenti.entrySet()) {
            Utente u = e.getValue();
            if (u.getGiocatore() == g || (g.getNome() != null && g.getNome().equals(u.getUsername()))) {
                return e.getKey();
            }
        }
        return null;
    }

    /**
     * Broadcast di una mossa effettuata
     */
    private void broadcastMossa(String token, Giocatore g, Mossa mossa, boolean automatica) {
        Object payload = new Object() {
            public final String tokenUtente = token;
            public final String username = g != null ? g.getNome() : null;
            public final Mossa m = mossa;
            public final boolean auto = automatica;
            public final String cartaCorrente = partita != null && partita.getCartaCorrente() != null
                    ? partita.getCartaCorrente().toString()
                    : null;
        };
        clientConnessi.values().forEach(c -> c.sendEvent("partita:mossa", payload));
    }

    /**
     * Broadcast stato stanza (es. utente entrato/uscito)
     */
    private void broadcastStato(String stato, String messaggio) {
        ProtocolloMessaggi.RespStanza msg = new ProtocolloMessaggi.RespStanza(idStanza, stato, messaggio);
        clientConnessi.values().forEach(c -> c.sendEvent("stanza:aggiornamento", msg));
    }

    /**
     * Invia lo stato iniziale della partita ai client (in formato JSON)
     */
    private void broadcastPartitaIniziata() {
        if (partita == null) return;
        String json = partita.toJson();
        clientConnessi.values().forEach(c -> c.sendEvent("partita:inizia", json));
    }

    /**
     * Termina la partita, notifica i client e pulisce le strutture interne
     */
    private void terminaPartita(String motivo) {
        // notifica
        ProtocolloMessaggi.RespStanza msg = new ProtocolloMessaggi.RespStanza(idStanza, "TERMINATA", motivo);
        clientConnessi.values().forEach(c -> c.sendEvent("partita:terminata", msg));
        // pulizia
        puliziaPartita();
    }

    private void puliziaPartita() {
        try {
            scheduler.shutdownNow();
        } catch (Exception e) {
        	
        }
        partitaIniziata = false;
        partita = null;
    }

    public String getIdStanza() {
        return idStanza;
    }

    public String getNomeStanza() {
        return nomeStanza;
    }

    public int getMaxUtenti() {
        return maxUtenti;
    }

    public boolean isPartitaIniziata() {
        return partitaIniziata;
    }
}
