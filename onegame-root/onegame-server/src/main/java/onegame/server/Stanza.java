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
	protected boolean isAperta = true;

	protected final ReentrantLock lock = new ReentrantLock();

	protected final Set<Sessione> sessioni = new LinkedHashSet<>();

	public Stanza(int codice, long id, String nome, int maxUtenti) {
		this.codice = codice;
		this.id = id;
		this.nome = nome;
		this.maxUtenti = maxUtenti;
	}

	public boolean aggiungiUtente(Sessione sessione) {
		lock.lock();
		try {
			if (!isAperta || sessioni.size() >= maxUtenti || sessioni.contains(sessione))
				return false;
			return sessioni.add(sessione);
		} finally {
			lock.unlock();
		}
	}

	public boolean rimuoviUtente(Sessione sessione) {
		lock.lock();
		try {
			return sessioni.remove(sessione);
		} finally {
			lock.unlock();
		}
	}

	public boolean isVuota() {
		return sessioni.isEmpty();
	}

	public boolean isPiena() {
		return sessioni.size() >= maxUtenti;
	}

	public void broadcast(String evento, Object payload) {
		lock.lock();
		try {
			for (Sessione s : sessioni) {
				s.sendEvent(evento, payload);
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

//	public Set<String> getTokenUtenti() {
//		lock.lock();
//		try {
//			Set<String> tokens = new LinkedHashSet<>();
//			for (Sessione s : sessioni) {
//				tokens.add(s.getToken());
//			}
//			return Collections.unmodifiableSet(tokens);
//		} finally {
//			lock.unlock();
//		}
//	}

	public boolean hasUtente(Sessione sessione) {
		lock.lock();
		try {
			return sessioni.contains(sessione);
		} finally {
			lock.unlock();
		}
	}
}
