package onegame.modello;

import onegame.modello.giocatori.Giocatore;

/**
 * interfaccia partita implementata da classe partita e da fornire a giocatore (contiene uniche azioni "legali" da giocatore)
 */
public interface PartitaIF {
	public abstract Giocatore getGiocatoreCorrente();
	public abstract Mazzo getMazzo();
	public abstract void cambiaDirezione();
	public abstract void prossimoGiocatore();
	public abstract boolean getDirezione();
	public abstract int getNumeroGiocatori();
}
