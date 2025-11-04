package onegame.server;

import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;

import onegame.modello.net.messaggi.MessaggiSalvataggioPartite.*;
import onegame.modello.net.util.JsonHelper;
import onegame.server.db.PartitaIncompletaDb;
import onegame.server.db.UtenteDb;

/**
 * Gestisce il salvataggio e il caricamento delle partite offline per utenti registrati.
 */
public class GestorePartiteOffline {
	private final PartitaIncompletaDb partitaDb;
	private final UtenteDb utenteDb;

	private static final Logger logger = LoggerFactory.getLogger(GestorePartiteOffline.class);

	public GestorePartiteOffline() {
		this.partitaDb = new PartitaIncompletaDb();
		this.utenteDb = new UtenteDb();
	}

	/**
	 * Gestisce la richiesta di salvataggio della partita da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede il salvataggio
	 * @param str la stringa JSON della richiesta
	 * @param ackRequest l'oggetto per inviare la risposta di ack
	 */
	public void handleSalvaPartita(Sessione sessione, String str, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespCreaSalvataggio(false, -1, "Utente non valido"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			ReqCreaSalvataggio req = JsonHelper.fromJson(str, ReqCreaSalvataggio.class);
			if (req.nomeSalvataggio == null || req.partitaSerializzata == null || req.nomeSalvataggio.isBlank()
					|| req.partitaSerializzata.isBlank()) {
				ackRequest.sendAckData(new RespCreaSalvataggio(false, -1, "Dati mancanti"));
				return;
			}
			if (!isNomeSalvataggioValido(req.nomeSalvataggio)) {
				ackRequest.sendAckData(new RespCreaSalvataggio(false, -1, "Nome salvataggio non valido"));
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			boolean success;
			boolean esiste = partitaDb.esistePartita(utenteId, req.nomeSalvataggio);

			if (esiste && req.sovrascriviSeEsiste) {
				success = partitaDb.updatePartita(utenteId, req.nomeSalvataggio, req.partitaSerializzata);
				if (success) {
					logger.info("Salvataggio esistente {} sovrascritto per utente {}", req.nomeSalvataggio,
							sessione.getUsername());
					ackRequest.sendAckData(new RespCreaSalvataggio(success, 1, "Salvataggio sovrascritto"));

				} else {
					logger.warn("Errore nella sovrascrittura del salvataggio {} per utente {}", req.nomeSalvataggio,
							sessione.getUsername());
					ackRequest.sendAckData(new RespCreaSalvataggio(success, -1, "Errore nella sovrascrittura"));
				}
			} else if (esiste && !req.sovrascriviSeEsiste) {
				logger.warn("Tentativo di creare salvataggio esistente {} per utente {}", req.nomeSalvataggio,
						sessione.getUsername());
				ackRequest.sendAckData(new RespCreaSalvataggio(false, -1, "Salvataggio già esistente"));
			} else {
				success = partitaDb.createPartita(utenteId, req.nomeSalvataggio, req.partitaSerializzata);
				if (success) {
					logger.info("Nuovo salvataggio {} creato per utente {}", req.nomeSalvataggio,
							sessione.getUsername());
					ackRequest.sendAckData(new RespCreaSalvataggio(success, 0, "Salvataggio creato"));
				} else {
					logger.warn("Errore nella creazione del salvataggio {} per utente {}", req.nomeSalvataggio,
							sessione.getUsername());
					ackRequest.sendAckData(new RespCreaSalvataggio(success, -1, "Errore nella creazione"));
				}
			}
		} catch (Exception e) {
			logger.error("Errore durante il salvataggio della partita: {}", e.getMessage());
			ackRequest.sendAckData(new RespCreaSalvataggio(false, -1, "Errore durante il salvataggio"));
		}
	}

	/**
	 * Gestisce la richiesta di salvataggio della partita da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede il salvataggio
	 * @param str la stringa JSON della richiesta
	 * @param ackRequest l'oggetto per inviare la risposta di ack
	 */
	public void handleCaricaPartita(Sessione sessione, String str, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespCaricaSalvataggio(false, "", "Utente non valido"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			ReqCaricaSalvataggio req = JsonHelper.fromJson(str, ReqCaricaSalvataggio.class);
			if (req.nomeSalvataggio == null || req.nomeSalvataggio.isBlank()) {
				ackRequest.sendAckData(new RespCaricaSalvataggio(false, "", "Nome salvataggio mancante"));
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			String partitaJson = partitaDb.getPartitaByUtenteAndNome(utenteId, req.nomeSalvataggio);
			if (partitaJson != null && !partitaJson.isBlank()) {
				ackRequest.sendAckData(new RespCaricaSalvataggio(true, partitaJson, "Partita caricata"));
				logger.info("Partita caricata: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
			} else {
				ackRequest.sendAckData(new RespCaricaSalvataggio(false, "", "Partita non trovata"));
				logger.warn("Partita non trovata: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
			}
		} catch (SQLException e) {
			logger.error("Errore durante il caricamento per utente {}: {}", sessione.getUsername(), e.getMessage());
			ackRequest.sendAckData(new RespCaricaSalvataggio(false, "", "Errore durante il caricamento"));
		}
	}

	/**
	 * Gestisce la richiesta di lista dei salvataggi da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede la lista
	 * @param ackRequest l'oggetto per inviare la risposta di ack
	 */
	public void handleListaSalvataggi(Sessione sessione, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespListaSalvataggi(false, null, "Utente non valido"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			ArrayList<String> nomi = partitaDb.getPartiteByUtente(utenteId);
			ackRequest.sendAckData(new RespListaSalvataggi(true, nomi, "Lista recuperata"));
			logger.info("Lista salvataggi inviata per utente {}", sessione.getUsername());
		} catch (SQLException e) {
			logger.error("Errore durante il recupero dei salvataggi per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new RespListaSalvataggi(false, null, "Errore durante il recupero"));
		}
	}

	/**
	 * Gestisce la richiesta di eliminazione di un salvataggio da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede l'eliminazione
	 * @param str la stringa JSON della richiesta
	 * @param ackRequest l'oggetto per inviare la risposta di ack
	 */
	public void handleEliminaSalvataggio(Sessione sessione, String str, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespEliminaSalvataggio(false, "Utente non valido"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			ReqEliminaSalvataggio req = JsonHelper.fromJson(str, ReqEliminaSalvataggio.class);
			if (req.nomeSalvataggio == null || req.nomeSalvataggio.isBlank()) {
				ackRequest.sendAckData(new RespEliminaSalvataggio(false, "Nome salvataggio mancante"));
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			boolean success = partitaDb.deletePartitaByUtenteAndNome(utenteId, req.nomeSalvataggio);
			ackRequest.sendAckData(
					new RespEliminaSalvataggio(success, success ? "Salvataggio eliminato" : "Salvataggio non trovato"));
			logger.info("Eliminazione salvataggio {} per utente {}: {}", req.nomeSalvataggio, sessione.getUsername(),
					success ? "successo" : "fallita");
		} catch (Exception e) {
			logger.error("Errore durante l'eliminazione del salvataggio per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new RespEliminaSalvataggio(false, "Errore durante l'eliminazione"));
		}
	}

	/**
	 * Gestisce la richiesta di rinomina di un salvataggio da parte di un utente.
	 * @param sessione la sessione dell'utente che richiede la rinomina
	 * @param str la stringa JSON della richiesta
	 * @param ackRequest l'oggetto per inviare la risposta di ack
	 */
	public void handleRinominaSalvataggio(Sessione sessione, String str, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Utente non valido"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			ReqRinominaSalvataggio req = JsonHelper.fromJson(str, ReqRinominaSalvataggio.class);
			if (req.nomeVecchio == null || req.nomeNuovo == null || req.nomeVecchio.isBlank()
					|| req.nomeNuovo.isBlank()) {
				ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Dati mancanti"));
				return;
			}
			if (req.nomeVecchio.equals(req.nomeNuovo)) {
				ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Il nome nuovo deve essere diverso"));
				logger.warn("Tentativo di rinomina salvataggio con nomi identici per utente {}",
						sessione.getUsername());
				return;
			}

			if (!isNomeSalvataggioValido(req.nomeNuovo)) {
				ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Nome salvataggio non valido"));
				logger.warn("Tentativo di rinomina salvataggio con nome non valido per utente {}",
						sessione.getUsername());
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());

			if (partitaDb.esistePartita(utenteId, req.nomeNuovo)) {
				ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Nome già esistente"));
				logger.warn("Tentativo di rinomina con nome già esistente per utente {}", sessione.getUsername());
				return;
			}

			boolean success = partitaDb.rinominaSalvataggio(utenteId, req.nomeVecchio, req.nomeNuovo);
			ackRequest.sendAckData(
					new RespRinominaSalvataggio(success, success ? "Rinomina riuscita" : "Rinomina fallita"));
			logger.info("Rinomina salvataggio {} → {} per utente {}: {}", req.nomeVecchio, req.nomeNuovo,
					sessione.getUsername(), success ? "successo" : "fallita");
		} catch (Exception e) {
			logger.error("Errore durante la rinomina del salvataggio per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Errore durante la rinomina"));
		}
	}

	/**
	 * Verifica se il nome del salvataggio è valido.
	 * @param nomeSalvataggio Il nome del salvataggio da verificare
	 * @return true se il nome è valido, false altrimenti
	 */
	private boolean isNomeSalvataggioValido(String nomeSalvataggio) {
		return (nomeSalvataggio != null && !nomeSalvataggio.trim().isEmpty() && nomeSalvataggio.length() <= 100);
	}

}
