package modello.giocatori.modalita;

import modello.PartitaIF;
import modello.carte.Carta;
import modello.carte.Colore;
import modello.giocatori.Giocatore;
import vista.TemporaryView;

/**
 * classe che implementa il bot (gioco automatico)
 */
public class Bot implements Modalita {


	@Override
	public void scegliMossa(String cartaCorrente, TemporaryView tv, Giocatore g) {
		scegliMossa(tv, g);
	}

	private void scegliMossa(TemporaryView tv, Giocatore g) {
		PartitaIF partitaIF=g.getInterfacciaPartita();
		for(Carta c:g.getMano().getCarte()) {
			if(partitaIF.tentaGiocaCarta(c)) {
				g.rimuoveCarta(c);
				partitaIF.giocaCarta(c);
				tv.stampaMessaggio(g.getNome()+" ha giocato la carta: "+c);
				if(c.getColore()==Colore.NERO) {
					c.setColore(Colore.scegliColoreCasuale());
					tv.stampaMessaggio(g.getNome()+" ha cambiato il colore sul banco a "+c.getColore().name());
				}
				return;
			}
		}
		tv.stampaMessaggio(g.getNome()+" ha pescato");
		Carta pescata=partitaIF.pescaCarta();
		if(partitaIF.tentaGiocaCarta(pescata)) {
			partitaIF.giocaCarta(pescata);
			tv.stampaMessaggio(g.getNome()+" ha giocato la carta che ha pescato: "+pescata);
			if(pescata.getColore()==Colore.NERO) {
				pescata.setColore(Colore.scegliColoreCasuale());
				tv.stampaMessaggio(g.getNome()+" ha cambiato il colore sul banco a "+pescata.getColore().name());
			}
		}
		else
			g.aggiungiCarta(pescata);
	}
}
