package onegame.client.controllore.online;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import onegame.client.net.ClientSocket;
import onegame.client.vista.online.VistaMenuOnline;

public class ControlloreMenuOnline {
	private VistaMenuOnline vmo;
	private ClientSocket cs;

	public ControlloreMenuOnline(VistaMenuOnline vmo, ClientSocket cs) {
		this.vmo = vmo;
		this.cs = cs;
		// sezione di verifica connessione
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
			boolean connected = cs.isConnected();
			if (!connected)
				vmo.mostraAccesso();
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	public void aspettaLogout() {
		vmo.waitForLogoutBtnClick().thenRun(() -> {
			if (cs.getUtente() != null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
			}
			vmo.mostraAccesso();
		});
	}
}
