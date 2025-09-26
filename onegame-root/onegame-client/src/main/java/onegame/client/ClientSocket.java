package onegame.client;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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

    public ClientSocket(String url) throws Exception {
        IO.Options opts = new IO.Options();
        opts.reconnection = true;
        this.socket = IO.socket(new URI(url), opts);
        registerBaseHandlers();
    }

    /**
     * Registra gli handler di base
     */
    private void registerBaseHandlers() {
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
        MessaggiClient.ReqAuth r = new MessaggiClient.ReqAuth(username, password);
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
        MessaggiClient.ReqAuth r = new MessaggiClient.ReqAuth(username, password);
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
        MessaggiClient.ReqCreaStanza r = new MessaggiClient.ReqCreaStanza(nome, maxGiocatori);
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
        MessaggiClient.ReqEntraStanza r = new MessaggiClient.ReqEntraStanza(idStanza);
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
}
