package vista;

import modello.Mossa;
import modello.carte.Carta;
import modello.carte.Colore;
import modello.giocatori.Giocatore;

public interface InterfacciaVistaTemporanea {
	public void stampaMessaggio(String s);
	public String inserisciStringa(String message);
	public int scegliTraDue(String message, String optionZero, String optionOne);
	public int scegliTraN(String message, int minValue, int maxValue);
	public Colore scegliColore();
	public Mossa scegliMossa(Carta cartaCorrente, Giocatore g);
	public Carta scegliCarta(Carta cartaCorrente, Giocatore g);
}
