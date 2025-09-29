package onegame.client.controllore;

import onegame.client.net.ClientSocket;
import onegame.client.vista.VistaRegistrazione;

public class ControlloreRegistrazione {
	private VistaRegistrazione vr;
	private ClientSocket cs;
	
	public ControlloreRegistrazione(VistaRegistrazione vr, ClientSocket cs) {
		this.vr=vr;
		this.cs=cs;
	}
	
	public void eseguiRegistrazione() {
		vr.ottieniDati((username,password,confermaPassword)->{
			System.out.println(username);
			System.out.println(password);
			if(username.length()==0 || password.length()==0 || confermaPassword.length()==0) {
				vr.compilaMessaggioErrore("Uno o più campi vuoti");
				eseguiRegistrazione();
				return;
			}
			
			if(password.length()<8) {
				vr.compilaMessaggioErrore("Password troppo corta");
				vr.svuotaPassword();
				eseguiRegistrazione();
				return;
			}
			
			if(!password.equals(confermaPassword)) {
				vr.compilaMessaggioErrore("La password di conferma non è uguale alla password inserita");
				vr.svuotaPassword();
				eseguiRegistrazione();
				return;
			}
			
			//condizionale (sarà dentro una send asincrona al server e se la response == true, si ritorna alla vista d'accesso)
			if(true) {
				vr.mostraAccesso();	
			}
			else {
				vr.compilaMessaggioErrore("Dati errati");
				vr.svuotaPassword();
				eseguiRegistrazione();
			}
		});
	}
}
