package controllore;

import vista.VistaRegistrazione;

public class ControlloreRegistrazione {
	private VistaRegistrazione vr;
	
	public ControlloreRegistrazione(VistaRegistrazione vr) {
		this.vr=vr;
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
				vr.visualizzaAccesso();	
			}
			else {
				vr.compilaMessaggioErrore("Dati errati");
				vr.svuotaPassword();
				eseguiRegistrazione();
			}
		});
	}
}
