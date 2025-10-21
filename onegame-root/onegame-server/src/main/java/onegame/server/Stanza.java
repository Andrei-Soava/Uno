package onegame.server;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.net.StatoStanzaDTO;
import onegame.modello.net.messaggi.Messaggi;
import onegame.server.utils.DTOServerUtils;

/**
 * Classe astratta che rappresenta una stanza generica con gestione delle
 * sessioni.
 */
public abstract class Stanza {

	protected final int codice;
	protected final String nome;
	protected int maxUtenti;
	protected boolean isAperta = true;

	protected final ReentrantLock lock = new ReentrantLock();

	protected final Set<Sessione> sessioni = new LinkedHashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(Stanza.class);

	public Stanza(int codice, String nome, int maxUtenti) {
		this.codice = codice;
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

	public String getNome() {
		return nome;
	}

	public int getMaxUtenti() {
		return maxUtenti;
	}

	public boolean hasUtente(Sessione sessione) {
		lock.lock();
		try {
			return sessioni.contains(sessione);
		} finally {
			lock.unlock();
		}
	}

	public Set<Sessione> getSessioni() {
		lock.lock();
		try {
			return Collections.unmodifiableSet(new LinkedHashSet<>(sessioni));
		} finally {
			lock.unlock();
		}
	}

	public void notificaStato() {
		lock.lock();
		logger.debug("Notifica stato stanza {} a {} utenti", codice, sessioni.size());
		try {
			StatoStanzaDTO dto = DTOServerUtils.creaStanzaDTO(this);
			for (Sessione s : sessioni) {
				s.sendEvent(Messaggi.EVENT_STANZA_AGGIORNAMENTO, dto);
			}
		} finally {
			lock.unlock();
		}
	}

}
