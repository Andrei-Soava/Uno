package modello;

import modello.carte.Carta;

/**
 * interfaccia partita implementata da classe partita e da fornire a giocatore (contiene uniche azioni "legali" da giocatore)
 */
public interface PartitaIF {
	public abstract Carta pescaCarta(); 
	public abstract void giocaCarta(Carta c);
}
