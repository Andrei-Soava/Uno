package onegame.client.controllore;

import onegame.client.vista.online.VistaInserimentoCodice;

public class ControlloreCodicePartita {
	private VistaInserimentoCodice vic;
	
	public ControlloreCodicePartita(VistaInserimentoCodice vic) {
		this.vic=vic;
	}
	
	public void eseguiAccesso() {
		vic.ottieniCodice((codice)->{
			System.out.println(codice);
			if(codice.length()==0) {
				vic.compilaMessaggioErrore("Codice nullo. Riprovare");
				eseguiAccesso();
				return;
			}
			
			//condizionale (sarà dentro una send asincrona al gameserver e se la response == true, si va alla vista successiva)
			if(true) {
				vic.visualizzaStanza();	
			}
			else {
				vic.compilaMessaggioErrore("Codice non valido. Riprovare");
				vic.svuotaCampoCodice();
				eseguiAccesso();
			}
		});
	}
}
