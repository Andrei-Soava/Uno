package onegame.server;

import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.util.JsonHelper;
import onegame.modello.net.ProtocolloMessaggi.ReqSalvaPartita;
import onegame.modello.net.ProtocolloMessaggi.ReqCaricaPartita;
import onegame.modello.net.ProtocolloMessaggi.ReqEliminaPartita;
import onegame.server.db.PartitaIncompletaDb;
import onegame.server.db.UtenteDb;

/**
 * Gestisce il salvataggio e il caricamento delle partite offline per utenti
 * registrati. Le partite vengono salvate nel DB come stringhe JSON
 * serializzate.
 */
public class GestorePartiteOffline {
	private final PartitaIncompletaDb partitaDb;
	private final UtenteDb utenteDb;
	private final GestoreConnessioni gestoreConnessioni;

	private static final Logger logger = LoggerFactory.getLogger(GestorePartiteOffline.class);

	public GestorePartiteOffline(GestoreConnessioni gestoreConnessioni) {
		this.partitaDb = new PartitaIncompletaDb();
		this.utenteDb = new UtenteDb();
		this.gestoreConnessioni = gestoreConnessioni;
	}

	/**
	 * Salva la partita inviata dal client nel database
	 * @param client Il client che invia la richiesta
	 * @param str La richiesta
	 * @param ackRequest L'oggetto per inviare l'acknowledgment
	 */
	public void handleSalvaPartita(SocketIOClient client, String str, AckRequest ackRequest) {
		Sessione sessione = getSessioneAutenticato(client);
		if (sessione == null) {
			ackRequest.sendAckData(new ProtocolloMessaggi.RespSalvaPartita(false, "Utente non valido"));
			logger.warn("Accesso negato: token non valido o utente anonimo");
			return;
		}

		try {
			ReqSalvaPartita req = JsonHelper.fromJson(str, ReqSalvaPartita.class);
			if (req.nomeSalvataggio == null || req.partitaSerializzata == null || req.nomeSalvataggio.isEmpty()
					|| req.partitaSerializzata.isEmpty()) {
				ackRequest.sendAckData(new ProtocolloMessaggi.RespSalvaPartita(false, "Dati mancanti"));
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			partitaDb.createPartita(utenteId, req.nomeSalvataggio, req.partitaSerializzata);
			ackRequest.sendAckData(new ProtocolloMessaggi.RespSalvaPartita(true, "Salvataggio riuscito"));
			logger.info("Partita salvata: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
		} catch (Exception e) {
			logger.error("Errore durante il salvataggio della partita: {}", e.getMessage());
			ackRequest.sendAckData(new ProtocolloMessaggi.RespSalvaPartita(false, "Errore durante il salvataggio"));
		}
	}

	/**
	 * Carica una partita salvata dal database e la invia al client
	 * @param client Il client che invia la richiesta
	 * @param str La richiesta
	 * @param ackRequest L'oggetto per inviare l'acknowledgment
	 */
	public void handleCaricaPartita(SocketIOClient client, String str, AckRequest ackRequest) {
		Sessione sessione = getSessioneAutenticato(client);
		if (sessione == null) {
			ackRequest.sendAckData(new ProtocolloMessaggi.RespCaricaPartita(false, "", "Utente non valido"));
			logger.warn("Accesso negato: token non valido o utente anonimo");
			return;
		}

		try {
			ReqCaricaPartita req = JsonHelper.fromJson(str, ReqCaricaPartita.class);
			if (req.nomeSalvataggio == null || req.nomeSalvataggio.isBlank()) {
				ackRequest
						.sendAckData(new ProtocolloMessaggi.RespCaricaPartita(false, "", "Nome salvataggio mancante"));
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			String partitaJson = partitaDb.getPartitaByUtenteAndNome(utenteId, req.nomeSalvataggio);
			if (partitaJson != null && !partitaJson.isBlank()) {
				ackRequest.sendAckData(new ProtocolloMessaggi.RespCaricaPartita(true, partitaJson, "Partita caricata"));
				logger.info("Partita caricata: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
			} else {
				ackRequest.sendAckData(new ProtocolloMessaggi.RespCaricaPartita(false, "", "Partita non trovata"));
				logger.warn("Partita non trovata: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
			}
		} catch (SQLException e) {
			logger.error("Errore durante il caricamento per utente {}: {}", sessione.getUsername(), e.getMessage());
			ackRequest
					.sendAckData(new ProtocolloMessaggi.RespCaricaPartita(false, "", "Errore durante il caricamento"));
		}
	}

	/**
	 * Recupera la lista dei salvataggi dell'utente e la invia al client
	 * @param client Il client che invia la richiesta
	 * @param str La richiesta
	 * @param ackRequest L'oggetto per inviare l'acknowledgment
	 */
	public void handleListaSalvataggi(SocketIOClient client, AckRequest ackRequest) {
		Sessione sessione = getSessioneAutenticato(client);
		if (sessione == null) {
			ackRequest.sendAckData(new ProtocolloMessaggi.RespListaPartite(false, null, "Utente non valido"));
			logger.warn("Accesso negato: token non valido o utente anonimo");
			return;
		}

		try {
			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			ArrayList<String> nomi = partitaDb.getPartiteByUtente(utenteId);
			ackRequest.sendAckData(new ProtocolloMessaggi.RespListaPartite(true, nomi, "Lista recuperata"));
			logger.info("Lista salvataggi inviata per utente {}", sessione.getUsername());
		} catch (SQLException e) {
			logger.error("Errore durante il recupero dei salvataggi per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new ProtocolloMessaggi.RespListaPartite(false, null, "Errore durante il recupero"));
		}
	}

	public void handleEliminaSalvataggio(SocketIOClient client, String str, AckRequest ackRequest) {
		Sessione sessione = getSessioneAutenticato(client);
		if (sessione == null) {
			ackRequest.sendAckData(new ProtocolloMessaggi.RespEliminaPartita(false, "Utente non valido"));
			logger.warn("Accesso negato: token non valido o utente anonimo");
			return;
		}

		try {
			ReqEliminaPartita req = JsonHelper.fromJson(str, ReqEliminaPartita.class);
			if (req.nomeSalvataggio == null || req.nomeSalvataggio.isBlank()) {
				ackRequest.sendAckData(new ProtocolloMessaggi.RespEliminaPartita(false, "Nome salvataggio mancante"));
			}
			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			partitaDb.deletePartitaByUtenteAndNome(utenteId, req.nomeSalvataggio);
		} catch (Exception e) {
			logger.error("Errore durante l'eliminazione del salvataggio per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new ProtocolloMessaggi.RespEliminaPartita(false, "Errore durante l'eliminazione"));
		}
	}

	/**
	 * Recupera l'utente autenticato associato al client
	 * @param client Il client
	 * @return L'utente autenticato o null se non valido
	 */
	private Sessione getSessioneAutenticato(SocketIOClient client) {
		String token = client.get("token");
		Sessione sessione = gestoreConnessioni.getSessioneByToken(token);

		if (sessione == null || sessione.isAnonimo()) {
			logger.warn("Accesso negato: token non valido o utente anonimo");
			return null;
		}
		return sessione;
	}
}
