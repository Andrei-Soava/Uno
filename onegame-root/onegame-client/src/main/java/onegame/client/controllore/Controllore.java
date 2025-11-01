package onegame.client.controllore;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;

public abstract class Controllore {
	protected ClientSocket cs;
	protected ConnectionMonitor cm;
	

	protected Controllore(ClientSocket cs, ConnectionMonitor cm) {
		this.cs = cs;
		this.cm = cm;
	}

}
