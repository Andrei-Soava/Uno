package onegame.server;

import java.util.UUID;

/**
 * Rappresenta un utente connesso al sistema. Usabile sia per utenti registrati
 * sia per anonimi.
 */
public class Utente {
	private String username;
	private String nickname;
	private boolean anonimo;
	private boolean connesso;
	private long ultimoPing;

	private Utente() {
		this.connesso = true;
	}

	public static Utente createUtente(String username) {
		Utente u = new Utente();
		u.anonimo = false;
		u.username = username;
		u.nickname = username;
		return u;
	}

	public static Utente createUtenteAnonimo(String nickname) {
		Utente u = new Utente();
		u.anonimo = true;
		u.username = "guest-" + System.currentTimeMillis() + UUID.randomUUID().toString();
		u.nickname = nickname;
		return u;
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

	@Override
	public String toString() {
		return "Giocatore[username=" + username + ", anonimo=" + anonimo + "]";
	}
}
