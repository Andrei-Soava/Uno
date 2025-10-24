package onegame.client.controllore.online.stanza;

import javafx.application.Platform;
import onegame.client.controllore.online.StatoPartitaObserver;
import onegame.client.controllore.online.StatoStanzaObserver;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;
import onegame.modello.net.StatoPartitaDTO;
import onegame.modello.net.StatoStanzaDTO;

public abstract class ControlloreStanza implements StatoStanzaObserver, StatoPartitaObserver{

	protected VistaStanza vs;
	protected ClientSocket cs;
	
	protected ControlloreStanza(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
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
	}
	
	public abstract void aspetta();
	
	public void aspettaAbbandonaStanza() {
		vs.waitForAbbandonaBtnClick().thenRun(()->{
			cs.esciStanza(null);
			vs.mostraMenuOnline();
			});
	}
	
	
	
	@Override
	public void inizioPartita(StatoPartitaDTO stato) {
		vs.mostraGiocoOnline();
	}

	@Override
	public void aggiornaPartita(StatoPartitaDTO stato) {
		//NON SERVE
	}

	@Override
	public void finePartita(StatoPartitaDTO stato) {
		//NON SERVE
	}

	@Override
	public void aggiornaStanza(StatoStanzaDTO stato) {
		Platform.runLater(()->{
			vs.aggiornaGiocatori(stato.nicknames, stato.maxUtenti);
		});
	}
	
}
