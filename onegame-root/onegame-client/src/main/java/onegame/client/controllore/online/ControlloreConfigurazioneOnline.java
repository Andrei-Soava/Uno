package onegame.client.controllore.online;

import javafx.application.Platform;
import onegame.client.controllore.Controllore;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaConfigurazioneOnline;

public class ControlloreConfigurazioneOnline extends Controllore {
	private VistaConfigurazioneOnline vco;
	
	public ControlloreConfigurazioneOnline(VistaConfigurazioneOnline vco, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vco=vco;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vco.mostraHome();
	        }
	    });
		
		aspettaCreazioneStanza();
	}
	
	private void aspettaCreazioneStanza() {
		vco.configuraPartita((nomeStanza,numeroGiocatori)-> {
			cs.creaStanza(nomeStanza, numeroGiocatori, respCreaStanza -> {
				Platform.runLater(()->{
					if(respCreaStanza.success) {
						vco.mostraStanza(Integer.toString(respCreaStanza.codiceStanza));
					} else {
						aspettaCreazioneStanza();
						return;
					}					
				});
			});
		});
	}
}
