package onegame.client.controllore.online;

import onegame.modello.net.StatoStanzaDTO;

public interface StatoStanzaObserver {
	public void aggiornaStanza(StatoStanzaDTO stato);
}
