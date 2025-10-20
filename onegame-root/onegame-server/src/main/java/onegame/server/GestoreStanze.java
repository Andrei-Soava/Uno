package onegame.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.messaggi.Messaggi.ReqCreaStanza;
import onegame.modello.net.messaggi.Messaggi.ReqEntraStanza;
import onegame.modello.net.messaggi.Messaggi.RespAbbandonaStanza;
import onegame.modello.net.messaggi.Messaggi.RespCreaStanza;
import onegame.modello.net.messaggi.Messaggi.RespEntraStanza;
import onegame.modello.net.util.JsonHelper;

/**
 * Gestisce creazione, ingresso e gestione delle stanze di gioco. Una stanza è
 * rappresentata da un'istanza di StanzaPartita. Ogni stanza ha un id univoco
 * (long) e un codice (int).
 */
public abstract class GestoreStanze<Stanzz extends Stanza> implements SessioneObserver {

	// Mappa idStanza → Stanza
	protected final Map<Long, Stanzz> stanze = new ConcurrentHashMap<>();
	// Mappa sessione → idStanza
	private final Map<Sessione, Long> mappaSessioneAIdStanza = new ConcurrentHashMap<>();
	// Mappa codiceStanza → idStanza
	private final Map<Integer, Long> mappaCodiceAIdStanza = new ConcurrentHashMap<>();

	private final AtomicLong counterId = new AtomicLong();
	private final AtomicInteger counterCodice = new AtomicInteger(MIN_CODICE);

	private static final int MIN_CODICE = 1000000;
	private static final int MAX_CODICE = 9999999;

	private static final Logger logger = LoggerFactory.getLogger(GestoreStanze.class);

	public GestoreStanze() {
	}

	/**
	 * Crea una nuova stanza e aggiunge l'utente che ha fatto la richiesta.
	 */
	public void handleCreaStanza(Sessione sessione, String str, AckRequest ack) {
		try {
			ReqCreaStanza req = JsonHelper.fromJson(str, ReqCreaStanza.class);
			long idStanza = counterId.incrementAndGet();
			int maxGiocatori = Math.max(2, req.maxGiocatori);
			int codiceStanza = nextCodice();

			Stanzz stanza = creaStanza(codiceStanza, idStanza, req.nomeStanza, maxGiocatori);
			if (stanza == null) {
				ack.sendAckData(new RespCreaStanza(false, "Errore creazione stanza", -1));
				logger.error("Creazione stanza fallita per sessione {}", sessione.getToken());
				return;
			}

			stanze.put(idStanza, stanza);
			mappaSessioneAIdStanza.put(sessione, idStanza);
			stanza.aggiungiUtente(sessione);
			mappaCodiceAIdStanza.put(codiceStanza, idStanza);

			ack.sendAckData(new RespCreaStanza(true, "Stanza creata con successo", codiceStanza));
			logger.info("Creata stanza {} da sessione {}", idStanza, sessione.getToken());
		} catch (Exception e) {
			logger.error("Errore creazione stanza: {}", e.getMessage());
			ack.sendAckData(new RespCreaStanza(false, "Errore creazione stanza", -1));
		}
	}

	/**
	 * Aggiunge l'utente autenticato alla stanza richiesta.
	 */
	public void handleEntraStanza(Sessione sessione, String str, AckRequest ack) {
		try {
			ReqEntraStanza req = JsonHelper.fromJson(str, ReqEntraStanza.class);
			Long idStanza = mappaCodiceAIdStanza.get(req.codiceStanza);
			Stanzz stanza = stanze.get(idStanza);

			if (stanza == null) {
				ack.sendAckData(new RespEntraStanza(false, "Stanza non trovata"));
				logger.warn("Tentativo di ingresso in stanza non esistente: codice {}", req.codiceStanza);
				return;
			}

			boolean ok = stanza.aggiungiUtente(sessione);
			if (!ok) {
				ack.sendAckData(new RespEntraStanza(false, "Impossibile entrare in stanza (piena o già in gioco)"));
				return;
			}

			mappaSessioneAIdStanza.put(sessione, idStanza);
			ack.sendAckData(new RespEntraStanza(true, "Ingresso in stanza avvenuto con successo"));
			logger.info("Sessione {} entrata in stanza {} con codice {}", sessione.getToken(), idStanza,
					req.codiceStanza);
		} catch (Exception e) {
			logger.error("Errore ingresso stanza: {}", e.getMessage());
			ack.sendAckData(new RespEntraStanza(false, "Errore ingresso stanza"));
		}
	}

	/**
	 * Rimuove l'utente dalla stanza e la stanza se è vuota.
	 */
	public void rimuoviUtenteDaSistema(Sessione sessione) {
		Long idStanza = mappaSessioneAIdStanza.remove(sessione);
		if (idStanza == null)
			return;

		Stanzz stanza = stanze.get(idStanza);
		if (stanza == null)
			return;

		stanza.lock.lock();
		try {
			if (stanza.rimuoviUtente(sessione)) {
				logger.info("Sessione {} rimossa da stanza {}", sessione.getToken(), idStanza);
			}
			if (stanza.isVuota()) {
				stanze.remove(idStanza);
				mappaCodiceAIdStanza.remove(stanza.getCodice());
				logger.info("Stanza {} rimossa perché vuota", idStanza);
			}
		} finally {
			stanza.lock.unlock();
		}
	}

	public Stanzz getStanza(long idStanza) {
		return stanze.get(idStanza);
	}

	public Stanzz getStanzaPerSessione(Sessione sessione) {
		Long idStanza = mappaSessioneAIdStanza.get(sessione);
		return idStanza != null ? stanze.get(idStanza) : null;
	}

	/**
	 * Permette all'utente di abbandonare la stanza in cui si trova.
	 */
	public void handleAbbandonaStanza(Sessione sessione, AckRequest ack) {
		if (!mappaSessioneAIdStanza.containsKey(sessione)) {
			ack.sendAckData(new RespAbbandonaStanza(false, "Utente in nessuna stanza"));
			logger.warn("Sessione {} non è in nessuna stanza", sessione.getToken());
			return;
		}

		rimuoviUtenteDaSistema(sessione);
		ack.sendAckData(new RespAbbandonaStanza(true, "Abbandono stanza avvenuto con successo"));
	}

	@Override
	public void onSessioneInattiva(Sessione sessione) {
		rimuoviUtenteDaSistema(sessione);
	}

	protected abstract Stanzz creaStanza(int codice, long id, String nome, int maxUtenti);

	private synchronized int nextCodice() {
		int codice;
		do {
			codice = counterCodice.getAndIncrement();
			if (codice > MAX_CODICE) {
				counterCodice.set(MIN_CODICE);
				codice = counterCodice.getAndIncrement();
			}
		} while (mappaCodiceAIdStanza.containsKey(codice));
		return codice;
	}
}
