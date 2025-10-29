package onegame.server;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.messaggi.Messaggi.ReqAuth;
import onegame.modello.net.messaggi.Messaggi.ReqAuthAnonimo;
import onegame.modello.net.messaggi.Messaggi.RespAuth;
import onegame.modello.net.util.JsonHelper;
import onegame.server.db.UtenteDb;
import onegame.server.utils.PasswordUtils;
import onegame.server.utils.UsernameValidator;

/**
 * Gestore delle connessioni e dell'autenticazione degli utenti
 */
public class GestoreConnessioni {
	private final GestoreSessioni gestoreSessioni;
	private final UtenteDb utenteDb = new UtenteDb();

	private static final String JWT_SECRET = "u7$T9z!k@!#Lqa^mT2&b10pW";
	private static final long TOKEN_EXPIRATION_MS = 36000_000;

	private static final Logger logger = LoggerFactory.getLogger(GestoreConnessioni.class);

	public GestoreConnessioni(GestoreSessioni gestoreSessioni) {
		this.gestoreSessioni = gestoreSessioni;
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
				ackRequest.sendAckData(new RespAuth(false, null, "Credenziali mancanti", null, null));
				logger.warn("Tentativo di login con credenziali mancanti");
				return;
			}

			String storedHash = utenteDb.getPasswordHash(username);
			if (storedHash == null || !PasswordUtils.verificaPassword(password, storedHash)) {
				ackRequest.sendAckData(new RespAuth(false, null, "Credenziali non valide", null, null));
				logger.warn("Tentativo di login fallito per username: {}", username);
				return;
			}

			String token = generaToken(username);
			Sessione sessione = Sessione.createSessione(username, token);
			gestoreSessioni.associaToken(token, sessione, client);

			ackRequest.sendAckData(
					new RespAuth(true, token, "Login completato", sessione.getUsername(), sessione.getNickname()));
			logger.info("Login utente: username: {}, token: {}, session-id: {}", username, token,
					client.getSessionId());
		} catch (Exception e) {
			logger.error("Errore durante il login: {}", e);
			ackRequest.sendAckData(new RespAuth(false, null, "Errore interno", null, null));
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
				ackRequest.sendAckData(new RespAuth(false, null, "Credenziali mancanti", null, null));
				logger.warn("Tentativo di registrazione con credenziali mancanti");
				return;
			}

			if (!UsernameValidator.isUsernameValido(username)) {
				ackRequest.sendAckData(new RespAuth(false, null, "Username non disponibile", null, null));
				logger.warn("Tentativo di registrazione con username non valido: {}", username);
				return;
			}

			if (utenteDb.esisteUtente(username)) {
				ackRequest.sendAckData(new RespAuth(false, null, "Username non disponibile", null, null));
				logger.warn("Tentativo di registrazione con username già esistente: {}", username);
				return;
			}

			String passwordHash = PasswordUtils.hashPassword(password);
			utenteDb.registraUtente(username, passwordHash);

			String token = generaToken(username);
			Sessione sessione = Sessione.createSessione(username, token);
			gestoreSessioni.associaToken(token, sessione, client);

			ackRequest.sendAckData(new RespAuth(true, token, "Registrazione completata", sessione.getUsername(),
					sessione.getNickname()));
			logger.info("Registrazione utente: username: {}, token: {}, session-id: {}", username, token,
					client.getSessionId());
		} catch (Exception e) {
			logger.error("Errore durante la registrazione: {}", e);
			ackRequest.sendAckData(new RespAuth(false, null, "Errore interno", null, null));
		}
	}

	/**
	 * Gestisce la richiesta di accesso anonimo
	 * @param client Il client che effettua la richiesta
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
	public void handleAnonimo(SocketIOClient client, String str, AckRequest ackRequest) {
		try {
			ReqAuthAnonimo req = JsonHelper.fromJson(str, ReqAuthAnonimo.class);
			String nickname = req.nickname;

			if (nickname == null || nickname.isEmpty() || !nickname.matches("^[a-zA-Z0-9_]{3,44}$")) {
				ackRequest.sendAckData(new RespAuth(false, null, "Nickname non valido", null, null));
				logger.warn("Tentativo di accesso anonimo con nickname non valido: {}", nickname);
				return;
			}

			String token;
			do {
				token = UUID.randomUUID().toString();
			} while (gestoreSessioni.getSessione(token) != null);

			Sessione sessione = Sessione.createSessioneAnonimo(nickname, token);
			gestoreSessioni.associaToken(token, sessione, client);

			ackRequest.sendAckData(new RespAuth(true, token, "Accesso anonimo riuscito", sessione.getUsername(),
					sessione.getNickname()));
			logger.info("Accesso anonimo: nickname={} token={} sessionId={}", sessione.getNickname(), token,
					client.getSessionId());
		} catch (Exception e) {
			logger.error("Errore durante l'accesso anonimo: {}", e);
			ackRequest.sendAckData(new RespAuth(false, null, "Errore interno", null, null));
		}
	}

	public void handleConnessione(SocketIOClient client) {
		HandshakeData hd = client.getHandshakeData();
		String addr = "unknown";
		try {
			if (client.getRemoteAddress() != null)
				addr = client.getRemoteAddress().toString();
			else if (hd != null && hd.getAddress() != null)
				addr = hd.getAddress().toString();
		} catch (Exception ex) {
		}

		logger.info("Nuova connessione da {} sessionId={}", addr, client.getSessionId());

		// Recupera token dal parametro della connessione (se presente)
		String token = hd.getSingleUrlParam("token");
		if (token != null && !token.isEmpty()) {
			Sessione s = gestoreSessioni.getSessione(token);
			if (s != null) {
				s.setConnesso(true);
				s.aggiornaPing();
				client.set("token", token);
				logger.info("Riconnesso utente: {} (token={})", s.getUsername(), token);
			}
		}
	}

	public void handleDisconnessione(SocketIOClient client) {
		gestoreSessioni.marcaDisconnesso(client);
	}
}
