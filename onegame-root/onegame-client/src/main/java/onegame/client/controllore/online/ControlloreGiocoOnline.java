package onegame.client.controllore.online;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;
import onegame.client.controllore.Controllore;
import onegame.client.controllore.utils.MappaUtils;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.partita.VistaGioco;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaComparator;
import onegame.modello.carte.CartaSpeciale;
import onegame.net.DTOUtils;
import onegame.net.GiocatoreDTO;
import onegame.net.MossaDTO;
import onegame.net.StatoPartitaDTO;
import onegame.net.MossaDTO.TipoMossa;
import onegame.net.messaggi.MessaggiGioco.MessStatoPartita;
import onegame.modello.carte.Colore;
import onegame.modello.carte.TipoSpeciale;

public class ControlloreGiocoOnline extends Controllore implements StatoPartitaObserver {
	private VistaGioco vg;
	private boolean mossaInviata;
	private static final CartaComparator cartaComparator = new CartaComparator();
	private PauseTransition timerTurno;
	private PauseTransition timerONE;
	private SimpleIntegerProperty secondsLeft = new SimpleIntegerProperty(8);
	private Timeline countdownTurno = new Timeline(
			new KeyFrame(Duration.seconds(1), e -> secondsLeft.set(secondsLeft.get() - 1))
			);
	
	public ControlloreGiocoOnline(VistaGioco vg, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vg=vg;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            vg.mostraHome();
	        }
	    });
		creaTimers();
		cs.setStanzaObserver(null);
		cs.setPartitaObserver(this);
		aspettaAbbandona();
		
		aggiornaPartita(cs.getStatoPartita());
	}
	
	/**
	 * funzione asincrona che capta quando viene premuto bottone di abbandono
	 * DOPO che è stato premuto il logout btn (se viene premuto)
	 */
	private void aspettaAbbandona() {
		vg.waitForAbbandonaBtnClick().thenRun(()->{
			CompletableFuture<Boolean> scelta = vg.stampaAbbandonaAlert();
	        
	        // Gestisci il risultato in modo asincrono
	        scelta.thenAccept(confermato -> {
	            if (confermato) {
	            	cs.esciStanza(null);
	            	bloccaTimerTurno();
	            	vg.mostraMenuOnline();
	            } else {
	                aspettaAbbandona();
	                return;
	            }
	        });
		});
	}
	
	/**
	 * metodo di creazione timers e countdown per turno
	 */
	private void creaTimers() {
		timerTurno = new PauseTransition(Duration.seconds(8));
		timerONE = new PauseTransition(Duration.seconds(2));
		countdownTurno.setCycleCount(8);
		vg.stampaTimerTurno(secondsLeft);
	}
	
	/**
	 * metodo che avvia timerTurno
	 */
	private void avviaTimerTurno() {
		Platform.runLater(()->{
			secondsLeft.set(8);
			countdownTurno.play();
			timerTurno.setOnFinished(fine->{
				cs.effettuaMossa(new MossaDTO(TipoMossa.PESCA_E_PASSA), null);
				timerTurno.setOnFinished(null);
				vg.chiudiFinestraAperta();
				bloccaTimerTurno();
			});
			timerTurno.play();
			vg.mostraTimerTurno(true);
			
		});
	}
	
	/**
	 * metodo che interrompe timerTurno
	 */
	private void bloccaTimerTurno() {
		Platform.runLater(()->{
			countdownTurno.stop();
			timerTurno.stop();
			vg.mostraTimerTurno(false);
		});
	}
	
	/**
	 * metodo che avvia timerONE ed imposta il setOnFinished se scade
	 */
	private void avviaTimerONE() {
		timerONE.setOnFinished(fine->{
			bloccaTimerONE();
		});
    	timerONE.play();
	}
	
	/**
	 * metodo che interrompe timerONE
	 */
	private void bloccaTimerONE() {
		Platform.runLater(()->{
			timerONE.stop();
			vg.nascondiONEBtn();
		});
	}

	@Override
	public void inizioPartita(MessStatoPartita mess) {
		//NON SERVE
	}

	@Override
	public void aggiornaPartita(MessStatoPartita mess) {
		mossaInviata = false;
		vg.chiudiFinestraAperta();
		StatoPartitaDTO statoPartita = mess.statoPartita;
		List<GiocatoreDTO> giocatori = statoPartita.giocatori;
		List<Carta> carte = DTOUtils.convertiListaDTOinCarte(mess.carteInMano);
		carte.sort(cartaComparator);
		Carta cartaCorrente = DTOUtils.convertiDTOinCarta(statoPartita.cartaCorrente);
		cartaCorrente.setColore(statoPartita.coloreCorrente);
		boolean direzione = statoPartita.direzioneCrescente;
		int posizioneAssoluta = mess.indiceGiocatoreLocale;
		int posizioneTurnoCorrente = statoPartita.indiceGiocatoreCorrente;
		vg.stampaTurnoCorrente(giocatori.get(posizioneTurnoCorrente).nickname);
		vg.stampaTurnazione(MappaUtils.creaMappa(giocatori, posizioneTurnoCorrente, posizioneAssoluta), direzione);
		
		//turno giocante
		if(posizioneAssoluta == posizioneTurnoCorrente) {
			vg.evidenziaTurnoCorrente();

			avviaTimerTurno();
			if(mess.cartaPescata==null)
				scegliMossa(cartaCorrente, carte);
			else
				gestisciCartaPescata(DTOUtils.convertiDTOinCarta(mess.cartaPescata), carte, cartaCorrente);
		} 
		else //turno spettatore
		{
			bloccaTimerTurno();
			vg.stampaManoReadOnly(cartaCorrente, carte);
		}
		
	}
	
	private void gestisciCartaPescata(Carta cartaPescata, List<Carta> carteMano, Carta cartaCorrente) {
		// verifico subito se la carta pescata è un +4
		if (cartaPescata instanceof CartaSpeciale
				&& ((CartaSpeciale) cartaPescata).getTipo() == TipoSpeciale.PIU_QUATTRO) {

			// se ci sono carte giocabili esclusi i +4, allora passo subito
			if (!verificaPiuQuattroGiocabile(cartaCorrente, carteMano)) {
				cs.effettuaMossa(new MossaDTO(TipoMossa.PASSA), null);
				bloccaTimerTurno();
				return;
			}

		}
		if (cartaPescata.giocabileSu(cartaCorrente)) {
			vg.stampaCartaPescataAsync(cartaPescata, scelta ->{
				if(scelta==1) //gioco la carta pescata
				{
					// se è una carta nera, devo prima cambiare il colore
					if (cartaPescata.getColore() == Colore.NERO) {
						vg.stampaColoriAsync(coloreScelto -> {
							gestisciInviaCarta(cartaPescata, carteMano.size(),coloreScelto);
							return;
						});
					} else // carta NON nera
					{
						gestisciInviaCarta(cartaPescata, carteMano.size());
						return;
					}
				} else //tengo la carta pescata
				{
					cs.effettuaMossa(new MossaDTO(TipoMossa.PASSA), null);
					bloccaTimerTurno();
				}
			});
			
		} else // carta non giocabile
		{
			cs.effettuaMossa(new MossaDTO(TipoMossa.PASSA), null);
			bloccaTimerTurno();
			return;
		}	
	}
	
	private void scegliMossa(Carta cartaCorrente, List<Carta> carteMano) {
		vg.scegliMossaAsync(cartaCorrente, carteMano, mossa->{
			if(mossa.getTipoMossa()==onegame.modello.Mossa.TipoMossa.PESCA) {
				cs.effettuaMossa(new MossaDTO(TipoMossa.PESCA), null);
			}
			else {
				gestisciCartaScelta(mossa.getCartaScelta(), carteMano, cartaCorrente);
			}
		});
	}
	
	private void gestisciCartaScelta(Carta cartaScelta, List<Carta> carteMano, Carta cartaCorrente) {
		// verifico subito se la cartascelta è un +4
		if (cartaScelta instanceof CartaSpeciale
				&& ((CartaSpeciale) cartaScelta).getTipo() == TipoSpeciale.PIU_QUATTRO) {

			// se ci sono carte giocabili esclusi i +4, allora è da rifare la mossa
			if (!verificaPiuQuattroGiocabile(cartaCorrente, carteMano)) {
				vg.stampaMessaggio("Puoi giocare un +4 SOLO se non hai altre opzioni");
				scegliMossa(cartaCorrente, carteMano);
				return;
			}

		}
		if (cartaScelta.giocabileSu(cartaCorrente)) {

			// se è una carta nera, devo prima cambiare il colore
			if (cartaScelta.getColore() == Colore.NERO) {
				vg.stampaColoriAsync(coloreScelto -> {
					gestisciInviaCarta(cartaScelta, carteMano.size(),coloreScelto);
					return;
				});
			} else // carta NON nera
			{
				gestisciInviaCarta(cartaScelta, carteMano.size());
				return;
			}
		} else // carta non giocabile
		{
			vg.stampaMessaggio("Carta non compatibile");
			scegliMossa(cartaCorrente, carteMano);
			return;
		}

	}
	
	private static boolean verificaPiuQuattroGiocabile(Carta cartaCorrente, List<Carta> carte) {
		ArrayList<Carta> carteInMano = new ArrayList<>();
		carteInMano.addAll(carte);
		carteInMano.removeIf(carta -> carta instanceof CartaSpeciale
				&& ((CartaSpeciale) carta).getTipo() == TipoSpeciale.PIU_QUATTRO);
		for (Carta carta : carteInMano) {
			if (carta.giocabileSu(cartaCorrente))
				return false;
		}
		return true;
	}
	
	private void gestisciInviaCarta(Carta cartaDaInviare, int numeroCarteInMano, Colore coloreScelto) {
		if (mossaInviata) return;
	    mossaInviata = true;
		MossaDTO mossaDaInviare = new MossaDTO((TipoMossa.GIOCA_CARTA));
		if(coloreScelto!=null)
			mossaDaInviare.coloreScelto = coloreScelto;
		mossaDaInviare.carta = DTOUtils.convertiCartaInDTO(cartaDaInviare);
		cs.effettuaMossa(mossaDaInviare, null);
		bloccaTimerTurno();
		if(numeroCarteInMano==2) {
			avviaTimerONE();
			vg.mostraONEBtn().thenRun(()->{
				cs.effettuaMossa(new MossaDTO(TipoMossa.DICHIARA_UNO), null);
				bloccaTimerONE();
			});
		}
	}
	
	private void gestisciInviaCarta(Carta cartaDaInviare, int numeroCarteInMano) {
		this.gestisciInviaCarta(cartaDaInviare, numeroCarteInMano, null);
	}

	@Override
	public void finePartita(MessStatoPartita mess) {
		vg.stampaFinePartita(mess.statoPartita.giocatori.get(mess.statoPartita.indiceVincitore).nickname, ()->{
			vg.mostraMenuOnline();
			cs.esciStanza(null);
		});
		
	}

}
