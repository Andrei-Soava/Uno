package vista;

import java.util.Scanner;

import modello.Mossa;
import modello.Mossa.TipoMossa;
import modello.carte.Carta;
import modello.carte.Colore;
import modello.giocatori.Giocatore;

public class TemporaryView {

	private Scanner sc;
	
	public TemporaryView() {
		sc=new Scanner(System.in);
	}
	
	public void closeScanner() {
		sc.close();
	}
	
	public void stampaMessaggio(String s) {
		System.out.println(s);
	}
	
	public String inserisciStringa(String message) {
		this.stampaMessaggio(message);
		return sc.nextLine();
	}
	
	public int scegliTraDue(String message, String optionZero, String optionOne) {
		int scelta = -1;
		do { 
			try {
				System.out.print(message + ": "+optionZero+"->0 , "+optionOne+"->1");
				scelta = Integer.parseInt(sc.nextLine());
			}
			catch(NumberFormatException exception) {
				this.stampaMessaggio("Valore non valido! Inserisci 0 o 1!");
			}
		} while (scelta < 0 || scelta > 1);

		return scelta;
	}
	
	public int scegliTraN(String message, int minValue, int maxValue) {
		int scelta = 0;
		boolean loop = true;
		
		do {
			loop = false;
			try {
				System.out.print(message + " (" + minValue + "-" + maxValue + "): ");
				scelta = Integer.parseInt(sc.nextLine());
				
				if(scelta < minValue || scelta > maxValue) {
					this.stampaMessaggio("Valore non valido! Inserisci un valore compreso tra " + minValue + " e " + maxValue + "!");
					loop = true;
				}
			}
			catch(NumberFormatException exception) {
				this.stampaMessaggio("Valore non valido! Inserisci un valore compreso tra " + minValue + " e " + maxValue + "!");
				loop = true;
			}
		}while(loop);
		
		return scelta;
	}
	
	public Colore scegliColore() {
		int index = scegliTraN("Scegli un colore: 0->Rosso, 1->Blu, 2->Giallo, 3->Verde", 0, 3);
		return Colore.values()[index];
	}
		
	public Mossa scegliMossa(String cartaCorrente, Giocatore g) {
		this.stampaMessaggio(
				"Turno di " + g.getNome() + "\n" + "CARTA CORRENTE: " + cartaCorrente + "\n" + g.mostraCarteInMano());
		int index = this.scegliTraDue("scegli azione", "pesca", "gioca carta");
		if(index==0) 
			return new Mossa(TipoMossa.PESCA);
		else
			return new Mossa(TipoMossa.GIOCA_CARTA);
	}
	
	public Carta scegliCarta(String cartaCorrente, Giocatore g) {
		int indexCarta = -1; // variabile separata per la carta
		String s = "CARTA CORRENTE: " + cartaCorrente + "\n" + g.mostraCarteInMano() + "\n";
		indexCarta = this.scegliTraN(s, 0, (g.getMano().getNumCarte() - 1));
		return g.getMano().getCarte().get(indexCarta);
	}
}
