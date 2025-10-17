package onegame.client.controllore.online;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaConfigurazioneOnline;
import onegame.modello.net.ProtocolloMessaggi.RespCreaStanza;
import onegame.modello.net.util.JsonHelper;

public class ControlloreConfigurazioneOnline {
	private VistaConfigurazioneOnline vco;
	private ClientSocket cs;
	
	public ControlloreConfigurazioneOnline(VistaConfigurazioneOnline vco, ClientSocket cs, ConnectionMonitor cm) {
		this.vco=vco;
		this.cs=cs;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vco.mostraHome();
	        }
	    });
	}
	
	public void aspettaCreazioneStanza() {
		vco.configuraPartita(numeroGiocatori-> {
			//richiesta codice della stanza con funzione asincrona
//			String codiceOttenuto="codice";
//			vco.mostraStanza(codiceOttenuto);
			
			cs.creaStanza("Prova", numeroGiocatori, respCreaStanza -> {
				if(respCreaStanza.success) {
					vco.mostraStanza(Integer.toString(respCreaStanza.codiceStanza));
				} else {
					aspettaCreazioneStanza();
					return;
				}
			});
		});
	}
}
