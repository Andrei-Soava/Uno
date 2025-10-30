package onegame.server;

import java.util.List;
import java.util.Map;

import onegame.server.gioco.CartaNET;
import onegame.server.gioco.GiocatoreNET;


@FunctionalInterface
public interface PartitaObserver {
	public void partitaAggiornata(Map<GiocatoreNET, List<CartaNET>> cartePescate);
}
