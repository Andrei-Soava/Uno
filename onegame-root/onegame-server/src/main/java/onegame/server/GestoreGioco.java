package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.MossaDTO;
import onegame.modello.net.ProtocolloMessaggi.ReqEffettuaMossa;
import onegame.modello.net.ProtocolloMessaggi.RespEffettuaMossa;
import onegame.modello.net.util.JsonHelper;

public class GestoreGioco {
	private final GestoreStanzePartita gestoreStanze;
	private final GestoreSessioni gestoreSessioni;

	private final static Logger logger = LoggerFactory.getLogger(GestoreGioco.class);

	public GestoreGioco(GestoreStanzePartita gestoreStanze, GestoreSessioni gestoreSessioni) {
		this.gestoreStanze = gestoreStanze;
		this.gestoreSessioni = gestoreSessioni;
	}

	public void handleEffettuaMossa(SocketIOClient client, String str, AckRequest ack) {
		try {
			String token = client.get("token");
			Sessione sessione = gestoreSessioni.getSessioneByClient(client);

			if (sessione == null) {
				ack.sendAckData(new RespEffettuaMossa(false, "Utente non valido"));
				logger.warn("Accesso negato");
				return;
			}

			ReqEffettuaMossa req = JsonHelper.fromJson(str, ReqEffettuaMossa.class);
			MossaDTO mossa = req.mossa;
			StanzaPartita stanza = gestoreStanze.getStanzaPerToken(token);
			
			stanza.riceviMossa(token, mossa);
			ack.sendAckData(new RespEffettuaMossa(true, "Mossa effettuata con successo"));
			logger.info("Mossa ricevuta da utente {}: {}", sessione.getUsername(), mossa);
		} catch (Exception e) {
			logger.error("Errore durante l'elaborazione della mossa", e);
			ack.sendAckData(new RespEffettuaMossa(false, "Errore interno del server"));
		}

	}
}
