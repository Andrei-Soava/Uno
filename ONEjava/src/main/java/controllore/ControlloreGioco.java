package controllore;

import java.util.ArrayList;
import modello.Mossa;
import modello.Mossa.TipoMossa;
import modello.Partita;
import modello.carte.Colore;
import modello.giocatori.Giocatore;
import modello.giocatori.GiocatoreAnonimo;
import vista.InterfacciaVistaTemporanea;
import vista.VistaGioco;
import vista.VistaTemporanea;

/**
 * classe centrale per l'esecuzione del gioco
 * 
 * attributi importanti:
 * -tv--> riferimento a VistaTemporanea per raccolta input durante il gioco
 * -partita--> riferimento alla partita attualmente in corso
 * -cp--> riferimento a ControllorePersistenza per gestire salvataggi/caricamenti di partita
 * 
 * metodi importanti:
 * -configuraPartita--> inizializza una nuova partita senza avviarla
 * -configuraPartitaVsBot--> come sopra ma solo un giocatore è umano
 * -avviaPartita--> loop di gioco
 * -caricaPartita--> scorciatoia per caricare partita tramite cp
 * 
 */
public class ControlloreGioco {
	private InterfacciaVistaTemporanea tv;
	private Partita partita;
	private ControllorePersistenza cp;

	public ControlloreGioco() {
		this.tv = new VistaTemporanea();
		this.cp=new ControllorePersistenza(null);
	}
	
	public InterfacciaVistaTemporanea getTv() {
		return tv;
	}

	public Partita getPartita() {
		return partita;
	}

	public ControllorePersistenza getCp() {
		return cp;
	}
	
	public void setPartita(Partita partita) {
		this.partita = partita;
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
		//da fare solo se persona è loggata
		cp.setSalvataggioCorrente();
		//salvataggioCorrente=getNomeDisponibile();
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
		//da fare solo se persona è loggata
		cp.setSalvataggioCorrente();
		//salvataggioCorrente=getNomeDisponibile();
	}
	
	public void avviaPartita() {
		cp.salvaPartitaAutomatico(this);
		try {
			while (!partita.verificaFinePartita()) {
				Giocatore g=partita.getGiocatoreCorrente();
				//se giocatore è un bot--> gestisco le sue scelte E aggiunta/rimozione carte della mano dentro il giocatore (applicaEffetto non serve)
				if(g.isBot()) {
					Mossa m=g.scegliMossaAutomatica();
					if(m.getTipoMossa()==TipoMossa.PESCA)
						tv.stampaMessaggio(g.getNome()+" ha pescato");
					else
						tv.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
				}
				else {
					Mossa m = tv.scegliMossa(partita.getCartaCorrente(), g);
					if (m.getTipoMossa() == TipoMossa.PESCA) 
					{
						gestisciPescaggioInterno(g,m);
					} 
					else 
					{
						m.setCartaScelta(tv.scegliCarta(partita.getCartaCorrente(), g));
						do {
							//se la carta selezionata non è giocabile--> o riprovo o pesco
							
							if (partita.applicaMossa(g, m) == null) 
							{
								//pesco (applico mossa) ed esco dal ciclo
								if (tv.scegliTraDue("carta non compatibile", "riprova", "pesca") == 1) 
								{
									m.setTipoMossa(TipoMossa.PESCA);
									//se pescaggio NON fornisce carta valida--> esco dal ciclo
									//altrimenti--> la carta è giocabile quindi ulteriore ciclo
									if(!gestisciPescaggioInterno(g, m));
										break;
								} 
								//riprovo--> scelgo un'altra carta
								else
									m.setCartaScelta(tv.scegliCarta(partita.getCartaCorrente(), g));
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
				cp.salvaPartitaAutomatico(this);
			}
			tv.stampaMessaggio("Ha vinto "+partita.getGiocatoreCorrente().getNome()+"!");
		} catch (NullPointerException e) {
			e.printStackTrace();
			tv.stampaMessaggio("Non hai configurato una nuova partita!");
		}
	}

	
	private boolean gestisciPescaggioInterno(Giocatore g, Mossa m) {
		//il pescaggio non ha ulteriori effetti (la carta pescata non è giocabile)
		if (partita.applicaMossa(g, m) == null) {
			return false;
		} 
		//il pescaggio ha restituito una carta giocabile--> posso scegliere cosa farne
		else 
		{
			if (tv.scegliTraDue(
					"Puoi giocare la carta che hai pescato:" + m.getCartaScelta() + " Scegli", "tienila",
					"giocala") == 1) {
				if (m.getCartaScelta().getColore() == Colore.NERO)
				{
					m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
					m.getCartaScelta().setColore(tv.scegliColore());
					tv.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a "
							+ m.getCartaScelta().getColore().name());
				} 
				partita.applicaMossa(g, m);
			}	
			return true;
		}
	}
		
	static void setPartitaIF(ArrayList<Giocatore> giocatori, Partita partita) {
		for(Giocatore g:giocatori) {
			g.setInterfacciaPartita(partita);
		}
	}
	
	public void caricaPartita() {
		cp.caricaPartita(this);
	}
		
}
