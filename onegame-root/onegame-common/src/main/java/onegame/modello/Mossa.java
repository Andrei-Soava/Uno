package onegame.modello;

import onegame.modello.carte.Carta;

/**
 * classe ausiliaria che serve durante l'esecuzione della partita 
 * per gestire le scelte dei giocatori
 */
public class Mossa {
	public enum TipoMossa {PESCA, GIOCA_CARTA, SCEGLI_COLORE};
	private TipoMossa tipo;
	private Carta cartaScelta;
	
	/**
	 * 
	 * @param tipo tipo della mossa scelto dal giocatore 
	 * 		o generato dalla partita (per mosse estendibili)
	 * @param cartaScelta carta provata dal giocatore
	 * 		nel caso di PESCA, cartaScelta è null
	 */
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
