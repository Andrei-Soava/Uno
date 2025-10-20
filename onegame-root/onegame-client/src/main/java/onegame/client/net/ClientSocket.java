package onegame.client.net;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.messaggi.MessaggiSalvataggiPartite;
import onegame.modello.net.messaggi.Messaggi;
import onegame.modello.net.messaggi.Messaggi.ReqAuth;
import onegame.modello.net.messaggi.Messaggi.ReqCreaStanza;
import onegame.modello.net.messaggi.Messaggi.ReqEffettuaMossa;
import onegame.modello.net.messaggi.Messaggi.ReqEntraStanza;
import onegame.modello.net.messaggi.Messaggi.RespAuth;
import onegame.modello.net.messaggi.Messaggi.RespCreaStanza;
import onegame.modello.net.messaggi.Messaggi.RespDettagliStanza;
import onegame.modello.net.messaggi.Messaggi.RespEffettuaMossa;
import onegame.modello.net.messaggi.Messaggi.RespEntraStanza;
import onegame.modello.net.util.Callback;
import onegame.modello.net.util.JsonHelper;

/**
 * ClientSocket
 * - gestisce la connessione al server Socket.IO
 * - mantiene il token di autenticazione
 * - fornisce metodi per le operazioni principali (login, registrazione, creazione stanza, ecc.)
 * - registra handler per gli eventi
 */
public class ClientSocket {

	private final Socket socket;
	private String token;
	private Utente utente;

	public ClientSocket(String url) throws Exception {
		IO.Options opts = new IO.Options();
		opts.reconnection = true;
		this.socket = IO.socket(new URI(url), opts);
		this.utente = new Utente(true);
		registerBaseHandlers();
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

	/**
	 * Registra gli handler di base
	 */
	private void registerBaseHandlers() {
		socket.on(Socket.EVENT_CONNECT, args -> {
			System.out.println("[client] connesso al server");
			// se abbiamo token, notifichiamo il server (se implementa un handler "auth:setToken")
			if (token != null) {
				try {
					socket.emit("auth:setToken", JsonHelper.toJson(token));
				} catch (Exception e) {
					/* ignore */ }
			}
		});

		socket.on(Socket.EVENT_DISCONNECT, args -> System.out.println("[client] disconnesso dal server"));
		socket.on("connect_error", args -> System.out.println("[client] errore connessione: " + args[0]));

		socket.on("stanza:aggiornamento", args -> System.out.println("[server][stanza:aggiornamento] " + args[0]));
		socket.on("partita:inizia", args -> System.out.println("[server][partita:inizia] " + args[0]));
		socket.on("partita:turno", args -> System.out.println("[server][partita:turno] " + args[0]));
		socket.on("partita:mossa", args -> System.out.println("[server][partita:mossa] " + args[0]));
		socket.on("partita:terminata", args -> System.out.println("[server][partita:terminata] " + args[0]));
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
		// se non è connesso, prova a connettersi e attendi max 1 secondo TODO da rivedere (fare solo il primo if non è
		// pratico visto che causa eccezioni socket.connect è asincrono))
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

//    /**
//     * Invia il token di autenticazione al server
//     * @param token Il token di autenticazione
//     */
//    @Deprecated
//    public void setToken(String token) {
//        this.token = token;
//        try {
//            socket.emit("auth:setToken", token);
//        } catch (Exception e) { }
//    }

	public void setToken(String token) {
		this.token = token;
	}

	public void register(String username, String password, Callback<RespAuth> callback) {
		ReqAuth req = new ReqAuth(username, password);
		System.out.println("[CLIENT] Invio richiesta registrazione di " + username);
		socket.emit(Messaggi.EVENT_AUTH_REGISTER, JsonHelper.toJson(req), (Ack) args -> {
			RespAuth auth = getPayload(RespAuth.class, args);

			setToken(auth.token);
			if (callback != null)
				callback.call(auth);
		});

	}

	public void login(String username, String password, Callback<RespAuth> callback) {
		ReqAuth req = new ReqAuth(username, password);
		System.out.println("[CLIENT] Invio richiesta login di " + username);
		socket.emit(Messaggi.EVENT_AUTH_LOGIN, JsonHelper.toJson(req), (Ack) args -> {
			RespAuth auth = getPayload(RespAuth.class, args);

			setToken(auth.token);
			if (callback != null)
				callback.call(auth);
		});
	}

	/**
	 * Invia la richiesta di login anonimo al server
	 * @param callback Callback per la risposta del server
	 */
	public void anonimo(Callback<RespAuth> callback) {
		System.out.println("[CLIENT] Invio richiesta login anonimo");
		socket.emit(Messaggi.EVENT_AUTH_ANONIMO, "", (Ack) args -> {
			RespAuth auth = getPayload(RespAuth.class, args);

			setToken(auth.token);
			if (callback != null)
				callback.call(auth);
		});
	}

	/**
	 * Invia la richiesta di creazione di una nuova stanza
	 * @param nome Nome della stanza
	 * @param maxGiocatori Numero massimo di giocatori
	 * @param callback Callback per la risposta del server
	 * @throws Exception
	 */
	public void creaStanza(String nome, int maxGiocatori, Callback<RespCreaStanza> callback) {
		ReqCreaStanza req = new ReqCreaStanza(nome, maxGiocatori);
		System.out.println(
				"[CLIENT] Invio richiesta creazione stanza: " + nome + " (max giocatori: " + maxGiocatori + ")");
//		socket.emit(ProtocolloMessaggi.EVENT_STANZA_CREA, JsonHelper.toJson(req), (Ack) args -> {
//			RespCreaStanza resp = getPayload(RespCreaStanza.class, args);
//			
//			if (callback != null) {
//				callback.call(resp);
//			}
//		});

		socketEmitEvent(Messaggi.EVENT_STANZA_CREA, req, callback, RespCreaStanza.class);
	}

	/**
	 * Invia la richiesta di ingresso in una stanza
	 * @param idStanza ID della stanza
	 * @param callback Callback per la risposta del server
	 * @throws Exception
	 */
	public void entraStanza(int codice, Callback<RespEntraStanza> callback) {
		ReqEntraStanza req = new ReqEntraStanza(codice);
		System.out.println("[CLIENT] Invio richiesta ingresso in stanza: " + codice);

		socketEmitEvent(Messaggi.EVENT_STANZA_ENTRA, req, callback, RespEntraStanza.class);
	}
	
	public void dettagliStanza(Callback<RespDettagliStanza> callback) {
		System.out.println("[CLIENT] Invio richiesta dettagli stanza");

		socketEmitEvent(Messaggi.EVENT_STANZA_DETTAGLI, null, callback, RespDettagliStanza.class);
	}

	public void inviaMossa(MossaDTO mossa, Callback<RespEffettuaMossa> callback) {
		ReqEffettuaMossa req = new ReqEffettuaMossa(mossa);
		System.out.println("[CLIENT] Invio richiesta mossa: " + mossa);

		socketEmitEvent(Messaggi.EVENT_GIOCO_MOSSA, req, callback, RespEffettuaMossa.class);
	}

	public void listaPartite(Callback<MessaggiSalvataggiPartite.RespListaSalvataggi> callback) {
		System.out.println("[CLIENT] Invio richiesta lista partite salvate");
//		socket.emit(ProtocolloMessaggi.EVENT_LISTA_PARTITE, null, (Ack) callback);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_LISTA_SALVATAGGI, null, callback, MessaggiSalvataggiPartite.RespListaSalvataggi.class);
	}

	public void salvaPartita(String nomeSalvataggio, String partitaSerializzata, Callback<MessaggiSalvataggiPartite.RespCreaSalvataggio> callback) {
		MessaggiSalvataggiPartite.ReqCreaSalvataggio req = new MessaggiSalvataggiPartite.ReqCreaSalvataggio(nomeSalvataggio, partitaSerializzata);
		System.out.println("[CLIENT] Invio richiesta salvataggio partita: " + nomeSalvataggio);
//		socket.emit(ProtocolloMessaggi.EVENT_SALVA_PARTITA, JsonHelper.toJson(req), (Ack) callback);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_CREA_SALVATAGGIO, req, callback, MessaggiSalvataggiPartite.RespCreaSalvataggio.class);
	}

	public void caricaPartita(String nomeSalvataggio, Callback<MessaggiSalvataggiPartite.RespCaricaSalvataggio> callback) {
		MessaggiSalvataggiPartite.ReqCaricaSalvataggio req = new MessaggiSalvataggiPartite.ReqCaricaSalvataggio(nomeSalvataggio);
		System.out.println("[CLIENT] Invio richiesta caricamento partita: " + nomeSalvataggio);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_CARICA_SALVATAGGIO, req, callback, MessaggiSalvataggiPartite.RespCaricaSalvataggio.class);
	}

	public void eliminaPartita(String nomeSalvataggio, Callback<MessaggiSalvataggiPartite.RespEliminaSalvataggio> callback) {
		MessaggiSalvataggiPartite.ReqEliminaSalvataggio req = new MessaggiSalvataggiPartite.ReqEliminaSalvataggio(nomeSalvataggio);
		System.out.println("[CLIENT] Invio richiesta eliminazione partita: " + nomeSalvataggio);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_ELIMINA_SALVATAGGIO, req, callback, MessaggiSalvataggiPartite.RespEliminaSalvataggio.class);
	}
	
	public void rinominaPartita(String vecchioNome, String nuovoNome, Callback<MessaggiSalvataggiPartite.RespRinominaSalvataggio> callback) {
		MessaggiSalvataggiPartite.ReqRinominaSalvataggio req = new MessaggiSalvataggiPartite.ReqRinominaSalvataggio(vecchioNome, nuovoNome);
		System.out.println("[CLIENT] Invio richiesta rinomina partita: " + vecchioNome + " -> " + nuovoNome);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_RINOMINA_SALVATAGGIO, req, callback,
				MessaggiSalvataggiPartite.RespRinominaSalvataggio.class);
	}

	/**
	 * Registra un nuovo handler
	 * @param evento Nome dell'evento
	 * @param handler Handler dell'evento
	 */
	public void on(String evento, Emitter.Listener handler) {
		socket.on(evento, handler);
	}

	public static <T> T getPayload(Class<T> clazz, Object... args) {
		if (args == null || args.length == 0 || args[0] == null) {
//			throw new IllegalArgumentException("Nessun argomento valido fornito");
			return null;
		}

		String json = args[0].toString();
		return JsonHelper.fromJson(json, clazz);
	}

	private <T> void socketEmitEvent(String evento, Object req, Callback<T> callback, Class<T> clazz) {
		System.out.println(req.getClass());
		socket.emit(evento, JsonHelper.toJson(req), (Ack) args -> {
			T resp = getPayload(clazz, args);

			if (callback != null) {
				callback.call(resp);
			}
		});
	}
}
