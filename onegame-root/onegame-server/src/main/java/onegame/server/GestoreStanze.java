package onegame.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;

import onegame.modello.net.StatoStanzaDTO;
import onegame.modello.net.messaggi.Messaggi.ReqCreaStanza;
import onegame.modello.net.messaggi.Messaggi.ReqEntraStanza;
import onegame.modello.net.messaggi.Messaggi.RespAbbandonaStanza;
import onegame.modello.net.messaggi.Messaggi.RespCreaStanza;
import onegame.modello.net.messaggi.Messaggi.RespDettagliStanza;
import onegame.modello.net.messaggi.Messaggi.RespEntraStanza;
import onegame.modello.net.util.JsonHelper;
import onegame.server.utils.DTOServerUtils;

/**
 * Gestisce creazione, ingresso e gestione delle stanze di gioco. Una stanza è
 * rappresentata da un'istanza di StanzaPartita. Ogni stanza ha un id univoco
 * (long) e un codice (int). Ogni sessione può essere in al più una stanza alla volta.
 */
public abstract class GestoreStanze<Stanzz extends Stanza> implements SessioneObserver {

	private final Map<Sessione, Stanzz> mappaSessioneAStanza = new ConcurrentHashMap<>();
	protected final Map<Integer, Stanzz> stanzePerCodice = new ConcurrentHashMap<>();

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
			if (mappaSessioneAStanza.containsKey(sessione)) {
				ack.sendAckData(new RespCreaStanza(false, "Utente già in una stanza", -1));
				logger.warn("Tentativo di creare una stanza da parte di una sessione già in stanza: {}",
						sessione.getToken());
				return;
			}

			ReqCreaStanza req = JsonHelper.fromJson(str, ReqCreaStanza.class);
			int maxGiocatori = Math.max(2, req.maxGiocatori);
			int codiceStanza = nextCodice();

			Stanzz stanza = creaStanza(codiceStanza, req.nomeStanza, maxGiocatori);
			if (stanza == null) {
				ack.sendAckData(new RespCreaStanza(false, "Errore creazione stanza", -1));
				logger.error("Creazione stanza fallita per sessione {}", sessione.getToken());
				return;
			}

			mappaSessioneAStanza.put(sessione, stanza);
			stanzePerCodice.put(codiceStanza, stanza);
			stanza.aggiungiUtente(sessione);

			ack.sendAckData(new RespCreaStanza(true, "Stanza creata con successo", codiceStanza));
			logger.info("Creata stanza {} da sessione {}", stanza.getCodice(), sessione.getToken());

			stanza.notificaStato();
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
			if (mappaSessioneAStanza.containsKey(sessione)) {
				ack.sendAckData(new RespEntraStanza(false, "Utente già in una stanza"));
				logger.warn("Tentativo di ingresso in stanza da parte di una sessione già in stanza: {}",
						sessione.getToken());
				return;
			}

			ReqEntraStanza req = JsonHelper.fromJson(str, ReqEntraStanza.class);

			Stanzz stanza = stanzePerCodice.get(req.codiceStanza);

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

			mappaSessioneAStanza.put(sessione, stanza);
			ack.sendAckData(new RespEntraStanza(true, "Ingresso in stanza avvenuto con successo"));
			logger.info("Sessione {} entrata nella stanza con codice {}", sessione.getToken(), req.codiceStanza);

			stanza.notificaStato();
		} catch (Exception e) {
			logger.error("Errore ingresso stanza: {}", e.getMessage());
			ack.sendAckData(new RespEntraStanza(false, "Errore ingresso stanza"));
		}
	}

	public void handleDettagliStanza(Sessione sessione, AckRequest ack) {
		try {
			Stanza stanzz = mappaSessioneAStanza.get(sessione);
			if (stanzz == null) {
				ack.sendAckData(new RespDettagliStanza(false, "Utente non è in una stanza", null));
				logger.warn("Richiesta dettagli stanza da parte di una sessione non in stanza: {}",
						sessione.getToken());
				return;
			}
			StatoStanzaDTO dto = DTOServerUtils.creaStanzaDTO(stanzz);
			ack.sendAckData(new RespDettagliStanza(true, "Dettagli stanza ottenuti con successo", dto));
			logger.info("Dettagli stanza {} inviati a sessione {}", stanzz.getCodice(), sessione.getToken());
		} catch (Exception e) {
			logger.error("Errore ottenimento dettagli stanza: {}", e.getMessage());
			ack.sendAckData(new RespDettagliStanza(false, "Errore ottenimento dettagli stanza", null));
		}
	}

	/**
	 * Rimuove la sessione dalla stanza e la stanza se è vuota.
	 */
	private void rimuoviUtenteDaSistema(Sessione sessione) {
		Stanzz stanza = mappaSessioneAStanza.remove(sessione);
		if (stanza == null)
			return;

		stanza.lock.lock();
		try {
			if (stanza.rimuoviUtente(sessione)) {
				logger.info("Sessione {} rimossa da stanza {}", sessione.getToken(), stanza.getCodice());
			}
			if (stanza.isVuota()) {
				removeStanza(stanza);
				return;
			}
			stanza.notificaStato();
		} finally {
			stanza.lock.unlock();
		}
	}

	private void removeStanza(Stanzz stanza) {
		stanzePerCodice.remove(stanza.getCodice());
		logger.info("Stanza {} rimossa manualmente", stanza.getCodice());
	}

	public Stanzz getStanzaPerSessione(Sessione sessione) {
		return mappaSessioneAStanza.get(sessione);
	}

	/**
	 * Permette all'utente di abbandonare la stanza in cui si trova.
	 */
	public void handleAbbandonaStanza(Sessione sessione, AckRequest ack) {
		if (!mappaSessioneAStanza.containsKey(sessione)) {
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

	protected abstract Stanzz creaStanza(int codice, String nome, int maxUtenti);

	private synchronized int nextCodice() {
		int codice;
		do {
			codice = counterCodice.getAndIncrement();
			if (codice > MAX_CODICE) {
				counterCodice.set(MIN_CODICE);
				codice = counterCodice.getAndIncrement();
			}
		} while (stanzePerCodice.containsKey(codice));
		return codice;
	}
}
