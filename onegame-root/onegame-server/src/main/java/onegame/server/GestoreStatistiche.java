package onegame.server;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;

import onegame.net.messaggi.MessaggiStatistiche.RespCaricaStatistiche;
import onegame.server.db.UtenteDb;

/**
 * Gestore delle statistiche degli utenti
 */
public class GestoreStatistiche {
	private final UtenteDb utenteDb;

	private static final Logger logger = LoggerFactory.getLogger(GestorePartiteOffline.class);

	public GestoreStatistiche() {
		this.utenteDb = new UtenteDb();
	}

	/**
	 * Gestisce la richiesta di caricamento delle statistiche di un utente
	 * @param sessione la sessione dell'utente che richiede le statistiche
	 * @param ackRequest l'oggetto per inviare la risposta di ack
	 */
	public void handleCaricaStatistiche(Sessione sessione, AckRequest ackRequest) {
		if (sessione == null || sessione.isAnonimo()) {
			ackRequest.sendAckData(new RespCaricaStatistiche(false, -1, -1, "Accesso negato"));
			logger.warn("Accesso negato: sessione nulla o utente anonimo");
			return;
		}

		try {
			String username = sessione.getUsername();

			Map<String, Integer> statistiche = utenteDb.getStatisticheUtente(username);
			// @formatter:off
			RespCaricaStatistiche resp = new RespCaricaStatistiche(
					true,
					statistiche.getOrDefault("partite_giocate", -1),
					statistiche.getOrDefault("partite_vinte", -1),
					"Statistiche caricate con successo"
			);
			
			ackRequest.sendAckData(resp);
			logger.info("Statistiche caricate per utente {}: giocate={}, vinte={}",
					username,
					resp.partiteGiocate,
					resp.partiteVinte);
			// @formatter:on
		} catch (SQLException e) {
			logger.error("Errore durante il caricamento per utente {}: {}", sessione.getUsername(), e.getMessage());
			ackRequest.sendAckData(new RespCaricaStatistiche(false, -1, -1, "Errore del server"));
		}
	}
}
