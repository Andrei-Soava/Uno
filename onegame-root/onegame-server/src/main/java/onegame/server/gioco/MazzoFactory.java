package onegame.server.gioco;

import java.util.List;

@FunctionalInterface
public interface MazzoFactory {
	public List<CartaNET> creaCarte();
}
