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

	public void handleSalvaPartita(Sessione sessione, String str, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespCreaSalvataggio(false, "Utente non valido"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			ReqCreaSalvataggio req = JsonHelper.fromJson(str, ReqCreaSalvataggio.class);
			if (req.nomeSalvataggio == null || req.partitaSerializzata == null || req.nomeSalvataggio.isBlank()
					|| req.partitaSerializzata.isBlank()) {
				ackRequest.sendAckData(new RespCreaSalvataggio(false, "Dati mancanti"));
				return;
			}

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			partitaDb.createPartita(utenteId, req.nomeSalvataggio, req.partitaSerializzata);
			ackRequest.sendAckData(new RespCreaSalvataggio(true, "Salvataggio riuscito"));
			logger.info("Partita salvata: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
		} catch (Exception e) {
			logger.error("Errore durante il salvataggio della partita: {}", e.getMessage());
			ackRequest.sendAckData(new RespCreaSalvataggio(false, "Errore durante il salvataggio"));
		}
	}

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
			partitaDb.deletePartitaByUtenteAndNome(utenteId, req.nomeSalvataggio);
			ackRequest.sendAckData(new RespEliminaSalvataggio(true, "Salvataggio eliminato"));
			logger.info("Salvataggio eliminato: {} per utente {}", req.nomeSalvataggio, sessione.getUsername());
		} catch (Exception e) {
			logger.error("Errore durante l'eliminazione del salvataggio per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new RespEliminaSalvataggio(false, "Errore durante l'eliminazione"));
		}
	}

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

			long utenteId = utenteDb.getIdByUsername(sessione.getUsername());
			partitaDb.rinominaSalvataggio(utenteId, req.nomeVecchio, req.nomeNuovo);
			ackRequest.sendAckData(new RespRinominaSalvataggio(true, "Rinomina riuscita"));
			logger.info("Salvataggio rinominato: {} → {} per utente {}", req.nomeVecchio, req.nomeNuovo,
					sessione.getUsername());
		} catch (Exception e) {
			logger.error("Errore durante la rinomina del salvataggio per utente {}: {}", sessione.getUsername(),
					e.getMessage());
			ackRequest.sendAckData(new RespRinominaSalvataggio(false, "Errore durante la rinomina"));
		}
	}
}
