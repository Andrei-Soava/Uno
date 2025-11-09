package onegame.client.controllore.online;

import javafx.application.Platform;
import onegame.client.controllore.Controllore;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaInserimentoCodice;

public class ControlloreCodicePartita extends Controllore {
	private VistaInserimentoCodice vic;
	
	public ControlloreCodicePartita(VistaInserimentoCodice vic, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vic=vic;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vic.mostraHome();
	        }
	    });
		
		eseguiAccesso();
	}
	
	private void eseguiAccesso() {
		vic.ottieniCodice((codice)->{
			if(codice.length()==0) {
				vic.compilaMessaggioErrore("Codice nullo. Riprovare");
				eseguiAccesso();
				return;
			}
			
			try {
				int codiceParsato=Integer.parseInt(codice);
				cs.entraStanza(codiceParsato, respEntraStanza -> {
					Platform.runLater(()->{
						if(respEntraStanza.success) {
							vic.mostraStanza(codice);
						} else {
							vic.compilaMessaggioErrore(respEntraStanza.messaggio);
							vic.svuotaCampoCodice();
							eseguiAccesso();
							return;
						}
					});
				});
			} catch (NumberFormatException e) {
				vic.compilaMessaggioErrore("Formato codice non valido. Riprovare");
				vic.svuotaCampoCodice();
				eseguiAccesso();
			}
		});
	}
}
