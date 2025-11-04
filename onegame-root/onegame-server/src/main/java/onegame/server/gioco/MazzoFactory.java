package onegame.server.gioco;

import java.util.List;

/**
 * Interfaccia funzionale per la creazione di un mazzo di carte.
 */
@FunctionalInterface
public interface MazzoFactory {
	/**
	 * Crea e restituisce una lista di carte.
	 * @return la lista di carte create
	 */
	public List<CartaNET> creaCarte();
}
