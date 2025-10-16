package onegame.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.Utente;
import onegame.server.db.GestoreDatabase;

/**
 * ServerUno - registra eventi di connessione/disconnessione - instrada eventi
 * di autenticazione e gestione stanze verso i gestori - instrada eventi di
 * gioco ("partita:mossa") verso la StanzaPartita corretta
 */
public class ServerUno {

	private final SocketIOServer server;
	private final GestoreConnessioni gestoreConnessioni;
	private final GestoreStanze gestoreStanze;
	private final GestorePartiteOffline gestorePartiteOffline;
	private final GestoreGioco gestoreGioco;
	
	// sessioni: token -> Utente
	private final Map<String, Utente> sessioni = new ConcurrentHashMap<>();
	
	private static final Logger logger = LoggerFactory.getLogger(ServerUno.class);


	public ServerUno(String host, int port) {
		Configuration config = new Configuration();
		config.setHostname(host);
		config.setPort(port);
		config.setPingInterval(10000);
		config.setPingTimeout(60000);
		config.setExceptionListener(new ServerUnoExceptionListener());
		
		this.server = new SocketIOServer(config);
		this.gestoreConnessioni = new GestoreConnessioni(sessioni);
		this.gestoreStanze = new GestoreStanze(gestoreConnessioni);
		this.gestorePartiteOffline = new GestorePartiteOffline(gestoreConnessioni);
		this.gestoreGioco = new GestoreGioco(gestoreStanze);

		registraEventi();
	}

	private void registraEventi() {
		server.addConnectListener(client -> {
			HandshakeData hd = client.getHandshakeData();
			String addr = "unknown";
			try {
				if (client.getRemoteAddress() != null)
					addr = client.getRemoteAddress().toString();
				else if (hd != null && hd.getAddress() != null)
					addr = hd.getAddress().toString();
			} catch (Exception ex) {
			}

			logger.info("[SERVER] Nuova connessione da {} sessionId={}", addr, client.getSessionId());

			// Recupera token dal parametro della connessione (se presente)
			String token = hd.getSingleUrlParam("token");
			if (token != null && !token.isEmpty()) {
				Utente u = gestoreConnessioni.getUtenteByToken(token);
				if (u != null) {
					u.setConnesso(true);
					client.set("token", token);
					logger.info("[SERVER] Riconnesso utente: {} (token={})", u.getUsername(), token);
				}
			}

		});

		// disconnessione
		server.addDisconnectListener(client -> {
			logger.info("[SERVER] Disconnessione client sessionId={}", client.getSessionId());
			// delego a gestore connessioni per marcare Utente offline
			gestoreConnessioni.handleDisconnessione(client);
		});

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
			//stanza.riceviMossa(token, mossa);
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
		
		logger.debug("Eventi registrati");
	}

	public void avvia() {
		server.start();
		logger.info("[SERVER] Avviato su {}:{}", server.getConfiguration().getHostname(),
				server.getConfiguration().getPort());
	}

	public void stop() {
		server.stop();
		logger.info("[SERVER] Arrestato.");
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
			logger.info("[SERVER] Arresto in corso...");
			srv.stop();
		}));
	}
}
