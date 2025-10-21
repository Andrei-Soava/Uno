package onegame.server;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

/**
 * Rappresenta una sessione utente nel server.
 */
public class Sessione {
	private String username;
	private String nickname;
	private boolean anonimo;
	private boolean connesso;
	private long ultimoPing;
	private final String token;
	private SocketIOClient client;

	private static final Logger logger = LoggerFactory.getLogger(Sessione.class);

	private Sessione(String token) {
		this.connesso = true;
		this.token = token;
	}

	public static Sessione createSessione(String username, String token) {
		Sessione sessione = new Sessione(token);
		sessione.anonimo = false;
		sessione.username = username;
		sessione.nickname = username;
		return sessione;
	}

	public static Sessione createSessioneAnonimo(String nickname, String token) {
		Sessione sessione = new Sessione(token);
		sessione.anonimo = true;
		sessione.username = "guest-" + System.currentTimeMillis() + UUID.randomUUID().toString();

		if (nickname.startsWith("guest-")) {
			sessione.nickname = nickname;
		} else {
			sessione.nickname = "guest-" + nickname;
		}

		return sessione;
	}

	public String getUsername() {
		return username;
	}

	public String getNickname() {
		return nickname;
	}

	public boolean isAnonimo() {
		return anonimo;
	}

	public boolean isConnesso() {
		return connesso;
	}

	public void setConnesso(boolean connesso) {
		this.connesso = connesso;
	}

	public void aggiornaPing() {
		this.ultimoPing = System.currentTimeMillis();
	}

	public long getUltimoPing() {
		return ultimoPing;
	}

	public String getToken() {
		return token;
	}

	void setClient(SocketIOClient client) {
		this.client = client;
	}

	public void sendEvent(String evento, Object payload) {
		if (client != null) {
			client.sendEvent(evento, payload);
		} else {
			logger.debug("Impossibile inviare evento '{}': client nullo per username {}", evento, username);
		}
	}

	public void sendEvent(String evento, Object payload, AckRequest ack) {
		if (client != null) {
			client.sendEvent(evento, payload, ack);
		} else {
			logger.debug("Impossibile inviare evento '{}': client nullo per username {}", evento, username);
		}
	}

	@Override
	public String toString() {
		return "Sessione[username=" + username + ", anonimo=" + anonimo + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Sessione other = (Sessione) obj;
		return Objects.equals(token, other.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}

}
