package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.net.messaggi.Messaggi;
import onegame.net.messaggi.MessaggiGioco;
import onegame.net.messaggi.MessaggiSalvataggioPartite;
import onegame.net.messaggi.MessaggiStatistiche;
import onegame.net.messaggi.MessaggiUtente;
import onegame.server.db.GestoreDatabase;

/**
 * Server principale per il gioco Uno. Gestisce le connessioni, le sessioni e instrada gli eventi ai gestori
 * appropriati.
 */
public class ServerUno {

	private final SocketIOServer server;
	private final GestoreConnessioni gestoreConnessioni;
	private final GestoreStanzePartita gestoreStanze;
	private final GestorePartiteOffline gestorePartiteOffline;
	private final GestoreSessioni gestoreSessioni;
	private final GestoreUtenti gestoreUtenti;
	private final GestoreStatistiche gestoreStatistiche;

	private static final Logger logger = LoggerFactory.getLogger(ServerUno.class);

	public ServerUno(String host, int port) {
		Configuration config = new Configuration();
		config.setHostname(host);
		config.setPort(port);
		config.setPingInterval(10000);
		config.setPingTimeout(60000);
		config.setExceptionListener(new ServerUnoExceptionListener());

		this.server = new SocketIOServer(config);
		this.gestoreSessioni = new GestoreSessioni();
		this.gestoreConnessioni = new GestoreConnessioni(gestoreSessioni);
		this.gestoreStanze = new GestoreStanzePartita();
		this.gestorePartiteOffline = new GestorePartiteOffline();
		this.gestoreUtenti = new GestoreUtenti(gestoreSessioni);
		this.gestoreStatistiche = new GestoreStatistiche();

		this.gestoreSessioni.addObserver(gestoreStanze);

		registraEventi();
	}

	/**
	 * Registra gli eventi di connessione, disconnessione e messaggi vari
	 */
	private void registraEventi() {
		// Connessione
		server.addConnectListener(client -> gestoreConnessioni.handleConnessione(client));

		// Disconnessione
		server.addDisconnectListener(client -> gestoreConnessioni.handleDisconnessione(client));

		// Autenticazione
		server.addEventListener(Messaggi.EVENT_AUTH_LOGIN, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleLogin(client, reqAuth, ack));
		server.addEventListener(Messaggi.EVENT_AUTH_REGISTER, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleRegister(client, reqAuth, ack));
		server.addEventListener(Messaggi.EVENT_AUTH_ANONIMO, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleAnonimo(client, reqAuth, ack));

		// Stanze
		server.addEventListener(Messaggi.EVENT_STANZA_CREA, String.class,
				(client, data, ack) -> gestoreStanze.handleCreaStanza(getSessione(client), data, ack));
		server.addEventListener(Messaggi.EVENT_STANZA_ENTRA, String.class,
				(client, data, ack) -> gestoreStanze.handleEntraStanza(getSessione(client), data, ack));
		server.addEventListener(Messaggi.EVENT_STANZA_DETTAGLI, String.class,
				(client, data, ack) -> gestoreStanze.handleDettagliStanza(getSessione(client), ack));
		server.addEventListener(Messaggi.EVENT_STANZA_ESCI, Void.class,
				(client, data, ack) -> gestoreStanze.handleAbbandonaStanza(getSessione(client), ack));

		// Salvataggi partite offline
		server.addEventListener(MessaggiSalvataggioPartite.EVENT_SALVA_SALVATAGGIO, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleSalvaPartita(getSessione(client), data, ack));
		server.addEventListener(MessaggiSalvataggioPartite.EVENT_CARICA_SALVATAGGIO, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleCaricaPartita(getSessione(client), data, ack));
		server.addEventListener(MessaggiSalvataggioPartite.EVENT_LISTA_SALVATAGGI, Void.class,
				(client, data, ack) -> gestorePartiteOffline.handleListaSalvataggi(getSessione(client), ack));
		server.addEventListener(MessaggiSalvataggioPartite.EVENT_ELIMINA_SALVATAGGIO, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleEliminaSalvataggio(getSessione(client), data, ack));
		server.addEventListener(MessaggiSalvataggioPartite.EVENT_RINOMINA_SALVATAGGIO, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleRinominaSalvataggio(getSessione(client), data, ack));

		// Utente
		server.addEventListener(MessaggiUtente.EVENT_CAMBIO_USERNAME, String.class,
				(client, data, ack) -> gestoreUtenti.handleCambioUsername(getSessione(client), data, ack));
		server.addEventListener(MessaggiUtente.EVENT_CAMBIO_PASSWORD, String.class,
				(client, data, ack) -> gestoreUtenti.handleCambioPassword(getSessione(client), data, ack));
		server.addEventListener(MessaggiUtente.EVENT_ELIMINA_ACCOUNT, String.class,
				(client, data, ack) -> gestoreUtenti.handleEliminaAccount(getSessione(client), data, ack));

		// Gioco
		server.addEventListener(MessaggiGioco.EVENT_INIZIA_PARTITA, String.class,
				(client, data, ack) -> gestoreStanze.handleIniziaPartita(getSessione(client), ack));
		server.addEventListener(MessaggiGioco.EVENT_EFFETTUA_MOSSA_PARTITA, String.class,
				(client, data, ack) -> gestoreStanze.handleEffettuaMossa(getSessione(client), data, ack));

		// Statistiche
		server.addEventListener(MessaggiStatistiche.EVENT_CARICA_STATISTICHE, Void.class,
				(client, data, ack) -> gestoreStatistiche.handleCaricaStatistiche(getSessione(client), ack));
	}

	/**
	 * Avvia il server
	 */
	public void avvia() {
		server.start();
		logger.info("Avviato su {}:{}", server.getConfiguration().getHostname(), server.getConfiguration().getPort());
	}

	/**
	 * Recupera la sessione associata al client, aggiornandone il ping
	 * @param client Il client Socket.IO
	 * @return La sessione associata
	 * @throws IllegalStateException Se la sessione non viene trovata
	 */
	private Sessione getSessione(SocketIOClient client) {
		Sessione s = gestoreSessioni.getSessione(client.get("token"));
		if (s == null) {
			logger.warn("Sessione non trovata per client con token {}", client.get("token").toString());
			throw new IllegalStateException("Sessione non trovata");
		}
		s.aggiornaPing();
		return s;
	}

	public void stop() {
		server.stop();
		logger.info("Arrestato.");
	}

	public static void main(String[] args) {
		try {
			GestoreDatabase.inizializzaDatabase();
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.exit(1);
		}
		String host = "0.0.0.0";
		String port = System.getenv("PORT");
		int portNumber = port != null ? Integer.parseInt(port) : 8080;
		ServerUno srv = new ServerUno(host, portNumber);
		srv.avvia();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Arresto in corso...");
			srv.stop();
		}));
	}
}
