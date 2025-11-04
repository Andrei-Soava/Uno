package onegame.net;

import onegame.modello.carte.Colore;
import onegame.modello.carte.TipoSpeciale;

public class CartaDTO {
	public boolean isCartaNumero;
	public int numero;
	public TipoSpeciale tipo;
	public Colore colore;

	public CartaDTO() {
	}

	public CartaDTO(boolean isCartaNumero, int numero, TipoSpeciale tipo, Colore colore) {
		this.isCartaNumero = isCartaNumero;
		this.numero = numero;
		this.tipo = tipo;
		this.colore = colore;
	}

	@Override
	public String toString() {
		return "CartaDTO [isCartaNumero=" + isCartaNumero + ", numero=" + numero + ", "
				+ (tipo != null ? "tipo=" + tipo + ", " : "") + (colore != null ? "colore=" + colore : "") + "]";
	}
	
}
