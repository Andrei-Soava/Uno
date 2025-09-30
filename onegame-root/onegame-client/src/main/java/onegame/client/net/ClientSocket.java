package onegame.client.net;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import onegame.modello.net.ProtocolloMessaggi.ReqAuth;
import onegame.modello.net.ProtocolloMessaggi.ReqCreaStanza;
import onegame.modello.net.ProtocolloMessaggi.ReqEntraStanza;
import onegame.modello.net.ProtocolloMessaggi.RespAuthOk;
import onegame.modello.net.Utente;

/**
 * ClientSocket
 * - gestisce la connessione al server Socket.IO
 * - mantiene il token di autenticazione
 * - fornisce metodi per le operazioni principali (login, registrazione, creazione stanza, ecc.)
 * - registra handler per gli eventi
 */
public class ClientSocket {

    private final Socket socket;
    private final ObjectMapper mapper = new ObjectMapper();
    private String token;
    private Utente utente;

    public ClientSocket(String url) throws Exception {
        IO.Options opts = new IO.Options();
        opts.reconnection = true;
        this.socket = IO.socket(new URI(url), opts);
        this.utente=new Utente(true);
        registerBaseHandlers();
    }

    /**
     * Registra gli handler di base
     */
    private void registerBaseHandlers() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("[client] connesso al server");
            // se abbiamo token, notifichiamo il server (se implementa un handler "auth:setToken")
            if (token != null) {
                try {
                    socket.emit("auth:setToken", mapper.writeValueAsString(token));
                } catch (Exception e) { /* ignore */ }
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> System.out.println("[client] disconnesso dal server"));
        socket.on("connect_error", args -> System.out.println("[client] errore connessione: " + args[0]));

        socket.on("stanza:aggiornamento", args -> System.out.println("[server][stanza:aggiornamento] " + args[0]));
        socket.on("partita:inizia", args -> System.out.println("[server][partita:inizia] " + args[0]));
        socket.on("partita:turno", args -> System.out.println("[server][partita:turno] " + args[0]));
        socket.on("partita:mossa", args -> System.out.println("[server][partita:mossa] " + args[0]));
        socket.on("partita:terminata", args -> System.out.println("[server][partita:terminata] " + args[0]));
        socket.on("auth:ok", args -> handleAuthOk(args[0]));
        socket.on("auth:fail", args -> System.out.println("[server][auth:fail] " + args[0]));
        socket.on("stanza:ok", args -> System.out.println("[server][stanza:ok] " + args[0]));
        socket.on("stanza:fail", args -> System.out.println("[server][stanza:fail] " + args[0]));
    }

    private void handleAuthOk(Object payload) {
        try {
            RespAuthOk resp = mapper.convertValue(payload, RespAuthOk.class);
            if (resp != null && resp.token != null) {
                setToken(resp.token);
                System.out.println("[server][auth:ok] token memorizzato: " + resp.token);
            } else {
                System.out.println("[server][auth:ok] " + payload);
            }
        } catch (Exception e) {
            System.out.println("[server][auth:ok] (impossibile parsare payload) " + payload);
        }
    }

    /**
     * Avvia la connessione al server
     */
    public void connect() {
        socket.connect();
    }
    
    /**
     * Verifica se il client è attualmente connesso al server
     * @return true se connesso, false altrimenti
     */
    public boolean isConnected() {
        if (socket != null && socket.connected()) {
            return true;
        }
        // se non è connesso, prova a connettersi e attendi max 1 secondo
        // TODO da rivedere (fare solo il primo if non è pratico visto che causa eccezioni socket.connect è asincrono))
        try {
            socket.connect();
            return waitForConnect(0); // attende 0 secondi
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
	 * Disconnette dal server
	 */
    public void disconnect() {
        socket.disconnect();
    }

    /**
	 * Attende la connessione al server
	 * @param seconds Numero di secondi da attendere
	 * @return true se connesso, false se timeout
	 * @throws InterruptedException
	 */
    public boolean waitForConnect(long seconds) throws InterruptedException {
        CountDownLatch l = new CountDownLatch(1);
        Emitter.Listener lconn = args -> l.countDown();
        socket.once(Socket.EVENT_CONNECT, lconn);
        return l.await(seconds, TimeUnit.SECONDS);
    }

    /**
     * Invia il token di autenticazione al server
     * @param token Il token di autenticazione
     */
    public void setToken(String token) {
        this.token = token;
        try {
            socket.emit("auth:setToken", token);
        } catch (Exception e) { }
    }

    /**
     * Invia la richiesta di login al server
     * @param username Username dell'utente
     * @param password Password dell'utente
     * @param callback Callback per la risposta del server
     * @throws Exception
     */
    public void login(String username, String password, Ack callback) throws Exception {
        ReqAuth r = new ReqAuth(username, password);
        String json = mapper.writeValueAsString(r);
        socket.emit("auth:login", json, callback);
    }

    /**
     * Invia la richiesta di registrazione al server
     * @param username Username dell'utente
     * @param password Password dell'utente
     * @param callback Callback per la risposta del server
     * @throws Exception
     */
    public void register(String username, String password, Ack callback) throws Exception {
        ReqAuth r = new ReqAuth(username, password);
        String json = mapper.writeValueAsString(r);
        socket.emit("auth:register", json, callback);
    }

    /**
     * Invia la richiesta di login anonimo al server
     * @param callback Callback per la risposta del server
     */
    public void anonimo(Ack callback) {
        socket.emit("auth:anonimo", null, callback);
    }

    /**
	 * Invia la richiesta di creazione di una nuova stanza
	 * @param nome Nome della stanza
	 * @param maxGiocatori Numero massimo di giocatori
	 * @param callback Callback per la risposta del server
	 * @throws Exception
	 */
    public void creaStanza(String nome, int maxGiocatori, Ack callback) throws Exception {
        ReqCreaStanza r = new ReqCreaStanza(nome, maxGiocatori);
        String json = mapper.writeValueAsString(r);
        socket.emit("stanza:crea", json, callback);
    }

    /**
	 * Invia la richiesta di ingresso in una stanza
	 * @param idStanza ID della stanza
	 * @param callback Callback per la risposta del server
	 * @throws Exception
	 */
    public void entraStanza(String idStanza, Ack callback) throws Exception {
        ReqEntraStanza r = new ReqEntraStanza(idStanza);
        String json = mapper.writeValueAsString(r);
        socket.emit("stanza:entra", json, callback);
    }

    /**
	 * Invia una mossa al server
	 * @param mossaObj La mossa da inviare
	 * @param callback Callback per la risposta del server
	 */
    public void inviaMossa(Object mossaObj, Ack callback) {
        try {
            if (mossaObj instanceof String) {
                socket.emit("partita:mossa", (String) mossaObj, callback);
            } else {
                String json = mapper.writeValueAsString(mossaObj);
                socket.emit("partita:mossa", json, callback);
            }
        } catch (Exception e) {
            if (callback != null) callback.call(e.getMessage());
        }
    }

    /**
	 * Registra un nuovo handler
	 * @param evento Nome dell'evento
	 * @param handler Handler dell'evento
	 */
    public void on(String evento, Emitter.Listener handler) {
        socket.on(evento, handler);
    }

    /**
     * Ottieni utente dal socket
     * @return utente del socket
     */
	public Utente getUtente() {
		return this.utente;
	}

	/**
	 * Imposta utente del socket (potrebbe servire)
	 * @param utente
	 */
	public void setUtente(Utente utente) {
		this.utente = utente;
	}
    
    
}
