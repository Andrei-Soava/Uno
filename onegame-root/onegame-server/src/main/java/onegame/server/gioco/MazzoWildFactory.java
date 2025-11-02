package onegame.server.gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import onegame.modello.carte.Colore;

public class MazzoWildFactory implements MazzoFactory {

	private static MazzoWildFactory instance = new MazzoWildFactory();

	private MazzoWildFactory() {
	}

	public static MazzoWildFactory getInstance() {
		return instance;
	}

	@Override
	public List<CartaNET> creaCarte() {
		List<CartaNET> all = new ArrayList<>();
		for (Colore c : Colore.values()) {
			if (c == Colore.NERO)
				continue;
			for (int i = 0; i <= 8; i++) {
				all.add(CartaNET.numero(c, i));
			}
		}
		for (int i = 0; i < 40; i++) {
			all.add(CartaNET.cambioColore());
		}
		Collections.shuffle(all);
		return all;
	}
}
