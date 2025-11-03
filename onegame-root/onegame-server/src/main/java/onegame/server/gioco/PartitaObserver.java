package onegame.server.gioco;


@FunctionalInterface
public interface PartitaObserver {
	public void partitaAggiornata(CartaNET cartaPescata);
}
