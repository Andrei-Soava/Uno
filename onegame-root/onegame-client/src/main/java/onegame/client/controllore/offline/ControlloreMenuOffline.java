package onegame.client.controllore.offline;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import onegame.client.net.ClientSocket;
import onegame.client.vista.offline.VistaMenuOffline;

public class ControlloreMenuOffline {

	private VistaMenuOffline vmo;
	private ClientSocket cs;

	public ControlloreMenuOffline(VistaMenuOffline vmo, ClientSocket cs) {
		this.vmo = vmo;
		this.cs = cs;

		// sezione di verifica connessione
		vmo.compilaStatoConnessione(false);
		vmo.disableOnlineBtns();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
			boolean connected = cs.isConnected();
			if (connected)
				vmo.enableOnlineBtns();
			else
				vmo.disableOnlineBtns();
			vmo.compilaStatoConnessione(connected);
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
