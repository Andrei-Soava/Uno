package onegame.modello.carte;

import java.util.Comparator;
import java.util.Map;

public class CartaComparator implements Comparator<Carta> {
//	@formatter:off
	private Map<Colore, Integer> gerarchiaColori = Map.of(
			Colore.NERO, 0,
			Colore.ROSSO, 1,
			Colore.BLU, 2,
			Colore.GIALLO, 3,
			Colore.VERDE, 4
		);
//	@formatter:on

	/**
	 * Confronta una carta con un'altra in base al colore, secondo una gerarchia:
	 * NERO < ROSSO < BLU < GIALLO < VERDE. Se le carte hanno lo stesso colore, le
	 * carte numero sono confrontate in base al loro numero, mentre le carte
	 * speciali sono confrontate in base al loro tipo.
	 * @param carta1 la prima carta da confrontare
	 * @param carta2 la seconda carta da confrontare
	 */
	@Override
	public int compare(Carta carta1, Carta carta2) {
		int num1 = gerarchiaColori.get(carta1.getColore());
		int num2 = gerarchiaColori.get(carta2.getColore());

		int temp = Integer.compare(num1, num2);

		if (temp != 0) {
			return temp;
		}

		if (carta1 instanceof CartaNumero && carta2 instanceof CartaNumero) {
			int numero1 = ((CartaNumero) carta1).getNumero();
			int numero2 = ((CartaNumero) carta2).getNumero();
			return Integer.compare(numero1, numero2);
		} else if (carta1 instanceof CartaSpeciale && carta2 instanceof CartaSpeciale) {
			TipoSpeciale tipo1 = ((CartaSpeciale) carta1).getTipo();
			TipoSpeciale tipo2 = ((CartaSpeciale) carta2).getTipo();
			return tipo1.compareTo(tipo2);
		} else if (carta1 instanceof CartaNumero) {
			return -1; // Le carte numero precedono le carte speciali
		} else {
			return 1; // Le carte speciali seguono le carte numero
		}
	}
}
