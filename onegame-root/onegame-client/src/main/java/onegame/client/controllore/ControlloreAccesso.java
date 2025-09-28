package onegame.client.controllore;

import onegame.client.vista.VistaAccesso;
import onegame.modello.net.Utente;

public class ControlloreAccesso {
	private VistaAccesso va;
	private Utente utente;
	
	public ControlloreAccesso(VistaAccesso va, Utente utente) {
		this.va=va;
		this.utente=utente;
	}
	
	public void eseguiAccesso() {
		va.ottieniDati((username,password)->{
			System.out.println(username);
			System.out.println(password);
			
			if(username==null && password==null) {
				utente.setAnonimo(true);
				utente.setUsername("anonimo");
				va.visualizzaHome();
				return;
			}
			if(username.length()==0 || password.length()==0) {
				va.compilaMessaggioErrore("Uno o più campi vuoti");
				eseguiAccesso();
				return;
			}
			
			//condizionale (sarà dentro una send asincrona al server e se la response == true, si va alla vista successiva)
			if(true) {
				utente.setAnonimo(false);
				utente.setUsername(username);
				va.visualizzaHome();	
			}
			else {
				va.compilaMessaggioErrore("Credenziali errate");
				va.svuotaCampi();
				eseguiAccesso();
			}
		});
	}

}
