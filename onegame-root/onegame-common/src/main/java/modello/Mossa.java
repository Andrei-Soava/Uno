package modello;

import modello.carte.Carta;

public class Mossa {
	public enum TipoMossa {PESCA, GIOCA_CARTA, SCEGLI_COLORE};
	private TipoMossa tipo;
	private Carta cartaScelta;
	
	public Mossa(TipoMossa tipo, Carta cartaScelta) {
		this.cartaScelta=cartaScelta;
		this.tipo=tipo;
	}
	
	public Mossa(TipoMossa tipo) {
		this(tipo,null);
	}
	
	public Carta getCartaScelta() {
		return cartaScelta;
	}
	
	public void setCartaScelta(Carta c) {
		this.cartaScelta=c;
	}
	
	public TipoMossa getTipoMossa() {
		return tipo;
	}
	
	public void setTipoMossa(TipoMossa tipo) {
		this.tipo=tipo;
	}
}
