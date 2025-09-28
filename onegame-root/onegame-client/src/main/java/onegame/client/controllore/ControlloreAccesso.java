package onegame.client.controllore;

import onegame.client.net.ClientSocket;
import onegame.client.vista.VistaAccesso;
import onegame.modello.net.Utente;

public class ControlloreAccesso {
	private VistaAccesso va;
	private ClientSocket cs;
	
	public ControlloreAccesso(VistaAccesso va, ClientSocket cs) {
		this.va=va;
		this.cs=cs;
	}
	
	public void eseguiAccesso() {
		va.ottieniDati((username,password)->{
			System.out.println(username);
			System.out.println(password);
			if(!cs.isConnected()) {
				va.visualizzaHome();
				return;
			}
			if(username==null && password==null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
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
				cs.getUtente().setAnonimo(false);
				cs.getUtente().setUsername(username);
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
