package onegame.client.controllore.online;

import onegame.modello.net.StatoPartitaDTO;

public interface StatoPartitaObserver {
	public void inizioPartita(StatoPartitaDTO stato);

	public void aggiornaPartita(StatoPartitaDTO stato);

	public void finePartita(StatoPartitaDTO stato);
}
