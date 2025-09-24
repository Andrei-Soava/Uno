package onegame.client.controllore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import onegame.client.vista.VistaGioco;
import onegame.modello.Mossa;
import onegame.modello.Partita;
import onegame.modello.Mossa.TipoMossa;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;

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
 * -avviaPartita--> inizia il gioco
 * -caricaPartita--> scorciatoia per caricare partita tramite cp
 * -eseguiTurno--> loop di gioco
 * 
 */
public class ControlloreGioco {
	private VistaGioco vg;
	private Partita partita;
	private ControllorePersistenza cp;
	private boolean partitaAttiva = false;


	public ControlloreGioco(VistaGioco vg) {
		this.vg = vg;
		this.cp=new ControllorePersistenza(null);
	}
	
	public VistaGioco getTv() {
		return vg;
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
	
	public void proseguiPartita() {
		partitaAttiva=true;
	}

    public void interrompiPartita() {
        partitaAttiva = false;
    }


    /*
	public void configuraNuovaPartitaOriginale() {
		int numero = vg.scegliTraN("Seleziona numero giocatori", 2, 4);
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		for (int i = 0; i < numero; i++) {
			String s = vg.inserisciStringa("Scegli un nome per il giocatore " + (i + 1) + ":");
			giocatori.add(new GiocatoreAnonimo(s));
		}
		partita = new Partita(giocatori);
		setPartitaIF(giocatori, partita);
		partita.eseguiPrePartita();
		//da fare solo se persona è loggata
		cp.setSalvataggioCorrente();
		//salvataggioCorrente=getNomeDisponibile();
	}
	
	public void configuraNuovaPartitaVsBotOriginale() {
		int numero = vg.scegliTraN("Seleziona numero giocatori", 2, 4);
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		String s = vg.inserisciStringa("Scegli un nome per te: ");
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
	
	public void avviaPartitaOriginale() {
		cp.salvaPartitaAutomatico(this);
		try {
			while (!partita.verificaFinePartita()) {
				Giocatore g=partita.getGiocatoreCorrente();
				//se giocatore è un bot--> gestisco le sue scelte E aggiunta/rimozione carte della mano dentro il giocatore (applicaEffetto non serve)
				if(g.isBot()) {
					Mossa m=g.scegliMossaAutomatica();
					if(m.getTipoMossa()==TipoMossa.PESCA)
						vg.stampaMessaggio(g.getNome()+" ha pescato");
					else
						vg.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
				}
				else {
					Mossa m = vg.scegliMossa(partita.getCartaCorrente(), g);
					if (m.getTipoMossa() == TipoMossa.PESCA) 
					{
						gestisciPescaggioInternoOriginale(g,m);
					} 
					else 
					{
						m.setCartaScelta(vg.scegliCarta(partita.getCartaCorrente(), g));
						do {
							//se la carta selezionata non è giocabile--> o riprovo o pesco
							
							if (partita.applicaMossa(g, m) == null) 
							{
								//pesco (applico mossa) ed esco dal ciclo
								if (vg.scegliTraDue("carta non compatibile", "riprova", "pesca") == 1) 
								{
									m.setTipoMossa(TipoMossa.PESCA);
									//se pescaggio NON fornisce carta valida--> esco dal ciclo
									//altrimenti--> la carta è giocabile quindi ulteriore ciclo
									if(!gestisciPescaggioInternoOriginale(g, m));
										break;
								} 
								//riprovo--> scelgo un'altra carta
								else
									m.setCartaScelta(vg.scegliCarta(partita.getCartaCorrente(), g));
							} 
							//la carta è giocabile--> verifico se colore è da cambiare o no
							else 
							{
								vg.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
								//devo cambiare il colore
								if (m.getCartaScelta().getColore() == Colore.NERO)
								{
									m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
									m.getCartaScelta().setColore(vg.scegliColore());
									vg.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a "
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
			vg.stampaMessaggio("Ha vinto "+partita.getGiocatoreCorrente().getNome()+"!");
		} catch (NullPointerException e) {
			e.printStackTrace();
			vg.stampaMessaggio("Non hai configurato una nuova partita!");
		}
	}

	
	private boolean gestisciPescaggioInternoOriginale(Giocatore g, Mossa m) {
		//il pescaggio non ha ulteriori effetti (la carta pescata non è giocabile)
		if (partita.applicaMossa(g, m) == null) {
			return false;
		} 
		//il pescaggio ha restituito una carta giocabile--> posso scegliere cosa farne
		else 
		{
			if (vg.scegliTraDue(
					"Puoi giocare la carta che hai pescato:" + m.getCartaScelta() + " Scegli", "tienila",
					"giocala") == 1) {
				if (m.getCartaScelta().getColore() == Colore.NERO)
				{
					m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
					m.getCartaScelta().setColore(vg.scegliColore());
					vg.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a "
							+ m.getCartaScelta().getColore().name());
				} 
				partita.applicaMossa(g, m);
			}	
			return true;
		}
	}
		
	
	public void caricaPartitaOriginale() {
		cp.caricaPartita(this);
	}
	*/
    
//	static void setPartitaIF(ArrayList<Giocatore> giocatori, Partita partita) {
//		for(Giocatore g:giocatori) {
//			g.setInterfacciaPartita(partita);
//		}
//	}
	//---------------------------------------------------------------------------------
	//versione con GUI javafx
	public void configuraNuovaPartitaVsBot(int numeroGiocatori) {
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		String s = vg.inserisciStringa("Scegli un nome per te:");
		giocatori.add(new Giocatore(s));
		for (int i = 0; i < (numeroGiocatori-1); i++) {
			s="Bot"+(i+1);
			Giocatore g=new Giocatore(s);
			g.cambiaModalita();
			giocatori.add(g);
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
		//da fare solo se persona è loggata
		cp.setSalvataggioCorrente();
		//salvataggioCorrente=getNomeDisponibile();
	}
	
	public void configuraNuovaPartita(int numero) {
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		for (int i = 0; i < numero; i++) {
			//String s = tv.inserisciStringa("Scegli un nome per il giocatore " + (i + 1) + ":");
			String s="giocatore"+i;
			giocatori.add(new Giocatore(s));
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
		//da fare solo se persona è loggata
		cp.setSalvataggioCorrente();
		//salvataggioCorrente=getNomeDisponibile();
	}
	
	public void caricaPartita(String salvataggio) {
		cp.caricaPartita(this, salvataggio);
	}
	
	public void avviaPartita() {
	    cp.salvaPartitaAutomatico(this);
	    partitaAttiva=true;
	    eseguiTurno();
	}
	
	private void gestisciPescaggioInternoAsync(Giocatore g, Mossa m, Runnable fineTurno) {
	    if (partita.applicaMossa(g, m) == null) {
	        //carta pescata non giocabile -> fine turno
	        fineTurno.run();
	    } 
	    else 
	    {
	    	//carta pescata giocabile -> scegliere se tenerla o giocarla
	    	((VistaGioco)vg).stampaCartaPescataAsync(m.getCartaScelta(), scelta -> {
	            if (scelta == 1) {
	                if (m.getCartaScelta().getColore() == Colore.NERO) {
	                	((VistaGioco)vg).stampaColoriAsync(colore -> {
	                		System.out.println();
	                        m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
	                        System.out.println();
	                        vg.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a " + colore.name());
	                        m.getCartaScelta().setColore(colore);
	                        partita.applicaMossa(g, m);
	                        fineTurno.run();
	                    });
	                    return;
	                }
	                partita.applicaMossa(g, m);
	            }
	            fineTurno.run();
	        });
	    }
	}
	
	
	private void eseguiTurno() {
		if (!partitaAttiva) {
	        System.out.println();
	        return;
	    }
	    //fine partita?
	    if (partita.verificaFinePartita()) {
	    	((VistaGioco)vg).stampaFinePartita(partita.getVincitore(), null);
	        interrompiPartita();
	        return;
	    }

	    Giocatore g = partita.getGiocatoreCorrente();
	    ((VistaGioco)vg).stampaTurno(g.getNome());
	    ((VistaGioco)vg).stampaProssimoTurno(partita.vediProssimoGiocatore().getNome());
	    if (g.isBot()) {
	        //turno eventuale bot
	        Mossa m = g.scegliMossaAutomatica();
	        if (m.getTipoMossa() == TipoMossa.PESCA) {
	            vg.stampaMessaggio(g.getNome() + " ha pescato");
	        } else {
	            vg.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
	        }
	        partita.passaTurno();
	        cp.salvaPartitaAutomatico(this);
	        if(partitaAttiva)
	        	eseguiTurno(); // turno successivo
	    } else {
	    	System.out.println();
	        //turno umano 
	    	//setup timer e counter -> da mandare alla VistaGioco
	    	AtomicBoolean mossaEffettuata = new AtomicBoolean(false);
	    	Mossa flagGiaPescato=new Mossa(null);
	    	PauseTransition timerTurno = new PauseTransition(Duration.seconds(30));
	    	SimpleIntegerProperty secondsLeft = new SimpleIntegerProperty(30);
	    	Timeline countdown = new Timeline(
	    			new KeyFrame(Duration.seconds(1), e -> secondsLeft.set(secondsLeft.get() - 1))
	    			);
	        countdown.setCycleCount(30);
	    	((VistaGioco)vg).setTimer(secondsLeft);
	    	
	    	//faccio partire timer e counter (30 secondi per fare la mossa)
	    	countdown.play();
	        timerTurno.setOnFinished(e -> {
	        	if (mossaEffettuata.compareAndSet(false, true)) {
	            countdown.stop();
	            timerTurno.stop();
	            timerTurno.setOnFinished(null);
	            
	            ((VistaGioco) vg).chiudiFinestraAperta();
	            
	            vg.stampaMessaggio("Tempo scaduto: "+g.getNome()+" ha pescato SENZA possibilità di giocare");
	            //importante--> se il giocatore ha già pescato di suo, non lo faccio pescare di nuovo (es se può giocare carta pescata)
	            if(flagGiaPescato.getTipoMossa()!=TipoMossa.PESCA)
	            	g.aggiungiCarta(partita.pescaCarta());
	            partita.passaTurno();
                cp.salvaPartitaAutomatico(this);
                if(partitaAttiva)
                	eseguiTurno(); 
	        	}
	        });
	        //DA COMMENTARE SE GIOCO SI ROMPE
	        timerTurno.play();
	    	
	    	
	    	//inizio turno vero e proprio (posso o pescare, o tentare di giocare una carta)
	        ((VistaGioco) vg).scegliMossaAsync(partita.getCartaCorrente(), g, m -> {
	        	System.out.println();
	            if (m.getTipoMossa() == TipoMossa.PESCA) {
	            	//serve nel caso in cui esca fuori una carta giocabile-> non faccio pescare di nuovo
	            	flagGiaPescato.setTipoMossa(TipoMossa.PESCA);
		        	System.out.println();
	                gestisciPescaggioInternoAsync(g, m, () -> {
	                	System.out.println();
	                	if (mossaEffettuata.compareAndSet(false, true)) {
	                	countdown.stop();
	    	            timerTurno.stop();
	    	            timerTurno.setOnFinished(null);
	                    partita.passaTurno();
	                    cp.salvaPartitaAutomatico(this);
	                    if(partitaAttiva)
	                    	eseguiTurno();
	                	}
	                });
	            } 
	            else 
	            {

	                    // Applica la mossa e gestisci eventuale cambio colore
	                    if (partita.applicaMossa(g, m) != null) {
	                        vg.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
	                        if (m.getCartaScelta().getColore() == Colore.NERO) {
	                            m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
	                            
	                            ((VistaGioco)vg).stampaColoriAsync(colore -> {
	                	        	System.out.println();

	                                vg.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a " + colore.name());
	                                if (mossaEffettuata.compareAndSet(false, true)) {
	                                	m.getCartaScelta().setColore(colore);
	                                countdown.stop();
	                	            timerTurno.stop();
	                	            timerTurno.setOnFinished(null);
	                                partita.applicaMossa(g, m);
	                                
	                                //QUI MOSTRA PULSANTE ONE (se hai appena giocato la penultima)
	                                if (g.getMano().getNumCarte() == 1) { 
	                                    ((VistaGioco) vg).mostraPulsanteONE(premuto -> {
	                                    	if (premuto) {
		                        	            vg.stampaMessaggio(g.getNome()+" ha chiamato ONE");
		                        	        } else {
		                        	            vg.stampaMessaggio(g.getNome()+" NON ha chiamato ONE in tempo -> pesca 2 carte");
		                        	            g.getMano().aggiungiCarta(partita.getMazzo().pescaN(2));
		                        	        }
		                        	        partita.passaTurno();
		                        	        cp.salvaPartitaAutomatico(this);
		                        	        if (partitaAttiva)
		                        	            eseguiTurno();
	                                    });
	                                } else {
	                                    //condizione falsa -> eseguo subito le 4 righe
	                                    partita.passaTurno();
	                                    cp.salvaPartitaAutomatico(this);
	                                    if (partitaAttiva)
	                                        eseguiTurno();
	                                }
	                                
	                                }
	                            });

	                            return;
	                        }
	                        if (mossaEffettuata.compareAndSet(false, true)) {
	                        	countdown.stop();
	                        	timerTurno.stop();
	                        	timerTurno.setOnFinished(null);
	                        	partita.applicaMossa(g, m);
	                        	
	                        	//QUI MOSTRA PULSANTE ONE (se hai appena giocato la penultima carta)
	                        	if (g.getMano().getNumCarte() == 1) { 
	                        	    ((VistaGioco) vg).mostraPulsanteONE(premuto -> {
	                        	        if (premuto) {
	                        	            vg.stampaMessaggio(g.getNome()+" ha chiamato ONE");
	                        	        } else {
	                        	            vg.stampaMessaggio(g.getNome()+" NON ha chiamato ONE in tempo -> pesca 2 carte");
	                        	            g.getMano().aggiungiCarta(partita.getMazzo().pescaN(2));
	                        	        }
	                        	        partita.passaTurno();
	                        	        cp.salvaPartitaAutomatico(this);
	                        	        if (partitaAttiva)
	                        	            eseguiTurno();
	                        	    });
	                        	} else {
	                        	    partita.passaTurno();
	                        	    cp.salvaPartitaAutomatico(this);
	                        	    if (partitaAttiva)
	                        	        eseguiTurno();
	                        	}
	                        	
	                        }
	                    } else {
	                        //carta non valida
	                        vg.stampaMessaggio("Carta non compatibile");
	                    }
	            }
	        });
	    }
	}


	//fine
}
