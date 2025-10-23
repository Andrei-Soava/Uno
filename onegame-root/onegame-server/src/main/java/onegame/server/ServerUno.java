package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.net.messaggi.MessaggiSalvataggioPartite;
import onegame.modello.net.messaggi.MessaggiUtente;
import onegame.modello.net.messaggi.Messaggi;
import onegame.server.db.GestoreDatabase;

/**
 * ServerUno - registra eventi di connessione/disconnessione - instrada eventi
 * di autenticazione e gestione stanze verso i gestori - instrada eventi di
 * gioco ("partita:mossa") verso la StanzaPartita corretta
 */
public class ServerUno {

	private final SocketIOServer server;
	private final GestoreConnessioni gestoreConnessioni;
	private final GestoreStanzePartita gestoreStanze;
	private final GestorePartiteOffline gestorePartiteOffline;
	private final GestoreGioco gestoreGioco;
	private final GestoreSessioni gestoreSessioni;
	private final GestoreUtenti gestoreUtenti;

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
		this.gestoreGioco = new GestoreGioco(gestoreStanze);
		this.gestoreUtenti = new GestoreUtenti(gestoreSessioni);

		this.gestoreSessioni.addObserver(gestoreStanze);

		registraEventi();
	}

	private void registraEventi() {
		// connessione
		server.addConnectListener(client -> gestoreConnessioni.handleConnessione(client));

		// disconnessione
		server.addDisconnectListener(client -> gestoreConnessioni.handleDisconnessione(client));

		// auth:login
		server.addEventListener(Messaggi.EVENT_AUTH_LOGIN, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleLogin(client, reqAuth, ack));

		// auth:register
		server.addEventListener(Messaggi.EVENT_AUTH_REGISTER, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleRegister(client, reqAuth, ack));

		// auth:anonimo
		server.addEventListener(Messaggi.EVENT_AUTH_ANONIMO, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleAnonimo(client, reqAuth, ack));

		// stanza:crea
		server.addEventListener(Messaggi.EVENT_STANZA_CREA, String.class,
				(client, data, ack) -> gestoreStanze.handleCreaStanza(getSessione(client), data, ack));

		// stanza:entra
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

		server.addEventListener(MessaggiUtente.EVENT_CAMBIO_USERNAME, String.class,
				(client, data, ack) -> gestoreUtenti.handleCambioUsername(getSessione(client), data, ack));

		server.addEventListener(MessaggiUtente.EVENT_CAMBIO_PASSWORD, String.class,
				(client, data, ack) -> gestoreUtenti.handleCambioPassword(getSessione(client), data, ack));

		server.addEventListener(MessaggiUtente.EVENT_ELIMINA_ACCOUNT, String.class,
				(client, data, ack) -> gestoreUtenti.handleEliminaAccount(getSessione(client), data, ack));

		server.addEventListener(Messaggi.EVENT_INIZIA_PARTITA, String.class,
				(client, data, ack) -> gestoreGioco.handleIniziaPartita(getSessione(client), ack));

		server.addEventListener(Messaggi.EVENT_EFFETTUA_MOSSA_PARTITA, String.class,
				(client, data, ack) -> gestoreGioco.handleEffettuaMossa(getSessione(client), data, ack));

		// Test
		server.addEventListener("test", String.class, (client, data, ack) -> {
			boolean tmp = ServerUno.testClient == client;
			logger.debug(tmp ? "True" : "False");
			ServerUno.testClient = client;
		});

		logger.debug("Eventi registrati");
	}

	// Test
	private static SocketIOClient testClient;

	public void avvia() {
		server.start();
		logger.info("Avviato su {}:{}", server.getConfiguration().getHostname(), server.getConfiguration().getPort());
	}

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
		String host = "127.0.0.1";
		int port = 8080;
		ServerUno srv = new ServerUno(host, port);
		srv.avvia();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Arresto in corso...");
			srv.stop();
		}));
	}
}
