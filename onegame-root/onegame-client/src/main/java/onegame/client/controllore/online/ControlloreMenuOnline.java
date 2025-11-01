package onegame.client.controllore.online;

import onegame.client.controllore.Controllore;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaMenuOnline;

public class ControlloreMenuOnline extends Controllore {
	private VistaMenuOnline vmo;

	public ControlloreMenuOnline(VistaMenuOnline vmo, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vmo = vmo;
	    cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vmo.mostraAccesso();
	        }
	    });
		
	    aspettaLogout();
	}

	private void aspettaLogout() {
		vmo.waitForLogoutBtnClick().thenRun(() -> {
			cs.setUtente(null);
			vmo.mostraAccesso();
		});
	}
}
