package onegame.client.controllore.online;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import onegame.client.controllore.online.utils.MappaUtils;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.partita.VistaGioco;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
import onegame.modello.carte.Colore;
import onegame.modello.net.DTOUtils;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.MossaDTO.TipoMossa;
import onegame.modello.net.StatoPartitaDTO;
import onegame.modello.net.messaggi.MessaggiGioco.MessStatoPartita;

public class ControlloreGiocoOnline implements StatoPartitaObserver {
	private ClientSocket cs;
	private VistaGioco vg;
	
	public ControlloreGiocoOnline(ClientSocket cs, ConnectionMonitor cm, VistaGioco vg) {
		this.cs=cs;
		this.vg=vg;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            vg.mostraHome();
	        }
	    });
		
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
	            	vg.mostraMenuOnline();
	            } else {
	                aspettaAbbandona();
	                return;
	            }
	        });
		});
	}

	@Override
	public void inizioPartita(MessStatoPartita mess) {
		//NON SERVE
	}

	@Override
	public void aggiornaPartita(MessStatoPartita mess) {
		StatoPartitaDTO statoPartita = mess.statoPartita;
		List<GiocatoreDTO> giocatori = statoPartita.giocatori;
		List<Carta> carte = DTOUtils.convertiListaDTOinCarte(mess.carteInMano);
		Carta cartaCorrente = DTOUtils.convertiDTOinCarta(statoPartita.cartaCorrente);
		boolean direzione = statoPartita.direzioneCrescente;
		int posizioneAssoluta = mess.indiceGiocatoreLocale;
		int posizioneTurnoCorrente = statoPartita.indiceGiocatoreCorrente;
		vg.stampaTurnoCorrente(giocatori.get(posizioneTurnoCorrente).nickname);
		vg.stampaTurnazione(MappaUtils.creaMappa(giocatori, posizioneTurnoCorrente, posizioneAssoluta), direzione);
		
		//turno giocante
		if(posizioneAssoluta == posizioneTurnoCorrente) {
			vg.evidenziaTurnoCorrente();
			scegliMossa(cartaCorrente, carte);
		} 
		else //turno spettatore
		{
			vg.stampaManoReadOnly(cartaCorrente, carte);
		}
		
	}
	
	private void scegliMossa(Carta cartaCorrente, List<Carta> carteMano) {
		System.out.println("Scegli mossa...");
		vg.scegliMossaAsync(cartaCorrente, carteMano, mossa->{
			if(mossa.getTipoMossa()==onegame.modello.Mossa.TipoMossa.PESCA) {
				//COME OTTENGO LA CARTA PESCATA????
				cs.effettuaMossa(new MossaDTO(TipoMossa.PESCA), callback->{
					
				});
			}
			else {
				gestisciCartaScelta(mossa.getCartaScelta(), carteMano, cartaCorrente);
			}
		});
	}
	
	private void gestisciCartaScelta(Carta cartaScelta, List<Carta> carteMano, Carta cartaCorrente) {
		System.out.println("Carta scelta: "+cartaScelta);
		//verifico subito se la cartascelta è un +4
		if(cartaScelta instanceof CartaSpeciale && ((CartaSpeciale)cartaScelta).getTipo()==TipoSpeciale.PIU_QUATTRO) {
			
			//se ci sono carte giocabili esclusi i +4, allora è da rifare la mossa
			if(!verificaPiuQuattroGiocabile(cartaCorrente, carteMano)) {
				vg.stampaMessaggio("Puoi giocare un +4 SOLO se non hai altre opzioni");
				scegliMossa(cartaCorrente, carteMano);
				return;
			}
			
			if(cartaScelta.giocabileSu(cartaCorrente)) {
				
				//se è una carta nera, devo prima cambiare il colore
				if(cartaScelta.getColore()==Colore.NERO) {
					vg.stampaColoriAsync(coloreScelto->{
						cartaScelta.setColore(coloreScelto);
						gestisciInviaCarta(cartaScelta, carteMano.size());
						return;
					});
				}
				else //carta NON nera
				{
					gestisciInviaCarta(cartaScelta, carteMano.size());
					return;
				}
			}
			else //carta non giocabile
			{
				vg.stampaMessaggio("Carta non compatibile");
				scegliMossa(cartaCorrente, carteMano);
				return;
			}
			
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
	
	private void gestisciInviaCarta(Carta cartaDaInviare, int numeroCarteInMano) {
		cs.effettuaMossa(new MossaDTO((TipoMossa.GIOCA_CARTA), DTOUtils.convertiCartaInDTO(cartaDaInviare)), null);
		if(numeroCarteInMano==2) {
			vg.mostraONEBtn().thenRun(()->{
				cs.effettuaMossa(new MossaDTO(TipoMossa.DICHIARA_UNO), null);
			});
		}
	}

	@Override
	public void finePartita(MessStatoPartita mess) {
		vg.stampaFinePartita(mess.statoPartita.giocatori.get(mess.statoPartita.indiceVincitore).nickname, ()->{
			vg.mostraMenuOnline();
		});
		
	}

}
