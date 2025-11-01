package onegame.client.controllore.online;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import onegame.client.controllore.Controllore;
import onegame.client.controllore.utils.MappaUtils;
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

public class ControlloreGiocoOnline extends Controllore implements StatoPartitaObserver {
	private VistaGioco vg;
	
	public ControlloreGiocoOnline(VistaGioco vg, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
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
		vg.chiudiFinestraAperta();
		StatoPartitaDTO statoPartita = mess.statoPartita;
		List<GiocatoreDTO> giocatori = statoPartita.giocatori;
		List<Carta> carte = DTOUtils.convertiListaDTOinCarte(mess.carteInMano);
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
			if(mess.cartePescate.size()==0)
				scegliMossa(cartaCorrente, carte);
			else
				gestisciCartaPescata(DTOUtils.convertiDTOinCarta(mess.cartePescate.getFirst()), carte, cartaCorrente);
		} 
		else //turno spettatore
		{
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
							cartaPescata.setColore(coloreScelto);
							gestisciInviaCarta(cartaPescata, carteMano.size());
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
				}
			});
			
		} else // carta non giocabile
		{
			cs.effettuaMossa(new MossaDTO(TipoMossa.PASSA), null);
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
					cartaScelta.setColore(coloreScelto);
					gestisciInviaCarta(cartaScelta, carteMano.size());
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
	
	private void gestisciInviaCarta(Carta cartaDaInviare, int numeroCarteInMano) {
		MossaDTO mossaDaInviare = new MossaDTO((TipoMossa.GIOCA_CARTA));
		if(cartaDaInviare instanceof CartaSpeciale && 
				(((CartaSpeciale)cartaDaInviare).getTipo()==TipoSpeciale.JOLLY ||
				((CartaSpeciale)cartaDaInviare).getTipo()==TipoSpeciale.PIU_QUATTRO
				)) {
			mossaDaInviare.coloreScelto = cartaDaInviare.getColore();
			cartaDaInviare.setColore(Colore.NERO);
		}
		mossaDaInviare.carta = DTOUtils.convertiCartaInDTO(cartaDaInviare);
		cs.effettuaMossa(mossaDaInviare, null);
		if(numeroCarteInMano==2) {
			vg.mostraONEBtn().thenRun(()->{
				System.out.println("CHIAMO UNO");
				cs.effettuaMossa(new MossaDTO(TipoMossa.DICHIARA_UNO), null);
				System.out.println("CHIAMATO UNO");
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
