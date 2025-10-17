package onegame.server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.MossaDTO;
import onegame.server.gioco.PartitaNET;

/**
 * Rappresenta una stanza di gioco UNO. Gestisce i client e delega la logica a PartitaNET.
 */
public class StanzaPartita {

    private final int codice;
    private final long id;
    private final String nome;
    private final int maxGiocatori;
    private final GestoreConnessioni gestoreConnessioni;

    private final Map<String, SocketIOClient> clientPerToken = new ConcurrentHashMap<>();
    private final Map<String, Giocatore> giocatori = new LinkedHashMap<>();

    private PartitaNET partita;
    private boolean partitaInCorso = false;

    public StanzaPartita(int codice, long id, String nome, int maxGiocatori, GestoreConnessioni gestoreConnessioni) {
        this.codice = codice;
        this.id = id;
        this.nome = nome;
        this.maxGiocatori = maxGiocatori;
        this.gestoreConnessioni = gestoreConnessioni;
    }

    public boolean aggiungiUtente(String token, SocketIOClient client) {
        if (partitaInCorso || giocatori.size() >= maxGiocatori) return false;
        if (giocatori.containsKey(token)) return true;

        Utente utente = gestoreConnessioni.getUtenteByToken(token);
        String username = utente != null ? utente.getUsername() : "anonimo";

        Giocatore g = null;//new Giocatore(username, token, client);
        giocatori.put(token, g);
        clientPerToken.put(token, client);

//        if (utente != null) utente.setGiocatore(g);
        throw new UnsupportedOperationException("Not implemented yet");

        //return true;
    }

    public void rimuoviUtente(String token) {
        giocatori.remove(token);
        clientPerToken.remove(token);
    }

    public boolean isVuota() {
        return giocatori.isEmpty();
    }

    public boolean isPiena() {
        return giocatori.size() >= maxGiocatori;
    }

    public void avviaPartita() {
        if (partitaInCorso || !isPiena()) return;

        List<Giocatore> lista = new ArrayList<>(giocatori.values());
        this.partita = new PartitaNET(lista);
        this.partitaInCorso = true;

        broadcast("UNO_PARTITA_INIZIATA", partita.topCard());

        for (Giocatore g : lista) {
            //g.getClient().sendEvent("UNO_MANO_INIZIALE", g.getMano().toDTO());
        }

        inviaTurnoCorrente();
    }

    public void riceviMossa(String token, MossaDTO mossa) {
        if (partita == null || !giocatori.containsKey(token)) return;
        Giocatore g = giocatori.get(token);
        partita.effettuaMossa(mossa, g);

        if (partita.isFinished()) {
            broadcast("UNO_PARTITA_FINITA", g.getNome());
            partitaInCorso = false;
        } else {
            inviaTurnoCorrente();
        }
    }

    private void inviaTurnoCorrente() {
        if (partita == null) return;
        Giocatore g = partita.getGiocatoreCorrente();
        //g.getClient().sendEvent("UNO_TOCCA_A_TE", partita.topCard());
    }

    public void broadcast(String evento, Object payload) {
        for (SocketIOClient client : clientPerToken.values()) {
            client.sendEvent(evento, payload);
        }
    }

    public int getCodice() {
        return codice;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isPartitaInCorso() {
        return partitaInCorso;
    }

    public Set<String> getTokenUtenti() {
        return giocatori.keySet();
    }

    public PartitaNET getPartita() {
		return partita;
	}
}
