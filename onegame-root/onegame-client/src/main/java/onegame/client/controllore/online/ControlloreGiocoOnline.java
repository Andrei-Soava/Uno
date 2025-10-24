package onegame.client.controllore.online;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javafx.animation.Animation;
import onegame.client.controllore.online.utils.CartaUtils;
import onegame.client.controllore.online.utils.MappaUtils;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.partita.VistaGioco;
import onegame.modello.carte.Carta;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.MossaDTO.TipoMossa;
import onegame.modello.net.StatoPartitaDTO;

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
		
		aspettaAbbandona();
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
	            	vg.mostraMenuOnline();
	            } else {
	                aspettaAbbandona();
	                return;
	            }
	        });
		});
	}

	@Override
	public void inizioPartita(StatoPartitaDTO stato) {
		//NON SERVE
	}

	@Override
	public void aggiornaPartita(StatoPartitaDTO stato) {
		List<GiocatoreDTO> giocatori = stato.giocatori;
		List<Carta> carte = CartaUtils.convertiCartaDTOinCarta(stato.carteInMano);
		Carta cartaCorrente = CartaUtils.convertiCartaDTOinCarta(stato.cartaCorrente);
		boolean direzione = stato.direzioneCrescente;
		int posizioneAssoluta = stato.indiceGiocatoreLocale;
		int posizioneTurnoCorrente = stato.indiceGiocatoreCorrente;
		vg.stampaTurnoCorrente(giocatori.get(posizioneTurnoCorrente).nickname);
		vg.stampaTurnazione(MappaUtils.creaMappa(giocatori, posizioneTurnoCorrente, posizioneAssoluta), direzione);
		
		//effettivo turno
		if(posizioneAssoluta == posizioneTurnoCorrente) {
			vg.evidenziaTurnoCorrente();
			vg.scegliMossaAsync(cartaCorrente, carte, mossa->{
				if(mossa.getTipoMossa()==onegame.modello.Mossa.TipoMossa.PESCA) {
					//COME OTTENGO LA CARTA PESCATA????
					cs.effettuaMossa(new MossaDTO(TipoMossa.PESCA), callback->{
						
					});
				}
			});
			
		} 
		else //turno spettatore
		{
			vg.stampaManoReadOnly(cartaCorrente, carte);
		}
		
	}

	@Override
	public void finePartita(StatoPartitaDTO stato) {
		vg.stampaFinePartita(stato.giocatori.get(stato.indiceVincitore).nickname, ()->{
			vg.mostraMenuOnline();
		});
		
	}

}
