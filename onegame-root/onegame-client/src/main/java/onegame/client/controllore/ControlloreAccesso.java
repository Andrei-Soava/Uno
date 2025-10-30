package onegame.client.controllore;


import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.net.Utente;
import onegame.client.vista.VistaAccesso;

public class ControlloreAccesso {
	private VistaAccesso va;
	private ClientSocket cs;

	public ControlloreAccesso(VistaAccesso va, ClientSocket cs, ConnectionMonitor cm) {
		this.va = va;
		this.cs = cs;
		
		va.aggiungiListener(cm);
		eseguiAccesso();
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
				gestisciAccessoAnonimo();
				return;
			}
			if (username.length() == 0 || password.length() == 0) {
				va.compilaMessaggioErrore("Uno o più campi vuoti");
				eseguiAccesso();
				return;
			}

			// condizionale (sarà dentro una send asincrona al server e se la response ==
			// true, si va alla vista successiva)
			cs.login(username, password, respAuth -> {

				Platform.runLater(() -> {
					if (respAuth.success) {
						Utente utente = new Utente(username, false);
						cs.setUtente(utente);
						cs.setIdentificatore(respAuth.username);
						va.mostraHome();
					} else {
						va.compilaMessaggioErrore(respAuth.messaggio);
						eseguiAccesso();
					}
				});

			});
		});
	}
	
	private void gestisciAccessoAnonimo() {
		va.mostraDialogNicknameOspite(contesto->{
			if(!contesto.getNickname().matches("^[a-zA-Z0-9_]{3,44}$")) {
				if(contesto.getNickname().length()<3)
					contesto.getErroreLbl().setText("Almeno 3 caratteri");
				else if (contesto.getNickname().length()>44)
					contesto.getErroreLbl().setText("Meno di 44 caratteri");
				else
					contesto.getErroreLbl().setText("Formato non valido");
				return;
			}

			cs.anonimo(contesto.getNickname(), respAnonimo -> {
				Platform.runLater(()->{
					cs.setIdentificatore(respAnonimo.username);
					cs.setUtente(new Utente(true));
					cs.getUtente().setUsername(contesto.getNickname());
					contesto.getDialog().close();
					va.mostraHome();
				});
			});
			return;
		}, () -> {
	        eseguiAccesso();
	    });
	}

}
