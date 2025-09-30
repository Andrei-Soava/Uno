package onegame.client.controllore;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import onegame.client.net.ClientSocket;
import onegame.client.vista.VistaAccesso;

public class ControlloreAccesso {
	private VistaAccesso va;
	private ClientSocket cs;

	public ControlloreAccesso(VistaAccesso va, ClientSocket cs) {
		this.va = va;
		this.cs = cs;
		
		//sezione di verifica connessione
		va.compilaStatoConnessione(false);
		va.disableOnlineBtns();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
			boolean connected = cs.isConnected();
			if (connected)
				va.enableOnlineBtns();
			else
				va.disableOnlineBtns();
			va.compilaStatoConnessione(connected);
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
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
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
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
			if (true) {
				cs.getUtente().setAnonimo(false);
				cs.getUtente().setUsername(username);
				va.mostraHome();
			} else {
				va.compilaMessaggioErrore("Credenziali errate");
				va.svuotaCampi();
				eseguiAccesso();
			}
		});
	}

}
