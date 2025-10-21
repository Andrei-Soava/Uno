package onegame.client.net;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import onegame.client.controllore.online.ClientSocketObserver;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.StatoStanzaDTO;
import onegame.modello.net.messaggi.MessaggiSalvataggiPartite;
import onegame.modello.net.messaggi.MessaggiSalvataggiPartite.*;
import onegame.modello.net.messaggi.Messaggi;
import onegame.modello.net.messaggi.Messaggi.*;
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
	private ClientSocketObserver observer;

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
	 * Ottieni observer dal socket
	 * @return observer del socket
	 */
	public ClientSocketObserver getObserver() {
		return observer;
	}

	/**
	 * Imposta observer del socket
	 * @param observer
	 */
	public void setObserver(ClientSocketObserver observer) {
		this.observer = observer;
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

		// socket.on("stanza:aggiornamento", args -> System.out.println("[server][stanza:aggiornamento] " + args[0]));
		socket.on("partita:inizia", args -> System.out.println("[server][partita:inizia] " + args[0]));
		socket.on("partita:turno", args -> System.out.println("[server][partita:turno] " + args[0]));
		socket.on("partita:mossa", args -> System.out.println("[server][partita:mossa] " + args[0]));
		socket.on("partita:terminata", args -> System.out.println("[server][partita:terminata] " + args[0]));
		socket.on(Messaggi.EVENT_STANZA_AGGIORNAMENTO, (args -> {
			StatoStanzaDTO stato = getPayload(StatoStanzaDTO.class, args);
			if (observer != null) {
				observer.aggiornaStanza(stato);
			}
		}));
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
	public void anonimo(String nickname, Callback<RespAuth> callback) {
		ReqAuthAnonimo req = new ReqAuthAnonimo(nickname);
		System.out.println("[CLIENT] Invio richiesta login anonimo con nickname: " + nickname);
		socket.emit(Messaggi.EVENT_AUTH_ANONIMO, JsonHelper.toJson(req), (Ack) args -> {
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

	public void esciStanza(Callback<RespAbbandonaStanza> callback) {
		System.out.println("[CLIENT] Invio richiesta uscita dalla stanza");

		socketEmitEvent(Messaggi.EVENT_STANZA_ESCI, null, callback, RespAbbandonaStanza.class);
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

	public void listaPartite(Callback<RespListaSalvataggi> callback) {
		System.out.println("[CLIENT] Invio richiesta lista partite salvate");

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_LISTA_SALVATAGGI, null, callback, RespListaSalvataggi.class);
	}

	public void salvaPartita(String nomeSalvataggio, String partitaSerializzata,
			Callback<RespCreaSalvataggio> callback) {
		ReqCreaSalvataggio req = new ReqCreaSalvataggio(nomeSalvataggio, partitaSerializzata);
		System.out.println("[CLIENT] Invio richiesta salvataggio partita: " + nomeSalvataggio);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_CREA_SALVATAGGIO, req, callback, RespCreaSalvataggio.class);
	}

	public void caricaPartita(String nomeSalvataggio, Callback<RespCaricaSalvataggio> callback) {
		ReqCaricaSalvataggio req = new ReqCaricaSalvataggio(nomeSalvataggio);
		System.out.println("[CLIENT] Invio richiesta caricamento partita: " + nomeSalvataggio);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_CARICA_SALVATAGGIO, req, callback, RespCaricaSalvataggio.class);
	}

	public void eliminaPartita(String nomeSalvataggio, Callback<RespEliminaSalvataggio> callback) {
		ReqEliminaSalvataggio req = new ReqEliminaSalvataggio(nomeSalvataggio);
		System.out.println("[CLIENT] Invio richiesta eliminazione partita: " + nomeSalvataggio);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_ELIMINA_SALVATAGGIO, req, callback,
				RespEliminaSalvataggio.class);
	}

	public void rinominaPartita(String vecchioNome, String nuovoNome, Callback<RespRinominaSalvataggio> callback) {
		ReqRinominaSalvataggio req = new ReqRinominaSalvataggio(vecchioNome, nuovoNome);
		System.out.println("[CLIENT] Invio richiesta rinomina partita: " + vecchioNome + " -> " + nuovoNome);

		socketEmitEvent(MessaggiSalvataggiPartite.EVENT_RINOMINA_SALVATAGGIO, req, callback,
				RespRinominaSalvataggio.class);
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
		socket.emit(evento, JsonHelper.toJson(req), (Ack) args -> {
			T resp = getPayload(clazz, args);

			if (callback != null) {
				callback.call(resp);
			}
		});
	}
}
