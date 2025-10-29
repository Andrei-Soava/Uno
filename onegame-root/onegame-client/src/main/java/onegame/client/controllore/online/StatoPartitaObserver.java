package onegame.client.controllore.online;

import onegame.modello.net.messaggi.MessaggiGioco.MessStatoPartita;

public interface StatoPartitaObserver {
	public void inizioPartita(MessStatoPartita stato);

	public void aggiornaPartita(MessStatoPartita stato);

	public void finePartita(MessStatoPartita stato);
}
