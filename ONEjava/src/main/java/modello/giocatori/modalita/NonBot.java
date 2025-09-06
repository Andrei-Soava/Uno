package modello.giocatori.modalita;

import modello.PartitaIF;
import modello.carte.Carta;
import modello.carte.Colore;
import modello.giocatori.Giocatore;
import vista.TemporaryView;

/**
 * classe che implementa il giocatore (gioco manuale)
 */
public class NonBot implements Modalita {

	@Override
	public void scegliMossa(String cartaCorrente, TemporaryView tv, Giocatore g) {
		PartitaIF partitaIF=g.getInterfacciaPartita();
		tv.stampaMessaggio(
				"Turno di " + g.getNome() + "\n" + "CARTA CORRENTE: " + cartaCorrente + "\n" + g.mostraCarteInMano());
		int index = tv.scegliTraDue("scegli azione", "pesca", "gioca carta");
		if (index == 0) {
			funzionePescaggio(tv, partitaIF, g);
			return;
		} else {
			int indexCarta = -1; // variabile separata per la carta
			while (indexCarta < 0 || indexCarta >= g.getMano().getNumCarte()) {

				String s = "CARTA CORRENTE: " + cartaCorrente + "\n" + g.mostraCarteInMano() + "\n";
				indexCarta = tv.scegliTraN(s, 0, (g.getMano().getNumCarte() - 1));

				if (indexCarta >= 0 && indexCarta < g.getMano().getNumCarte()) {
					if (!partitaIF.tentaGiocaCarta(g.getMano().getCarte().get(indexCarta))) {
						int scelta = tv.scegliTraDue("carta non compatibile", "riprova", "pesca");
						if (scelta == 1) {
							funzionePescaggio(tv, partitaIF, g);
							return; // esci dal metodo
						} else {
							indexCarta = -1; // ripeti scelta carta
						}
					} else {
						Carta scelta = g.getMano().getCarte().get(indexCarta);
						if (scelta.getColore() == Colore.NERO) {
							scelta.setColore(tv.scegliColore());
						}
						g.rimuoveCarta(scelta);
						partitaIF.giocaCarta(scelta);
						return; // esci dal metodo dopo aver giocato
					}
				}
			}
		}
	}
	
	private void funzionePescaggio(TemporaryView tv, PartitaIF partitaIF, Giocatore g) {
		int index = -1;
		Carta c = partitaIF.pescaCarta();
		if (partitaIF.tentaGiocaCarta(c)) {
			index = tv.scegliTraDue("Puoi giocare la carta che hai pescato:" + c + " Scegli", "tienila", "giocala");
			if (index == 0) {
				g.aggiungiCarta(c);
				return;
			} else {
				if (c.getColore() == Colore.NERO) {
					c.setColore(tv.scegliColore());
				}
				g.rimuoveCarta(c);
				partitaIF.giocaCarta(c);
				return;
			}
		} else {
			g.aggiungiCarta(c);
		}
	}

}
