package modello.giocatori.modalita;

import modello.giocatori.Giocatore;
import vista.TemporaryView;

/**
 * interfaccia di ruolo (pattern PLAYER-ROLE)
 */
public interface Modalita {
	public abstract void scegliMossa(String cartaCorrente, TemporaryView tv, Giocatore g);
}
