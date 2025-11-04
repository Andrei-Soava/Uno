package onegame.client.controllore.online;

import onegame.net.StatoStanzaDTO;

public interface StatoStanzaObserver {
	public void aggiornaStanza(StatoStanzaDTO stato);
}
