package onegame.modello;

import java.util.ArrayList;
import java.util.List;

import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
import onegame.modello.giocatori.Giocatore;

/**
 * interfaccia partita implementata da classe partita e da fornire a giocatore
 * (contiene uniche azioni "legali" da giocatore)
 */
public interface PartitaIF {
	public abstract Giocatore getGiocatoreCorrente();

	public abstract Mazzo getMazzo();

	public abstract void cambiaDirezione();

	public abstract void prossimoGiocatore();

	public abstract boolean getDirezione();

	public abstract int getNumeroGiocatori();

	public abstract List<Giocatore> getGiocatori();

	public abstract Carta getCartaCorrente();

	/**
	 * metodo di verifica su un +4 giocabile o meno
	 * 
	 * @param g, giocatore su cui si svolge la verifica
	 * @return true se il +4 è lecito da giocare, false altrimenti
	 */
	public default boolean verificaPiuQuattroGiocabile() {
		Giocatore g = getGiocatoreCorrente();
		if (!getGiocatori().contains(g))
			return false;
		else {
			ArrayList<Carta> carteInMano = new ArrayList<>();
			carteInMano.addAll(g.getMano().getCarte());
			// istruzione in cui vengono rimossi tutti i +4 dalla mano di una giocatore per
			// fare controlli
			carteInMano.removeIf(carta -> carta instanceof CartaSpeciale
					&& ((CartaSpeciale) carta).getTipo() == TipoSpeciale.PIU_QUATTRO);
			// ciclo di verifica possibilità di giocare altre carte oltre ai +4
			for (Carta carta : carteInMano) {
				if (carta.giocabileSu(getCartaCorrente()))
					return false;
			}
			// si arriva qui solo se nessuna delle carte in mano OLTRE ai +4 è giocabile
			return true;
		}
	}

	public default boolean isCartaGiocabile(Carta carta) {
		if (carta == null)
			return false;
		if (carta instanceof CartaSpeciale cs && cs.getTipo() == TipoSpeciale.PIU_QUATTRO) {
			return verificaPiuQuattroGiocabile();
		}
		return carta.giocabileSu(getCartaCorrente());
	}
}
