package controllore;

import vista.VistaAccesso;

public class ControlloreAccesso {
	private VistaAccesso va;
	private ControlloreHome ch;
	
	public ControlloreAccesso(VistaAccesso va, ControlloreHome ch) {
		this.va=va;
		this.ch=ch;
	}
	
	public void eseguiAccesso() {
		va.ottieniDati((username,password)->{
			System.out.println(username);
			System.out.println(password);
			if(username.length()==0 || password.length()==0) {
				System.out.println("Campo vuoto");
				eseguiAccesso();
			}
			
			//condizionale (sarà dentro una send asincrona al server e se la response == true, si va alla vista successiva)
			if(true) {
				ch.setUtente(username);
				va.visualizzaHome();	
			}
			else {
				System.out.println("Credenziali errate");
				va.svuotaCampi();
				eseguiAccesso();
			}
		});
	}
}
