package onegame.server;

import java.util.UUID;

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

	private static final long TIMEOUT_MS = 300_000;

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
		sessione.nickname = nickname;
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

	public boolean isAttivo() {
		return connesso && (System.currentTimeMillis() - ultimoPing < TIMEOUT_MS);
	}

	public boolean isAttivo(long timeoutMs) {
		return connesso && (System.currentTimeMillis() - ultimoPing < timeoutMs);
	}

	public long getUltimoPing() {
		return ultimoPing;
	}

	public String getToken() {
		return token;
	}

	@Override
	public String toString() {
		return "Sessione[username=" + username + ", anonimo=" + anonimo + "]";
	}
}
