package onegame.client.controllore.online.stanza;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;

public class ControlloreStanzaHost extends ControlloreStanza {

	public ControlloreStanzaHost(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
		super(vs, cs, cm);
	}

	@Override
	public void attendiInizioPartita() {
		vs.mostraAvviaBtn();
	}

}
