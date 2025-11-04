package onegame.server.gioco;

/**
 * Interfaccia per osservare gli aggiornamenti di una partita.
 */
@FunctionalInterface
public interface PartitaObserver {
	/**
	 * Metodo chiamato quando la partita viene aggiornata.
	 * @param cartaPescata la carta pescata durante l'aggiornamento della partita
	 */
	public void partitaAggiornata(CartaNET cartaPescata);
}
