package onegame.server.gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import onegame.modello.carte.Colore;

public class MazzoONEFactory implements MazzoFactory {

	@Override
	public List<CartaNET> creaCarte() {
		List<CartaNET> all = new ArrayList<>();
		for (Colore c : Colore.values()) {
			if (c == Colore.NERO)
				continue;
			for (int i = 0; i <= 9; i++) {
				all.add(CartaNET.numero(c, i));
				if (i != 0)
					all.add(CartaNET.numero(c, i));
			}
			all.add(CartaNET.skip(c));
			all.add(CartaNET.skip(c));
			all.add(CartaNET.cambioGiro(c));
			all.add(CartaNET.cambioGiro(c));
			all.add(CartaNET.pescaDue(c));
			all.add(CartaNET.pescaDue(c));
		}
		for (int i = 0; i < 4; i++)
			all.add(CartaNET.cambioColore());
		for (int i = 0; i < 4; i++)
			all.add(CartaNET.pescaQuattro());
		Collections.shuffle(all);
		return all;
	}
}
