package vista;

import onegame.modello.Mossa;
import onegame.modello.carte.Carta;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;

public interface InterfacciaVistaTemporanea {
	public void stampaMessaggio(String s);
	public String inserisciStringa(String message);
	public int scegliTraDue(String message, String optionZero, String optionOne);
	public int scegliTraN(String message, int minValue, int maxValue);
	public Colore scegliColore();
	public Mossa scegliMossa(Carta cartaCorrente, Giocatore g);
	public Carta scegliCarta(Carta cartaCorrente, Giocatore g);
}
