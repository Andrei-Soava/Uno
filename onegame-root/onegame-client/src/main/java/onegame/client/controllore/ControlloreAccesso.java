package onegame.client.controllore;


import javax.management.RuntimeErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.VistaAccesso;
import onegame.modello.net.ProtocolloMessaggi.RespAuth;
import onegame.modello.net.util.JsonHelper;
import onegame.modello.net.Utente;

public class ControlloreAccesso {
	private VistaAccesso va;
	private ClientSocket cs;

	public ControlloreAccesso(VistaAccesso va, ClientSocket cs, ConnectionMonitor cm) {
		this.va = va;
		this.cs = cs;
		
		va.aggiungiListener(cm);
	}

	public void eseguiAccesso() {
		va.ottieniDati((username, password) -> {
			System.out.println(username);
			System.out.println(password);
			if (!cs.isConnected()) {
				va.mostraHome();
				return;
			}
			if (username == null && password == null) {
//				cs.getUtente().setAnonimo(true);
//				cs.getUtente().setUsername("anonimo");
				cs.anonimo(null);
				cs.setUtente(new Utente(true));
				va.mostraHome();
				return;
			}
			if (username.length() == 0 || password.length() == 0) {
				va.compilaMessaggioErrore("Uno o più campi vuoti");
				eseguiAccesso();
				return;
			}

			// condizionale (sarà dentro una send asincrona al server e se la response ==
			// true, si va alla vista successiva)
//			if (true) {
//				cs.getUtente().setAnonimo(false);
//				cs.getUtente().setUsername(username);
//				va.mostraHome();
//			} else {
//				va.compilaMessaggioErrore("Credenziali errate");
//				va.svuotaCampi();
//				eseguiAccesso();
//			}
			try {
				cs.login(username, password, args -> {
					try {
						String json = args[0].toString();
						RespAuth auth = JsonHelper.fromJson(json, RespAuth.class);
						
						Platform.runLater(() -> {
				            if (auth.success) {
				                Utente utente = new Utente(username, false);
				                cs.setUtente(utente);
				                va.mostraHome();
				            } else {
				                va.compilaMessaggioErrore(auth.messaggio);
				                eseguiAccesso();
				            }
				        });
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (Exception e) {
				
			}
		});
	}

}
