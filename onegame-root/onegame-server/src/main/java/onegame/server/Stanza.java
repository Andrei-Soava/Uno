package onegame.server;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * Classe astratta che rappresenta una stanza generica con gestione delle
 * sessioni.
 */
public abstract class Stanza {

	protected final int codice;
	protected final long id;
	protected final String nome;
	protected int maxUtenti;
	protected final GestoreSessioni gestoreSessioni;
	protected boolean isAperta = true;

	protected final ReentrantLock lock = new ReentrantLock();

	protected final Map<String, Sessione> sessionePerToken = new LinkedHashMap<>();

	public Stanza(int codice, long id, String nome, int maxUtenti, GestoreSessioni gestoreSessioni) {
		this.codice = codice;
		this.id = id;
		this.nome = nome;
		this.maxUtenti = maxUtenti;
		this.gestoreSessioni = gestoreSessioni;
	}

	public boolean aggiungiUtente(String token) {
		lock.lock();
		try {
			if (!isAperta || sessionePerToken.size() >= maxUtenti || sessionePerToken.containsKey(token))
				return false;
			sessionePerToken.put(token, gestoreSessioni.getSessione(token));
			return true;
		} finally {
			lock.unlock();
		}
	}

	public boolean rimuoviUtente(String token) {
		lock.lock();
		try {
			return sessionePerToken.remove(token) != null;
		} finally {
			lock.unlock();
		}
	}

	public boolean isVuota() {
		return sessionePerToken.isEmpty();
	}

	public boolean isPiena() {
		return sessionePerToken.size() >= maxUtenti;
	}

	public void broadcast(String evento, Object payload) {
		lock.lock();
		try {
			for (Map.Entry<String, Sessione> entry : sessionePerToken.entrySet()) {
				String token = entry.getKey();
				SocketIOClient client = getClient(token);
				if (client != null) {
					client.sendEvent(evento, payload);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public int getCodice() {
		return codice;
	}

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public int getMaxUtenti() {
		return maxUtenti;
	}

	public Set<String> getTokenUtenti() {
		lock.lock();
		try {
			return Collections.unmodifiableSet(sessionePerToken.keySet());
		} finally {
			lock.unlock();
		}
	}

	public boolean hasUtente(String token) {
		lock.lock();
		try {
			return sessionePerToken.containsKey(token);
		} finally {
			lock.unlock();
		}
	}

	protected SocketIOClient getClient(String token) {
		return gestoreSessioni.getClient(token);
	}
}
