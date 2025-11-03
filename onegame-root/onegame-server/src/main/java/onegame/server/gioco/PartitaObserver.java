package onegame.server.gioco;

import java.util.Map;

@FunctionalInterface
public interface PartitaObserver {
	public void partitaAggiornata(Map<GiocatoreNET, CartaNET> cartePescate);
}
