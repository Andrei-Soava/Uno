package onegame.server.gioco;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(colore, isCartaNumero, numero, tipo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartaNET other = (CartaNET) obj;
		return colore == other.colore && isCartaNumero == other.isCartaNumero && numero == other.numero
				&& tipo == other.tipo;
	}

	/**
	 * Verifica se la carta è compatibile con la carta corrente e il colore corrente del gioco.
	 * @param coloreCorrente Il colore corrente del gioco.
	 * @param cartaCorrente La carta di riferimento sul tavolo.
	 * @return true se la carta è compatibile, false altrimenti.
	 */
	public boolean isCartaCompatibile(Colore coloreCorrente, CartaNET cartaCorrente) {
		System.err.println("Colore corrente: " + coloreCorrente);
		if (this.colore == Colore.NERO) {
			System.err.println(1);
			return true;
		}
		if (this.colore == coloreCorrente) {
			System.err.println(2);
			return true;
		}
		if (this.isCartaNumero && this.numero == cartaCorrente.getNumero()) {
			System.err.println(3);
			return true;
		}
		if (!isCartaNumero && !cartaCorrente.isCartaNumero() && tipo == cartaCorrente.getTipo()) {
			System.err.println(4);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "CartaNET [isCartaNumero=" + isCartaNumero + ", numero=" + numero + ", "
				+ (tipo != null ? "tipo=" + tipo + ", " : "") + (colore != null ? "colore=" + colore : "") + "]";
	}

}