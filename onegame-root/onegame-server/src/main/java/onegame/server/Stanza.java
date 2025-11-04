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
	protected int maxSessioni;
	protected boolean isAperta = true;

	protected final ReentrantLock lock = new ReentrantLock();

	protected final Set<Sessione> sessioni = new LinkedHashSet<>();
	protected Sessione proprietario = null;

	private static final Logger logger = LoggerFactory.getLogger(Stanza.class);

	public Stanza(int codice, String nome, int maxSessioni) {
		this.codice = codice;
		this.nome = nome;
		this.maxSessioni = maxSessioni;
	}

	public boolean aggiungiSessione(Sessione sessione) {
		lock.lock();
		try {
			if (!isAperta || sessioni.size() >= maxSessioni || sessioni.contains(sessione))
				return false;

			for (Sessione s : sessioni) {
				if (s.getUsername().equals(sessione.getUsername())) {
					return false; // L'utente è già presente nella stanza
				}
			}

			boolean added = sessioni.add(sessione);
			if (added && proprietario == null) {
				proprietario = sessione;
			}
			return added;
		} finally {
			lock.unlock();
		}
	}

	public boolean rimuoviSessione(Sessione sessione) {
		lock.lock();
		try {
			boolean removed = sessioni.remove(sessione);
			if (removed && Objects.equals(proprietario, sessione)) {
				proprietario = sessioni.isEmpty() ? null : sessioni.iterator().next();
			}
			return removed;
		} catch (Exception e) {
			logger.error("Errore rimozione sessione dalla stanza {}: {}", codice, e.getMessage());
			return false;
		} finally {
			lock.unlock();
		}
	}

	public boolean isVuota() {
		return sessioni.isEmpty();
	}

	public boolean isPiena() {
		return sessioni.size() >= maxSessioni;
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

	public int getMaxSessioni() {
		return maxSessioni;
	}

	public int getNumSessioni() {
		lock.lock();
		try {
			return sessioni.size();
		} finally {
			lock.unlock();
		}
	}

	public boolean hasSessione(Sessione sessione) {
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

	/**
	 * Notifica a tutte le sessioni nella stanza lo stato attuale della stanza.
	 */
	public void notificaStato() {
		lock.lock();
		try {
			StatoStanzaDTO dto = DTOServerUtils.creaStanzaDTO(this);
			for (Sessione s : sessioni) {
				s.sendEvent(Messaggi.EVENT_STANZA_AGGIORNAMENTO, dto);
			}
			logger.debug("Notifica stato stanza {} a {} utenti", codice, sessioni.size());
		} catch (Exception e) {
			logger.error("Errore notifica stato stanza {}: {}", codice, e.getMessage());
		} finally {
			lock.unlock();
		}
	}

	public Sessione getProprietario() {
		return proprietario;
	}

	/**
	 * Trasferisce la proprietà della stanza a una nuova sessione.
	 * @param nuovoProprietario la sessione che diventerà il nuovo proprietario
	 * @return true se il trasferimento è avvenuto con successo, false altrimenti
	 */
	public boolean trasferisciProprietario(Sessione nuovoProprietario) {
		lock.lock();
		try {
			if (sessioni.contains(nuovoProprietario)) {
				proprietario = nuovoProprietario;
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

}
