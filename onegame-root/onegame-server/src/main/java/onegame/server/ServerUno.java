package onegame.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.net.ProtocolloMessaggi;
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
		this.gestoreStanze = new GestoreStanzePartita(gestoreSessioni);
		this.gestorePartiteOffline = new GestorePartiteOffline(gestoreSessioni);
		this.gestoreGioco = new GestoreGioco(gestoreStanze, gestoreSessioni);

		this.gestoreSessioni.aggiungiObserver(gestoreStanze);

		registraEventi();
	}

	private void registraEventi() {
		// connessione
		server.addConnectListener(client -> gestoreConnessioni.handleConnessione(client));

		// disconnessione
		server.addDisconnectListener(client -> gestoreConnessioni.handleDisconnessione(client));

		// auth:login
		server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_LOGIN, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleLogin(client, reqAuth, ack));

		// auth:register
		server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_REGISTER, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleRegister(client, reqAuth, ack));

		// auth:anonimo
		server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_ANONIMO, String.class,
				(client, reqAuth, ack) -> gestoreConnessioni.handleAnonimo(client, ack));

		// stanza:crea
		server.addEventListener(ProtocolloMessaggi.EVENT_STANZA_CREA, String.class,
				(client, data, ack) -> gestoreStanze.handleCreaStanza(client, data, ack));

		// stanza:entra
		server.addEventListener(ProtocolloMessaggi.EVENT_STANZA_ENTRA, String.class,
				(client, data, ack) -> gestoreStanze.handleEntraStanza(client, data, ack));

		// richiesta partite non concluse
//		server.addEventListener(ProtocolloMessaggi.EVENT_RICHIESTA_PARTITE_NON_CONCLUSE, Void.class,
//				(client, data, ack) -> gestoreConnessioni.handleRichiestaPartiteNonConcluse(client));

		// evento partita:mossa -> payload: Mossa
		server.addEventListener(ProtocolloMessaggi.EVENT_GIOCO_MOSSA, String.class, (client, mossa, ack) -> {
			// TO DO
			Object tokenObj = client.get("token");
			if (tokenObj == null) {
				client.sendEvent("partita:invalid", "Non autenticato");
				return;
			}
			String token = tokenObj.toString();
			// recupero stanza da gestoreStanze tramite mapping interno
			StanzaPartita stanza = gestoreStanze.getStanzaPerToken(token);
			if (stanza == null) {
				client.sendEvent("partita:invalid", "Non sei in alcuna stanza");
				return;
			}
			// stanza.riceviMossa(token, mossa);
		});

		server.addEventListener(ProtocolloMessaggi.EVENT_SALVA_PARTITA, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleSalvaPartita(client, data, ack));

		server.addEventListener(ProtocolloMessaggi.EVENT_CARICA_PARTITA, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleCaricaPartita(client, data, ack));

		server.addEventListener(ProtocolloMessaggi.EVENT_LISTA_PARTITE, Void.class,
				(client, data, ack) -> gestorePartiteOffline.handleListaSalvataggi(client, ack));

		server.addEventListener(ProtocolloMessaggi.EVENT_ELIMINA_PARTITA, String.class,
				(client, data, ack) -> gestorePartiteOffline.handleEliminaSalvataggio(client, data, ack));

		server.addEventListener(ProtocolloMessaggi.EVENT_GIOCO_MOSSA, String.class,
				(client, data, ack) -> gestoreGioco.handleEffettuaMossa(client, data, ack));

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
