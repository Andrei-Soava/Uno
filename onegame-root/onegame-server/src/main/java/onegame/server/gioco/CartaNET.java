package onegame.server.gioco;

import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
import onegame.modello.carte.Colore;

/**
 * Rappresenta una carta di ONE
 */
public class CartaNET {

	public final boolean isCartaNumero;
	public final int numero;
	public final TipoSpeciale tipo;
	public final Colore colore;

	private static CartaNET[][] cacheCarteNumeri = new CartaNET[10][Colore.values().length];
	private static CartaNET[][] cacheCarteSpeciali = new CartaNET[TipoSpeciale.values().length][Colore.values().length];

	private CartaNET(boolean isCartaNumero, int numero, TipoSpeciale tipo, Colore colore) {
		this.isCartaNumero = isCartaNumero;
		this.numero = numero;
		this.tipo = tipo;
		this.colore = colore;
	}

	static {
		// Inizializza la cache delle carte numero
		for (int n = 0; n <= 9; n++) {
			for (Colore c : Colore.values()) {
				cacheCarteNumeri[n][c.ordinal()] = new CartaNET(true, n, null, c);
			}
		}
		// Inizializza la cache delle carte speciali
		for (TipoSpeciale t : TipoSpeciale.values()) {
			for (Colore c : Colore.values()) {
				cacheCarteSpeciali[t.ordinal()][c.ordinal()] = new CartaNET(false, -1, t, c);
			}
		}
	}

	public static CartaNET numero(Colore colore, int num) {
		return cacheCarteNumeri[num][colore.ordinal()];
	}

	public static CartaNET skip(Colore colore) {
		return cacheCarteSpeciali[TipoSpeciale.BLOCCA.ordinal()][colore.ordinal()];
	}

	public static CartaNET cambioGiro(Colore colore) {
		return cacheCarteSpeciali[TipoSpeciale.INVERTI.ordinal()][colore.ordinal()];
	}

	public static CartaNET pescaDue(Colore colore) {
		return cacheCarteSpeciali[TipoSpeciale.PIU_DUE.ordinal()][colore.ordinal()];
	}

	public static CartaNET cambioColore() {
		return cacheCarteSpeciali[TipoSpeciale.JOLLY.ordinal()][Colore.NERO.ordinal()];
	}

	public static CartaNET pescaQuattro() {
		return cacheCarteSpeciali[TipoSpeciale.PIU_QUATTRO.ordinal()][Colore.NERO.ordinal()];
	}

	public static CartaNET cartaSpeciale(Colore colore, TipoSpeciale tipo) {
		return cacheCarteSpeciali[tipo.ordinal()][colore.ordinal()];
	}

	public boolean isCartaNumero() {
		return isCartaNumero;
	}

	public int getNumero() {
		return numero;
	}

	public TipoSpeciale getTipo() {
		return tipo;
	}

	public Colore getColore() {
		return colore;
	}

}