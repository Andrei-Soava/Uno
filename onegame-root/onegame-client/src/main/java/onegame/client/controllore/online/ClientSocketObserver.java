package onegame.client.controllore.online;

import onegame.modello.net.StatoStanzaDTO;

public interface ClientSocketObserver {
	public void aggiornaStanza(StatoStanzaDTO stato);
}
