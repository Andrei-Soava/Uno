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
				cs.anonimo(args -> {});
				cs.setUtente(new Utente(true));
				va.inserisciNickname("Scegli un nome utente").thenAccept(nickname->{
					//se viene cliccato annulla o X
					if(nickname==null) {
						eseguiAccesso();
						return;
					}
					cs.getUtente().setUsername(nickname);
					va.mostraHome();
					return;
				});
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
						va.mostraHome();
					} else {
						va.compilaMessaggioErrore(respAuth.messaggio);
						eseguiAccesso();
					}
				});

			});
		});
	}

}
