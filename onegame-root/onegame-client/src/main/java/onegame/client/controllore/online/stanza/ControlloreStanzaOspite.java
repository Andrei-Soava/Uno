package onegame.client.controllore.online.stanza;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;

public class ControlloreStanzaOspite extends ControlloreStanza {

	public ControlloreStanzaOspite(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
		super(vs, cs, cm);
	}

	@Override
	public void attendiInizioPartita() {
		
	}

}
