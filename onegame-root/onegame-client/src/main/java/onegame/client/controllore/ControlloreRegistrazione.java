package onegame.client.controllore;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.net.Utente;
import onegame.client.vista.VistaRegistrazione;
import onegame.modello.net.ProtocolloMessaggi.RespAuth;
import onegame.modello.net.util.JsonHelper;

public class ControlloreRegistrazione {
	private VistaRegistrazione vr;
	private ClientSocket cs;
	
	public ControlloreRegistrazione(VistaRegistrazione vr, ClientSocket cs, ConnectionMonitor cm) {
		this.vr=vr;
		this.cs=cs;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vr.mostraAccesso();
	        }
	    });
		
		eseguiRegistrazione();
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
//			if(true) {
//				vr.mostraAccesso();	
//			}
//			else {
//				vr.compilaMessaggioErrore("Dati errati");
//				vr.svuotaPassword();
//				eseguiRegistrazione();
//			}
			
			try {
				cs.register(username, confermaPassword, respAuth -> {
					try {
						Platform.runLater(() -> {
						    if (respAuth.success) {
						        Utente utente = new Utente(username, false);
						        cs.setUtente(utente);
						        vr.mostraAccesso();
						    } else {
						        vr.compilaMessaggioErrore(respAuth.messaggio);
						        vr.svuotaPassword();
						        eseguiRegistrazione();
						    }
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
