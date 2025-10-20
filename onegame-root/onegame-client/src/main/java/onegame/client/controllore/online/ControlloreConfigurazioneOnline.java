package onegame.client.controllore.online;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaConfigurazioneOnline;

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
		
		aspettaCreazioneStanza();
	}
	
	public void aspettaCreazioneStanza() {
		vco.configuraPartita((nomeStanza,numeroGiocatori)-> {
			cs.creaStanza(nomeStanza, numeroGiocatori, respCreaStanza -> {
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
