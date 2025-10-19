package onegame.modello.net;

import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;

public class DTOUtils {
	public static CartaDTO creaCartaDTO(Carta carta) {
		if (carta == null) {
			return null;
		}
		if (carta instanceof CartaNumero cartaNumero) {
			return new CartaDTO(true, cartaNumero.getNumero(), null, cartaNumero.getColore());
		}
		if (carta instanceof CartaSpeciale cartaSpeciale) {
			return new CartaDTO(false, -1, cartaSpeciale.getTipo(), cartaSpeciale.getColore());
		}

		return null;
	}
}
