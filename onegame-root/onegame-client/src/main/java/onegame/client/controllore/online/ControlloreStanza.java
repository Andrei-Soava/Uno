package onegame.client.controllore.online;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;
import onegame.modello.net.StatoStanzaDTO;
import onegame.modello.net.messaggi.MessaggiGioco.MessStatoPartita;

public class ControlloreStanza implements StatoStanzaObserver, StatoPartitaObserver{

	private VistaStanza vs;
	private ClientSocket cs;
	
	public ControlloreStanza(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
		this.vs=vs;
		this.cs=cs;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vs.mostraHome();
	        }
	    });
		
		cs.setStanzaObserver(this);
		cs.setPartitaObserver(this);
		aspettaAbbandonaStanza();
		aggiornaStanza(cs.getStatoStanza());
	}
	
	public void aspettaAbbandonaStanza() {
		vs.waitForAbbandonaBtnClick().thenRun(()->{
			cs.esciStanza(null);
			vs.mostraMenuOnline();
			});
	}
	
	
	
	@Override
	public void inizioPartita(MessStatoPartita mess) {
		Platform.runLater(()->{
			vs.mostraGiocoOnline();
		});
	}

	@Override
	public void aggiornaPartita(MessStatoPartita mess) {
		//NON SERVE
	}

	@Override
	public void finePartita(MessStatoPartita mess) {
		//NON SERVE
	}

	@Override
	public void aggiornaStanza(StatoStanzaDTO stato) {
		Platform.runLater(()->{
			vs.aggiornaGiocatori(stato.nicknames, stato.maxUtenti);
			if(stato.usernames.get(stato.indiceProprietario).equals(cs.getIdentificatore())) {
				vs.mostraAvviaBtn();
				if(stato.nicknames.size()>1) {
					vs.attivaAvviaBtn();
					aspettaAvviaPartita();
				}
				else
					vs.disattivaAvviaBtn();
			} else {
				vs.nascodiAvviaBtn();
				vs.disattivaAvviaBtn();
			}
		});
	}
	
	private void aspettaAvviaPartita() {
		vs.waitForAvviaBtnClick().thenRun(()->{
			cs.iniziaPartita(null);
		});
	}
	
}
