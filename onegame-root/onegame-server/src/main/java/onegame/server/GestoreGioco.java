package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.MossaDTO;
import onegame.modello.net.util.JsonHelper;

public class GestoreGioco {
	private final static Logger logger = LoggerFactory.getLogger(GestoreGioco.class);
	private final GestoreStanze gestoreStanze;
	
	public GestoreGioco(GestoreStanze gestoreStanze) {
		this.gestoreStanze = gestoreStanze;
	}
	
	public void handleEffettuaMossa(SocketIOClient client, String str, AckRequest ack) {
//		Object tokenObj = client.get("token");
//	    if (tokenObj == null) {
//	        client.sendEvent("partita:invalid", "Non autenticato");
//	        return;
//	    }
//
//	    String token = tokenObj.toString();
//	    StanzaPartita stanza = gestoreStanze.getStanzaPerToken(token);
//	    if (stanza == null) {
//	        client.sendEvent("partita:invalid", "Non sei in alcuna stanza");
//	        return;
//	    }
//
//	    try {
//	        MossaDTO mossa = JsonHelper.fromJson(payloadJson, MossaDTO.class);
//	        stanza.riceviMossa(token, mossa);
//	        ack.sendAckData("OK");
//	    } catch (Exception e) {
//	        logger.error("[SERVER] Errore parsing mossa: {}", e.getMessage());
//	        client.sendEvent("partita:invalid", "Formato mossa non valido");
//	        ack.sendAckData("ERRORE");
//	    }
	}
}
