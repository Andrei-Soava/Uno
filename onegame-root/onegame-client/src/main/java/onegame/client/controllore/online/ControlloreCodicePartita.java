package onegame.client.controllore.online;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaInserimentoCodice;
import onegame.modello.net.ProtocolloMessaggi.RespEntraStanza;
import onegame.modello.net.util.JsonHelper;

public class ControlloreCodicePartita {
	private VistaInserimentoCodice vic;
	private ClientSocket cs;
	
	public ControlloreCodicePartita(VistaInserimentoCodice vic, ClientSocket cs, ConnectionMonitor cm) {
		this.vic=vic;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vic.mostraHome();
	        }
	    });
	}
	
	public void eseguiAccesso() {
		vic.ottieniCodice((codice)->{
			System.out.println(codice);
			if(codice.length()==0) {
				vic.compilaMessaggioErrore("Codice nullo. Riprovare");
				eseguiAccesso();
				return;
			}
			
			
			cs.entraStanza(codice, args->{
				String json=args[0].toString();
				RespEntraStanza risposta=JsonHelper.fromJson(json, RespEntraStanza.class);
				if(risposta.success) {
					vic.mostraStanza(codice);
				} else {
					vic.compilaMessaggioErrore(risposta.messaggio);
					vic.svuotaCampoCodice();
					eseguiAccesso();
					return;
				}
			});
//			//condizionale (sarà dentro una send asincrona al gameserver e se la response == true, si va alla vista successiva)
//			if(true) {
//				vic.mostraStanza(codice);	
//			}
//			else {
//				vic.compilaMessaggioErrore("Codice non valido. Riprovare");
//				vic.svuotaCampoCodice();
//				eseguiAccesso();
//			}
		});
	}
}
