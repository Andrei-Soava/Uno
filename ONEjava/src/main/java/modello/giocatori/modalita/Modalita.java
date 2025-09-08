package modello.giocatori.modalita;

import modello.giocatori.Giocatore;
import vista.VistaTemporanea;

/**
 * interfaccia di ruolo (pattern PLAYER-ROLE)
 */
public interface Modalita {
	public abstract void scegliMossa(String cartaCorrente, VistaTemporanea tv, Giocatore g);
}
