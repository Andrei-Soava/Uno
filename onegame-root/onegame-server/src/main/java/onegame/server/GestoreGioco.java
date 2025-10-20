package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;

import onegame.modello.net.MossaDTO;
import onegame.modello.net.messaggi.Messaggi.ReqEffettuaMossa;
import onegame.modello.net.messaggi.Messaggi.RespEffettuaMossa;
import onegame.modello.net.util.JsonHelper;

public class GestoreGioco {
	private final GestoreStanzePartita gestoreStanze;

	private static final Logger logger = LoggerFactory.getLogger(GestoreGioco.class);

	public GestoreGioco(GestoreStanzePartita gestoreStanze) {
		this.gestoreStanze = gestoreStanze;
	}

	public void handleEffettuaMossa(Sessione sessione, String str, AckRequest ack) {
		if (sessione == null) {
			ack.sendAckData(new RespEffettuaMossa(false, "Utente non valido"));
			logger.warn("Accesso negato");
			return;
		}

		try {
			ReqEffettuaMossa req = JsonHelper.fromJson(str, ReqEffettuaMossa.class);
			MossaDTO mossa = req.mossa;

			StanzaPartita stanza = gestoreStanze.getStanzaPerSessione(sessione);
			if (stanza == null) {
				ack.sendAckData(new RespEffettuaMossa(false, "Stanza non trovata"));
				logger.warn("Sessione {} non è associata a nessuna stanza", sessione.getToken());
				return;
			}

			stanza.riceviMossa(sessione, mossa);
			ack.sendAckData(new RespEffettuaMossa(true, "Mossa effettuata con successo"));
			logger.info("Mossa ricevuta da utente {}: {}", sessione.getUsername(), mossa);
		} catch (Exception e) {
			logger.error("Errore durante l'elaborazione della mossa", e);
			ack.sendAckData(new RespEffettuaMossa(false, "Errore interno del server"));
		}
	}
}
