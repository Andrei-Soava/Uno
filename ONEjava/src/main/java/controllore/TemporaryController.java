package controllore;

import java.util.ArrayList;

import modello.Partita;
import modello.giocatori.Giocatore;
import modello.giocatori.GiocatoreAnonimo;
import vista.TemporaryView;

public class TemporaryController {
	private TemporaryView tv;
	private Partita partita;

	public TemporaryController() {
		this.tv = new TemporaryView();
	}

	public void configuraNuovaPartita() {
		int numero = tv.chooseBetweenN("Seleziona numero giocatori", 2, 4);
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		for (int i = 0; i < numero; i++) {
			String s = tv.chooseName("Scegli un nome per il giocatore " + (i + 1) + ":");
			giocatori.add(new GiocatoreAnonimo(s));
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
	}
	
	public void configuraNuovaPartitaVsBot() {
		int numero = tv.chooseBetweenN("Seleziona numero giocatori", 2, 4);
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		String s = tv.chooseName("Scegli un nome te: ");
		giocatori.add(new GiocatoreAnonimo(s));
		for (int i = 0; i < (numero-1); i++) {
			s="Bot"+(i+1);
			Giocatore g=new GiocatoreAnonimo(s);
			g.cambiaModalita();
			giocatori.add(g);
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
	}

	public void avviaPartita() {
		try {
			while (!partita.verificaFinePartita()) {
				partita.eseguiUnTurno(tv);
			}
		} catch (NullPointerException e) {
			tv.printMessage("Non hai configurato una nuova partita!");
		}
	}
}
