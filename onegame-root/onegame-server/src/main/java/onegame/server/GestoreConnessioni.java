package onegame.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.util.JsonHelper;
import onegame.modello.net.util.PasswordUtils;
import onegame.server.db.UtenteDb;
import onegame.modello.net.ProtocolloMessaggi.ReqAuth;
import onegame.modello.net.ProtocolloMessaggi.RespAuth;

/**
 * Gestore delle connessioni e dell'autenticazione degli utenti
 */
public class GestoreConnessioni {
	private final Map<String, Utente> sessioni; // token -> Utente
	// private final Map<String, String> utentiRegistrati; // username ->
	// passwordHash
	private final UtenteDb utenteDb;

	private static final String JWT_SECRET = "u7$T9z!k@!#Lqa^mT2&b10pW";
	private static final long TOKEN_EXPIRATION_MS = 36000_000;

	private static final Logger logger = LoggerFactory.getLogger(GestoreConnessioni.class);

	/**
	 * Costruttore del gestore connessioni
	 * @param sessioni La mappa delle sessioni attive (token -> Utente)
	 */
	public GestoreConnessioni(Map<String, Utente> sessioni) {
		this.sessioni = sessioni;
		this.utenteDb = new UtenteDb();
		// this.utentiRegistrati = new ConcurrentHashMap<>();

		// utentiRegistrati.put("test25", hashPassword("test25"));
	}

	/** Genera un token JWT per l'utente */
	private String generaToken(String username) {
		return JWT.create().withClaim("username", username)
				.withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
				.sign(Algorithm.HMAC256(JWT_SECRET));
	}

	/**
	 * Gestisce la richiesta di login di un utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
	public void handleLogin(SocketIOClient client, String str, AckRequest ackRequest) {
		try {
			ReqAuth req = JsonHelper.fromJson(str, ReqAuth.class);
			String username = req.username;
			String password = req.password;

			if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
				ackRequest.sendAckData(new RespAuth(false, null, null, "Credenziali mancanti"));
				return;
			}

			String storedHash = utenteDb.getPasswordHash(username);
			if (storedHash == null || !PasswordUtils.verificaPassword(password, storedHash)) {
				ackRequest.sendAckData(new RespAuth(false, null, null, "Credenziali non valide"));
				logger.warn("[Server] Tentativo di login fallito per username: {}", username);
				return;
			}

			Utente utente = Utente.createUtente(username);
			utente.setConnesso(true);
			String token = generaToken(username);

			sessioni.put(token, utente);
			client.set("token", token);

			ackRequest.sendAckData(new RespAuth(true, null, token, "Login completato"));

			logger.info("[Server] Nuovo utente loggato: username: {}, token: {}, session-id: {}", username, token,
					client.getSessionId());
		} catch (Exception e) {
			logger.error("[Server] Errore durante il login: {}", e.getMessage());
			ackRequest.sendAckData(new RespAuth(false, null, null, "Errore interno"));
		}
	}

	/**
	 * Gestisce la richiesta di registrazione di un nuovo utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
	public void handleRegister(SocketIOClient client, String str, AckRequest ackRequest) {
		try {
			ReqAuth req = JsonHelper.fromJson(str, ReqAuth.class);
			String username = req.username;
			String password = req.password;

			if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
				ackRequest.sendAckData(new RespAuth(false, null, null, "Credenziali mancanti"));
				return;
			}

			if (!isUsernameValido(username)) {
				ackRequest.sendAckData(new RespAuth(false, null, null, "Username non valido"));
				logger.warn("[Server] Tentativo di registrazione con username non valido: {}", username);
				return;
			}

			if (utenteDb.esisteUtente(username)) {
				ackRequest.sendAckData(new RespAuth(false, null, null, "Utente già esistente"));
				logger.warn("[Server] Tentativo di registrazione con username già esistente: {}", username);
				return;
			}

			String passwordHash = PasswordUtils.hashPassword(password);
			utenteDb.registraUtente(username, passwordHash);

			Utente utente = Utente.createUtente(username);
			utente.setConnesso(true);
			String token = generaToken(username);

			sessioni.put(token, utente);
			client.set("token", token);

			ackRequest.sendAckData(new RespAuth(true, null, token, "Registrazione completata"));

			logger.info("[Server] Nuovo utente registrato: username: {}, token: {}, session-id: {}", username, token,
					client.getSessionId());
		} catch (Exception e) {
			logger.error("[Server] Errore durante la registrazione: {}", e.getMessage());
			ackRequest.sendAckData(new RespAuth(false, null, null, "Errore interno"));
		}
	}

	/**
	 * Gestisce la richiesta di accesso anonimo
	 * @param client Il client che effettua la richiesta
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
	public void handleAnonimo(SocketIOClient client, AckRequest ackRequest) {
		try {
			Utente utenteAnonimo = Utente.createUtenteAnonimo("Anonimo");
			String token = UUID.randomUUID().toString();

			sessioni.put(token, utenteAnonimo);
			client.set("token", token);
			ackRequest.sendAckData(new RespAuth(true, null, token, "Accesso anonimo riuscito"));
			logger.info("[Server] Utente anonimo connesso");
		} catch (Exception e) {
			logger.error("[Server] Errore durante l'accesso anonimo: {}", e.getMessage());
			ackRequest.sendAckData(new RespAuth(false, null, null, "Errore interno"));
		}
	}

	/**
	 * Gestisce la disconnessione di un client
	 * @param client Il client che si disconnette
	 */
	public void handleDisconnessione(SocketIOClient client) {
		String token = client.get("token");
		if (token == null)
			return;
		Utente u = sessioni.get(token);
		if (u != null) {
			u.setConnesso(false);
			logger.info("[Server] Disconnessione utente: {} sessionId={}", u.getNickname(),
					client.getSessionId());
		}
	}

//    public void handleRichiestaPartiteNonConcluse(SocketIOClient client) {
//        ProtocolloMessaggi.RespStanza resp = new ProtocolloMessaggi.RespStanza("", "NESSUNA",
//                "Funzionalità partite non concluse non ancora implementata");
//        client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_OK, resp);
//    }

	public void rimuoviSessione(String token) {
		if (token != null)
			sessioni.remove(token);
	}

	/**
	 * Recupera l'utente associato a un token
	 * @param token Il token di sessione
	 * @return L'utente associato, o null se non trovato
	 */
	public Utente getUtenteByToken(String token) {
		return sessioni.get(token);
	}

	private boolean isUsernameValido(String username) {
		String str;
		return username != null && username.matches("^[a-zA-Z0-9_]{3,50}$")
				&& !(str = username.toLowerCase()).startsWith("anonimo") && !str.startsWith("guest")
				&& !str.startsWith("admin");
	}
}
