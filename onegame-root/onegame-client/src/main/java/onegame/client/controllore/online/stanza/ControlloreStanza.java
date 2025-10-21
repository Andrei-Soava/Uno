package onegame.client.controllore.online.stanza;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;

public abstract class ControlloreStanza {

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
		
		aggiornaStanza();
		aspettaAbbandonaStanza();
	}
	
	public abstract void aspettaInizioPartita();
	
	public void aspettaAbbandonaStanza() {
		vs.waitForAbbandonaBtnClick().thenRun(()->{
			cs.esciStanza(null);
			vs.mostraMenuOnline();
			});
	}
	
	public void aggiornaStanza() {
		cs.dettagliStanza(dettagli->{
			Platform.runLater(()->{
				vs.aggiornaGiocatori(dettagli.statoStanza.nicknames, dettagli.statoStanza.maxUtenti);
				aggiornaStanza();
				return;
			});
		});
	}
}
