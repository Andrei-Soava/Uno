package controllore;

import java.util.ArrayList;

import modello.Mossa;
import modello.Mossa.TipoMossa;
import modello.Partita;
import modello.carte.Colore;
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
		int numero = tv.scegliTraN("Seleziona numero giocatori", 2, 4);
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		for (int i = 0; i < numero; i++) {
			String s = tv.inserisciStringa("Scegli un nome per il giocatore " + (i + 1) + ":");
			giocatori.add(new GiocatoreAnonimo(s));
		}
		partita = new Partita(giocatori);
		setPartitaIF(giocatori, partita);
		partita.eseguiPrePartita();
	}
	
	public void configuraNuovaPartitaVsBot() {
		int numero = tv.scegliTraN("Seleziona numero giocatori", 2, 4);
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		String s = tv.inserisciStringa("Scegli un nome per te: ");
		giocatori.add(new GiocatoreAnonimo(s));
		for (int i = 0; i < (numero-1); i++) {
			s="Bot"+(i+1);
			Giocatore g=new GiocatoreAnonimo(s);
			g.cambiaModalita();
			giocatori.add(g);
		}
		partita = new Partita(giocatori);
		setPartitaIF(giocatori, partita);
		partita.eseguiPrePartita();
	}
	
	/*
	public void avviaPartita() {
		try {
			while (!partita.verificaFinePartita()) {
				Giocatore g=partita.getGiocatoreCorrente();
				if(g.isBot()) {
					scegliMossaAutomatica(g);
				}
				else {
					Mossa m=tv.scegliMossa(partita.getCartaCorrente().toString(), g);
					if(m.getTipoMossa()==TipoMossa.PESCA) {
						gestisciPescaggio(g);
					}
					else {
						gestisciSceltaCarta(g);
					}
						
				}
				partita.eseguiUnTurno();
			}
			tv.stampaMessaggio("Ha vinto "+partita.getGiocatoreCorrente().getNome()+"!");
		} catch (NullPointerException e) {
			tv.stampaMessaggio("Non hai configurato una nuova partita!");
		}
	}
	*/
	
	public void avviaPartita() {
		try {
			while (!partita.verificaFinePartita()) {
				Giocatore g=partita.getGiocatoreCorrente();
				if(g.isBot()) {
					Mossa m=g.scegliMossaAutomatica();
					if(m.getTipoMossa()==TipoMossa.PESCA)
						tv.stampaMessaggio(g.getNome()+" ha pescato");
					else
						tv.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
					partita.applicaMossa(g, m);
				}
				else {
					Mossa m = tv.scegliMossa(partita.getCartaCorrente().toString(), g);
					if (m.getTipoMossa() == TipoMossa.PESCA) 
					{
						gestisciPescaggioInterno(g,m);
					} 
					else 
					{
						m.setCartaScelta(tv.scegliCarta(partita.getCartaCorrente().toString(), g));
						do {
							//se la carta selezionata non è giocabile--> o riprovo o pesco
							
							if (partita.applicaMossa(g, m) == null) 
							{
								//pesco (applico mossa) ed esco dal ciclo
								if (tv.scegliTraDue("carta non compatibile", "riprova", "pesca") == 1) 
								{
									m.setTipoMossa(TipoMossa.PESCA);
									gestisciPescaggioInterno(g, m);
									break;
								} 
								//riprovo--> scelgo un'altra carta
								else
									m.setCartaScelta(tv.scegliCarta(partita.getCartaCorrente().toString(), g));
							} 
							//la carta è giocabile--> verifico se colore è da cambiare o no
							else 
							{
								tv.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
								//devo cambiare il colore
								if (m.getCartaScelta().getColore() == Colore.NERO)
								{
									m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
									m.getCartaScelta().setColore(tv.scegliColore());
									tv.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a "
											+ m.getCartaScelta().getColore().name());
								} 
								//non devo (più) cambiare il colore--> esco dal ciclo
								partita.applicaMossa(g, m);
								break;
							}
						} while (true);
					}

				}
				partita.eseguiUnTurno();
			}
			tv.stampaMessaggio("Ha vinto "+partita.getGiocatoreCorrente().getNome()+"!");
		} catch (NullPointerException e) {
			tv.stampaMessaggio("Non hai configurato una nuova partita!");
		}
	}
	
	private void gestisciPescaggioInterno(Giocatore g, Mossa m) {
		//il pescaggio non ha ulteriori effetti (la carta pescata non è giocabile)
		if (partita.applicaMossa(g, m) == null) {
			;
		} 
		//il pescaggio ha restituito una carta giocabile--> posso scegliere cosa farne
		else 
		{
			if (tv.scegliTraDue(
					"Puoi giocare la carta che hai pescato:" + m.getCartaScelta() + " Scegli", "tienila",
					"giocala") == 1)
				
				partita.applicaMossa(g, m);
		}
	}
	
	/*
	private void gestisciPescaggio(Giocatore g) {
		int index = -1;
		Carta c = partita.pescaCarta();
		if (partita.tentaGiocaCarta(c)) {
			if(!g.isBot())
				index = tv.scegliTraDue("Puoi giocare la carta che hai pescato:" + c + " Scegli", "tienila", "giocala");
			else
				index=1;
			if (index == 0) {
				g.aggiungiCarta(c);
			} else {
				gestisciGiocaCarta(c,g);
			}
		} else {
			g.aggiungiCarta(c);
		}
	}
	
	private void gestisciSceltaCarta(Giocatore g){
		do {
			Carta c=tv.scegliCarta(partita.getCartaCorrente().toString(), g);
			if(partita.tentaGiocaCarta(c)) {
				gestisciGiocaCarta(c,g);
				break;
			}
			else {
				if(tv.scegliTraDue("carta non compatibile", "riprova", "pesca")==1) {
					gestisciGiocaCarta(c,g);
					break;
				}
			}
		} while(true);
	}
	
	private void gestisciGiocaCarta(Carta c, Giocatore g) {
		tv.stampaMessaggio(g.getNome()+" ha giocato la carta: "+c);
		if (c.getColore() == Colore.NERO) {
			if(!g.isBot())
				c.setColore(tv.scegliColore());
			else
				c.setColore(Colore.scegliColoreCasuale());
			tv.stampaMessaggio(g.getNome()+" ha cambiato il colore sul banco a "+c.getColore().name());
		}
		g.rimuoveCarta(c);
		partita.giocaCarta(c);
	}
	
	private void scegliMossaAutomatica(Giocatore g) {
		for(Carta c:g.getMano().getCarte()) {
			if(partita.tentaGiocaCarta(c)) {
				gestisciGiocaCarta(c,g);
				return;
			}
		}
		tv.stampaMessaggio(g.getNome()+" ha pescato");
		gestisciPescaggio(g);
	}
	*/
	
	private void setPartitaIF(ArrayList<Giocatore> giocatori, Partita partita) {
		for(Giocatore g:giocatori) {
			g.setInterfacciaPartita(partita);
		}
	}
}
