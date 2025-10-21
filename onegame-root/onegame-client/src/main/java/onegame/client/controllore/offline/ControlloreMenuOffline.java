package onegame.client.controllore.offline;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.offline.VistaMenuOffline;

public class ControlloreMenuOffline {

	private VistaMenuOffline vmo;
	private ClientSocket cs;

	public ControlloreMenuOffline(VistaMenuOffline vmo, ClientSocket cs, ConnectionMonitor cm) {
		this.vmo = vmo;
		this.cs = cs;

		// sezione di verifica connessione
		vmo.aggiungiListener(cm, cs.getUtente());
		
		aspettaLogout();
	}
	
	

	public void aspettaLogout() {
		vmo.waitForLogoutBtnClick().thenRun(() -> {
			cs.disconnect();
			vmo.mostraAccesso();
		});
	}
}
