package onegame.modello;

import onegame.modello.carte.Carta;

/**
 * interfaccia partita implementata da classe partita e da fornire a giocatore (contiene uniche azioni "legali" da giocatore)
 */
public interface PartitaIF {
	public abstract Carta pescaCarta(); 
	public abstract boolean tentaGiocaCarta(Carta c);
	public abstract void giocaCarta(Carta c);
}
