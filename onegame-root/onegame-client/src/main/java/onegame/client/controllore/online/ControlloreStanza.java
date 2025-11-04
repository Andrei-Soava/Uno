package onegame.client.controllore.online;

import javafx.application.Platform;
import onegame.client.controllore.Controllore;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;
import onegame.net.StatoStanzaDTO;
import onegame.net.messaggi.MessaggiGioco.MessStatoPartita;

public class ControlloreStanza extends Controllore implements StatoStanzaObserver, StatoPartitaObserver{
	private VistaStanza vs;
	
	public ControlloreStanza(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vs=vs;
		
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
	
	private void aspettaAbbandonaStanza() {
		vs.waitForAbbandonaBtnClick().thenRun(()->{
			cs.esciStanza(null);
			vs.mostraMenuOnline();
			});
	}
	
	private void aspettaAvviaPartita() {
		vs.waitForAvviaBtnClick().thenRun(()->{
			cs.iniziaPartita(null);
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
			vs.compilaNomePartita(stato.nomeStanza);
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
}
