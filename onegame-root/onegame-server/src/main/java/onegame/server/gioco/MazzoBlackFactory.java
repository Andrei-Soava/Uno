package onegame.server.gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import onegame.modello.carte.Colore;

public class MazzoBlackFactory implements MazzoFactory {

	private static MazzoBlackFactory instance = new MazzoBlackFactory();

	private MazzoBlackFactory() {
	}

	public static MazzoBlackFactory getInstance() {
		return instance;
	}

	@Override
	public List<CartaNET> creaCarte() {
		List<CartaNET> all = new ArrayList<>();
		for (Colore c : Colore.values()) {
			if (c == Colore.NERO)
				continue;
			for (int i = 0; i <= 3; i++) {
				all.add(CartaNET.numero(c, i));
			}
		}
		for (int i = 0; i < 20; i++) {
			all.add(CartaNET.cambioColore());
		}
		for (int i = 0; i < 20; i++) {
			all.add(CartaNET.pescaQuattro());
		}
		Collections.shuffle(all);
		return all;
	}
}
