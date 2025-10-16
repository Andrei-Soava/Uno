package onegame.client.controllore.offline;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import onegame.client.controllore.ControllorePersistenza;
import onegame.client.net.ClientSocket;
import onegame.client.persistenza_temporanea.ManagerPersistenza;
import onegame.client.vista.partita.VistaGioco;
import onegame.client.vista.partita.VistaSpettatore;
import onegame.modello.Mossa;
import onegame.modello.Partita;
import onegame.modello.Mossa.TipoMossa;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
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
	private VistaSpettatore vsp;
	private Partita partita;
	private ControllorePersistenza cp;
	private ClientSocket cs;
	private boolean partitaAttiva = false;


	public ControlloreGioco(VistaGioco vg, VistaSpettatore vsp, ClientSocket cs) {
		this.vg = vg;
		this.vsp = vsp;
		this.cs = cs;
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
	
	/**
	 * metodo per continuare la partita
	 */
	public void proseguiPartita() {
		partitaAttiva=true;
	}

	/**
	 * metodo per sospendere la partita (usato in VistaPartita)
	 */
    public void interrompiPartita() {
        partitaAttiva = false;
    }

    /**
     * metodo che crea una nuova partita contro bots (difatto l'unica opzione disponibile)
     * @param numeroGiocatori
     */
	public void configuraNuovaPartitaVsBot(int numeroGiocatori) {
		ArrayList<Giocatore> giocatori = new ArrayList<>();
		//String s = vg.inserisciStringa("Scegli un nome per te:");
		Giocatore giocatore=new Giocatore(cs.getUtente().getUsername());
		cs.getUtente().setGiocatore(giocatore);
		giocatori.add(giocatore);
		String s="";
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
	
	/**
	 * metodo che crea una partita di tutti giocatori senzienti (ma ti fatto inutile)
	 * @param numero
	 */
	@Deprecated
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
	
	/**
	 * metodo che identifica l'unico giocatore effettivo che prende scelte nella partita offline
	 */
	private void recuperaGiocatoreNonBot() {
		for(Giocatore g:partita.getGiocatori()) {
			if(!g.isBot()) {
				cs.getUtente().setGiocatore(g);
				return;
			}
			
		}
	}
	
	/**
	 * metodo richiamato ogni volta che viene avviata una partita (nuova o caricata)
	 */
	public void avviaPartita() {
		vsp.cg=this;
		vg.cg=this;
	    cp.salvaPartitaAutomatico(this);
	    partitaAttiva=true;
	    recuperaGiocatoreNonBot();
	    vg.mostraVista();
	    eseguiTurno();
	}
	
	/**
	 * metodo per la gestione del pescaCarta volontaria + possibilità di giocare la carta pescata
	 * @param g
	 * @param m
	 * @param fineTurno
	 */
	private void gestisciPescaggioInternoAsync(Giocatore g, Mossa m, Runnable fineTurno) {
	    if (partita.applicaMossa(g, m) == null) {
	        //carta pescata non giocabile -> fine turno
	        fineTurno.run();
	    } 
	    else 
	    {
	    	//impedisco che venga giocato un +4 pescato se avevo altre carte giocabili prima di pescare
	    	if(m.getCartaScelta() instanceof CartaSpeciale && ((CartaSpeciale)m.getCartaScelta()).getTipo()==TipoSpeciale.PIU_QUATTRO) {
	    		if(!partita.verificaPiuQuattroGiocabile(g)) {
	    			fineTurno.run();
	    			return;
	    		}
	    	}
	    	//carta pescata giocabile -> scegliere se tenerla o giocarla
	    	vg.stampaCartaPescataAsync(m.getCartaScelta(), scelta -> {
	            if (scelta == 1) {
	                if (m.getCartaScelta().getColore() == Colore.NERO) {
	                	vg.stampaColoriAsync(colore -> {
	                		;//breakpoint
	                        m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
	                        ;//breakpoint
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
	
	/**
	 * metodo centrale che gestisce il gameloop (si richiama da solo finchè non finisce la partita 
	 * OPPURE se la partita viene sospesa)
	 */
	private void eseguiTurno() {
		if (!partitaAttiva) {
			;//breakpoint
	        return;
	    }
	    //fine partita?
	    if (partita.verificaFinePartita()) {
	    	vg.stampaFinePartita(partita.getVincitore().getNome(), ()->{
	    		String salvataggio = getCp().getSalvataggioCorrente();
	            ManagerPersistenza.eliminaSalvataggio(salvataggio);
	            vg.mostraMenuOffline();
	    	});
	        interrompiPartita();
	        return;
	    }

	    Giocatore g = partita.getGiocatoreCorrente();
	    vg.stampaTurnoCorrente(g.getNome());
	    if (g.isBot()) {
	    	// primo delay di 5 secondi prima di scegliere la mossa
	    	vg.stampaTurnazione(partita.getTurnazioneDalGiocatore(cs.getUtente().getGiocatore()), partita.getDirezione());
	    	vg.stampaManoReadOnly(partita.getCartaCorrente(), cs.getUtente().getGiocatore());
	    	PauseTransition pausa1 = new PauseTransition(Duration.seconds(5));
			pausa1.setOnFinished(ev1 -> {
				if (partitaAttiva) {
					// eseguo la mossa automatica
					Mossa m = partita.scegliMossaAutomatica();
					if (m.getTipoMossa() == TipoMossa.PESCA) {
						vg.stampaMessaggio(g.getNome() + " ha pescato");
					} else {
						vg.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
					}
					vg.stampaTurnazione(partita.getTurnazioneDalGiocatore(cs.getUtente().getGiocatore()), partita.getDirezione());
			    	vg.stampaManoReadOnly(partita.getCartaCorrente(), cs.getUtente().getGiocatore());
					partita.passaTurno();
					cp.salvaPartitaAutomatico(this);
					// seconda pausa di 3 secondi dopo aver mostrato il messaggio
					PauseTransition pausa2 = new PauseTransition(Duration.seconds(3));
					pausa2.setOnFinished(ev2 -> {
						if (partitaAttiva) {
							eseguiTurno(); // turno successivo
						}
					});
					pausa2.play();
				}
			});

	    	pausa1.play();

	    } else {
	    	;//breakpoint
	    	vg.evidenziaTurnoCorrente();
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
	    	vg.setTimer(secondsLeft);
	    	
	    	//faccio partire timer e counter (30 secondi per fare la mossa)
	    	countdown.play();
	        timerTurno.setOnFinished(e -> {
	        	if (mossaEffettuata.compareAndSet(false, true)) {
	            countdown.stop();
	            timerTurno.stop();
	            timerTurno.setOnFinished(null);
	            
	            vg.chiudiFinestraAperta();
	            
                if(partitaAttiva) {
                	vg.stampaMessaggio("Tempo scaduto: "+g.getNome()+" ha pescato SENZA possibilità di giocare");
                	//importante--> se il giocatore ha già pescato di suo, non lo faccio pescare di nuovo (es se può giocare carta pescata)
                	if(flagGiaPescato.getTipoMossa()!=TipoMossa.PESCA)
                		g.aggiungiCarta(partita.pescaCarta());
                	partita.passaTurno();
                	cp.salvaPartitaAutomatico(this);
                	eseguiTurno(); 
                }
	        	}
	        });
	        //DA COMMENTARE SE GIOCO SI ROMPE
	        timerTurno.play();
	        vg.stampaTurnazione(partita.getTurnazioneDalGiocatore(g), partita.getDirezione());
	    	//inizio turno vero e proprio (posso o pescare, o tentare di giocare una carta)
	        vg.scegliMossaAsync(partita.getCartaCorrente(), g, m -> {
	        	;//breakpoint
	            if (m.getTipoMossa() == TipoMossa.PESCA) {
	            	//serve nel caso in cui esca fuori una carta giocabile-> non faccio pescare di nuovo
	            	flagGiaPescato.setTipoMossa(TipoMossa.PESCA);
	            	;//breakpoint
	                gestisciPescaggioInternoAsync(g, m, () -> {
	                	;//breakpoint
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
	            		//verifico che +4 effettivamente giocabile
		            	if(m.getCartaScelta() instanceof CartaSpeciale && ((CartaSpeciale)m.getCartaScelta()).getTipo()==TipoSpeciale.PIU_QUATTRO) {
		    	    		if(!partita.verificaPiuQuattroGiocabile(g)) {
		    	    			vg.stampaMessaggio("+4 giocabile solo se non hai altre opzioni!");
		    	    			return;
		    	    		}
		    	    	}
	                    // Applica la mossa e gestisci eventuale cambio colore
	                    if (partita.applicaMossa(g, m) != null) {
	                        vg.stampaMessaggio(g.getNome() + " ha giocato la carta: " + m.getCartaScelta());
	                        if (m.getCartaScelta().getColore() == Colore.NERO) {
	                            m.setTipoMossa(TipoMossa.SCEGLI_COLORE);
	                            
	                            vg.stampaColoriAsync(colore -> {
	                            	;//breakpoint

	                                vg.stampaMessaggio(g.getNome() + " ha cambiato il colore sul banco a " + colore.name());
	                                if (mossaEffettuata.compareAndSet(false, true)) {
	                                	m.getCartaScelta().setColore(colore);
	                                countdown.stop();
	                	            timerTurno.stop();
	                	            timerTurno.setOnFinished(null);
	                                partita.applicaMossa(g, m);
	                                
	                                //QUI MOSTRA PULSANTE ONE (se hai appena giocato la penultima)
	                                if (g.getMano().getNumCarte() == 1) { 
	                                	vg.stampaManoReadOnly(m.getCartaScelta(), g);
	                                    vg.mostraPulsanteONE(premuto -> {
	                                    	if (premuto) {
		                        	            vg.stampaMessaggio(g.getNome()+" ha chiamato ONE");
		                        	        } else {
		                        	            vg.stampaMessaggio(g.getNome()+" NON ha chiamato ONE in tempo -> pesca 2 carte");
		                        	            g.getMano().aggiungiCarte(partita.getMazzo().pescaN(2));
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
	                        		vg.stampaManoReadOnly(m.getCartaScelta(), g);
	                        	    vg.mostraPulsanteONE(premuto -> {
	                        	        if (premuto) {
	                        	            vg.stampaMessaggio(g.getNome()+" ha chiamato ONE");
	                        	        } else {
	                        	            vg.stampaMessaggio(g.getNome()+" NON ha chiamato ONE in tempo -> pesca 2 carte");
	                        	            g.getMano().aggiungiCarte(partita.getMazzo().pescaN(2));
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

}
