package onegame.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.ProtocolloMessaggi.ReqCreaStanza;
import onegame.modello.net.ProtocolloMessaggi.ReqEntraStanza;
import onegame.modello.net.ProtocolloMessaggi.RespAbbandonaStanza;
import onegame.modello.net.ProtocolloMessaggi.RespCreaStanza;
import onegame.modello.net.ProtocolloMessaggi.RespEntraStanza;
import onegame.modello.net.util.JsonHelper;

/**
 * Gestisce creazione, ingresso e gestione delle stanze di gioco. Una stanza è
 * rappresentata da un'istanza di StanzaPartita. Ogni stanza ha un id univoco
 * (long) e un codice (int).
 */
public class GestoreStanze {

	private final GestoreConnessioni gestoreConnessioni;

	// Mappa idStanza → StanzaPartita
	private final Map<Long, StanzaPartita> stanze = new ConcurrentHashMap<>();
	// Mappa tokenUtente → idStanza
	private final Map<String, Long> mappaTokenUtenteAStanza = new ConcurrentHashMap<>();
	// Mappa codiceStanza → idStanza
	private final Map<Integer, Long> mappaCodiceAIdStanza = new ConcurrentHashMap<>();

	private final AtomicLong counterId = new AtomicLong();
	private final AtomicInteger counterCodice = new AtomicInteger(MIN_CODICE);

	private static final int MIN_CODICE = 1000000;
	private static final int MAX_CODICE = 9999999;

	private static final Logger logger = LoggerFactory.getLogger(GestoreStanze.class);

	public GestoreStanze(GestoreConnessioni gestoreConnessioni) {
		this.gestoreConnessioni = gestoreConnessioni;
	}

	/**
	 * Crea una nuova stanza e aggiunge l'utente che ha fatto la richiesta.
	 */
	public void handleCreaStanza(SocketIOClient client, String str, AckRequest ack) {
		String token = getToken(client);
		if (token == null) {
			ack.sendAckData(new RespCreaStanza(false, "Utente non autenticato", -1));
			logger.warn("[Stanza] Tentativo di creare stanza senza autenticazione");
			return;
		}

		try {
			ReqCreaStanza req = JsonHelper.fromJson(str, ReqCreaStanza.class);
			long idStanza = counterId.incrementAndGet();
			int maxGiocatori = Math.max(2, req.maxGiocatori);

			int codiceStanza = nextCodice();
			StanzaPartita stanza = new StanzaPartita(codiceStanza, idStanza, req.nomeStanza, maxGiocatori,
					gestoreConnessioni);
			stanze.put(idStanza, stanza);
			mappaTokenUtenteAStanza.put(token, idStanza);
			stanza.aggiungiUtente(token, client);

			mappaCodiceAIdStanza.put(codiceStanza, idStanza);
			ack.sendAckData(new RespCreaStanza(true, "Stanza creata con successo", codiceStanza));
			logger.info("[Stanza] Creata stanza {} da token {}", idStanza, token);
		} catch (Exception e) {
			logger.error("[Stanza] Errore creazione stanza: {}", e.getMessage());
			ack.sendAckData(new RespCreaStanza(false, "Errore creazione stanza", -1));
		}
	}

	/**
	 * Aggiunge l'utente autenticato alla stanza richiesta.
	 */
	public void handleEntraStanza(SocketIOClient client, String str, AckRequest ack) {
		String token = getToken(client);
		if (token == null) {
			ack.sendAckData(new RespEntraStanza(false, "Utente non autenticato"));
			logger.warn("[Stanza] Tentativo di ingresso in stanza senza autenticazione");
			return;
		}

		try {
			ReqEntraStanza req = JsonHelper.fromJson(str, ReqEntraStanza.class);
			Long idStanza = mappaCodiceAIdStanza.get(req.codiceStanza);
			StanzaPartita stanza = stanze.get(idStanza);

			if (stanza == null) {
				ack.sendAckData(new RespEntraStanza(false, "Stanza non trovata"));
				logger.warn("[Stanza] Tentativo di ingresso in stanza non esistente: codice {}", req.codiceStanza);
				return;
			}

			boolean ok = stanza.aggiungiUtente(token, client);
			if (!ok) {
				ack.sendAckData(new RespEntraStanza(false, "Impossibile entrare in stanza (piena o già in gioco)"));
				return;
			}

			mappaTokenUtenteAStanza.put(token, idStanza);
			ack.sendAckData(new RespEntraStanza(true, "Ingresso in stanza avvenuto con successo"));
			logger.info("[Stanza] Token {} entrato in stanza {} con codice {}", token, idStanza, req.codiceStanza);
		} catch (Exception e) {
			logger.error("[Stanza] Errore ingresso stanza: {}", e.getMessage());
			ack.sendAckData(new RespEntraStanza(false, "Errore ingresso stanza"));
		}
	}

	/**
	 * Rimuove l'utente dalla stanza in cui si trova, se presente.
	 */
	@Deprecated
	private void rimuoviUtenteDaStanza(String tokenUtente) {
		try {
			Long id = mappaTokenUtenteAStanza.remove(tokenUtente);
			if (id != null) {
				StanzaPartita stanza = stanze.get(id);
				if (stanza != null) {
					stanza.rimuoviUtente(tokenUtente);
					logger.info("[Stanza] Utente {} rimosso da stanza {}", tokenUtente, id);
				}
			}
		} catch (Exception e) {
			logger.error("[Stanza] Errore rimozione utente {} da stanza: {}", tokenUtente, e.getMessage());
		}
	}

	public StanzaPartita getStanza(long idStanza) {
		return stanze.get(idStanza);
	}

	public StanzaPartita getStanzaPerToken(String token) {
		if (token == null)
			return null;
		Long idStanza = mappaTokenUtenteAStanza.get(token);
		if (idStanza == null)
			return null;
		return stanze.get(idStanza);
	}

	/**
	 * Permette all'utente di abbandonare la stanza in cui si trova.
	 * @param client Il client che invia la richiesta
	 * @param ack L'oggetto per inviare l'acknowledgment
	 */
	public void handleAbbandonaStanza(SocketIOClient client, AckRequest ack) {
		String token = getToken(client);
		if (token == null) {
			ack.sendAckData(new RespAbbandonaStanza(false, "Utente non autenticato"));
			logger.warn("[Stanza] Tentativo di abbandono stanza senza autenticazione");
			return;
		}

		Long idStanza = mappaTokenUtenteAStanza.remove(token);
		if (idStanza == null) {
			ack.sendAckData(new RespAbbandonaStanza(false, "Utente in nessuna stanza"));
			logger.warn("[Stanza] Utente {} non è in nessuna stanza", token);
			return;
		}

		StanzaPartita stanza = stanze.get(idStanza);
		if (stanza == null) {
			ack.sendAckData(new RespAbbandonaStanza(false, "Stanza non trovata"));
			logger.warn("[Stanza] Stanza {} non trovata per utente {}", idStanza, token);
			return;
		}

		stanza.rimuoviUtente(token);
		if (stanza.isVuota()) {
			stanze.remove(idStanza);
			mappaCodiceAIdStanza.remove(stanza.getCodice());
			logger.info("[Stanza] Stanza {} rimossa perché vuota", idStanza);
		}

		ack.sendAckData(new RespAbbandonaStanza(true, "Abbandono stanza avvenuto con successo"));
		logger.info("[Stanza] Utente {} ha abbandonato la stanza {}", token, idStanza);
	}

	private String getToken(SocketIOClient client) {
		Object tokenObj = client.get("token");
		return tokenObj != null ? tokenObj.toString() : null;
	}

//	private void removeStanza(long idStanza) {
//		try {
//			StanzaPartita stanza = stanze.remove(idStanza);
//			if (stanza != null) {
//				// Rimuovi tutti i token degli utenti nella stanza
//				for (String token : stanza.getUtenti()) {
//					mappaTokenUtenteAStanza.remove(token);
//				}
//				// Rimuovi il codice della stanza
//				mappaCodiceAIdStanza.values().removeIf(id -> id == idStanza);
//				logger.info("[Stanza] Stanza {} rimossa", idStanza);
//			}
//		} catch (Exception e) {
//			logger.error("[Stanza] Errore rimozione stanza {}: {}", idStanza, e.getMessage());
//		}
//		
//	}

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
