package onegame.modello.net;

import onegame.modello.carte.Colore;
import onegame.modello.carte.CartaSpeciale.TipoSpeciale;


public class CartaDTO {
    public int numero;
    public TipoSpeciale tipo;
    public Colore colore;

    public CartaDTO() {}

	public CartaDTO(int numero, TipoSpeciale tipo, Colore colore) {
		this.numero = numero;
		this.tipo = tipo;
		this.colore = colore;
	}
}
