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
public abstract class GestoreStanze<Stanzz extends Stanza> implements SessioneObserver {

	private final GestoreSessioni gestoreSessioni;

	// Mappa idStanza → Stanza
	protected final Map<Long, Stanzz> stanze = new ConcurrentHashMap<>();
	// Mappa tokenUtente → idStanza
	protected final Map<String, Long> mappaTokenUtenteAStanza = new ConcurrentHashMap<>();
	// Mappa codiceStanza → idStanza
	private final Map<Integer, Long> mappaCodiceAIdStanza = new ConcurrentHashMap<>();

	private final AtomicLong counterId = new AtomicLong();
	private final AtomicInteger counterCodice = new AtomicInteger(MIN_CODICE);

	private static final int MIN_CODICE = 1000000;
	private static final int MAX_CODICE = 9999999;

	private static final Logger logger = LoggerFactory.getLogger(GestoreStanze.class);

	public GestoreStanze(GestoreSessioni gestoreSessioni) {
		this.gestoreSessioni = gestoreSessioni;
	}

	/**
	 * Crea una nuova stanza e aggiunge l'utente che ha fatto la richiesta.
	 */
	public void handleCreaStanza(SocketIOClient client, String str, AckRequest ack) {
		String token = getToken(client);
		if (token == null) {
			ack.sendAckData(new RespCreaStanza(false, "Utente non autenticato", -1));
			logger.warn("Tentativo di creare stanza senza autenticazione");
			return;
		}

		try {
			ReqCreaStanza req = JsonHelper.fromJson(str, ReqCreaStanza.class);
			long idStanza = counterId.incrementAndGet();
			int maxGiocatori = Math.max(2, req.maxGiocatori);

			int codiceStanza = nextCodice();
			Stanzz stanza = creaStanza(codiceStanza, idStanza, req.nomeStanza, maxGiocatori, gestoreSessioni);

			if (stanza == null) {
				ack.sendAckData(new RespCreaStanza(false, "Errore creazione stanza", -1));
				logger.error("Creazione stanza fallita per token {}", token);
				return;
			}

			stanze.put(idStanza, stanza);
			mappaTokenUtenteAStanza.put(token, idStanza);
			stanza.aggiungiUtente(token);

			mappaCodiceAIdStanza.put(codiceStanza, idStanza);
			ack.sendAckData(new RespCreaStanza(true, "Stanza creata con successo", codiceStanza));
			logger.info("Creata stanza {} da token {}", idStanza, token);
		} catch (Exception e) {
			logger.error("Errore creazione stanza: {}", e.getMessage());
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
			logger.warn("Tentativo di ingresso in stanza senza autenticazione");
			return;
		}

		try {
			ReqEntraStanza req = JsonHelper.fromJson(str, ReqEntraStanza.class);
			Long idStanza = mappaCodiceAIdStanza.get(req.codiceStanza);
			Stanzz stanza = stanze.get(idStanza);

			if (stanza == null) {
				ack.sendAckData(new RespEntraStanza(false, "Stanza non trovata"));
				logger.warn("Tentativo di ingresso in stanza non esistente: codice {}", req.codiceStanza);
				return;
			}

			boolean ok = stanza.aggiungiUtente(token);
			if (!ok) {
				ack.sendAckData(new RespEntraStanza(false, "Impossibile entrare in stanza (piena o già in gioco)"));
				return;
			}

			mappaTokenUtenteAStanza.put(token, idStanza);
			ack.sendAckData(new RespEntraStanza(true, "Ingresso in stanza avvenuto con successo"));
			logger.info("Token {} entrato in stanza {} con codice {}", token, idStanza, req.codiceStanza);
		} catch (Exception e) {
			logger.error("Errore ingresso stanza: {}", e.getMessage());
			ack.sendAckData(new RespEntraStanza(false, "Errore ingresso stanza"));
		}
	}

	public void rimuoviUtenteDaSistema(String token) {
		Long idStanza = mappaTokenUtenteAStanza.remove(token);
		if (idStanza == null)
			return;

		Stanzz stanza = stanze.get(idStanza);
		if (stanza == null)
			return;

		if (stanza.rimuoviUtente(token)) {
			logger.info("Utente {} rimosso da stanza {}", token, idStanza);
		}

		if (stanza.isVuota()) {
			stanze.remove(idStanza);
			mappaCodiceAIdStanza.remove(stanza.getCodice());
			logger.info("Stanza {} rimossa perché vuota", idStanza);
		}
	}

	public Stanzz getStanza(long idStanza) {
		return stanze.get(idStanza);
	}

	public Stanzz getStanzaPerToken(String token) {
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
			logger.warn("Tentativo di abbandono stanza senza autenticazione");
			return;
		}

		if (!mappaTokenUtenteAStanza.containsKey(token)) {
			ack.sendAckData(new RespAbbandonaStanza(false, "Utente in nessuna stanza"));
			logger.warn("Utente {} non è in nessuna stanza", token);
			return;
		}

		rimuoviUtenteDaSistema(token);
		ack.sendAckData(new RespAbbandonaStanza(true, "Abbandono stanza avvenuto con successo"));
	}

	private String getToken(SocketIOClient client) {
		Object tokenObj = client.get("token");
		return tokenObj != null ? tokenObj.toString() : null;
	}

	@Override
	public void onSessioneInattiva(Sessione sessione) {
		rimuoviUtenteDaSistema(sessione.getToken());
	}

	protected abstract Stanzz creaStanza(int codice, long id, String nome, int maxUtenti, GestoreSessioni gestoreSessioni);

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
