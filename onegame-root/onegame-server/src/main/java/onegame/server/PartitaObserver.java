package onegame.server;

import java.util.List;
import java.util.Map;

import onegame.modello.carte.Carta;
import onegame.modello.giocatori.Giocatore;

@FunctionalInterface
public interface PartitaObserver {
	public void partitaAggiornata(Map<Giocatore, List<Carta>> cartePescate);
}
