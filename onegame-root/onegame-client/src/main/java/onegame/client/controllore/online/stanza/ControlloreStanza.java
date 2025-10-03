package onegame.client.controllore.online.stanza;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;

public abstract class ControlloreStanza {

	protected VistaStanza vs;
	protected ClientSocket cs;
	
	protected ControlloreStanza(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
		this.vs=vs;
		this.cs=cs;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vs.mostraHome();
	        }
	    });
	}
	
	public abstract void attendiInizioPartita();
}
