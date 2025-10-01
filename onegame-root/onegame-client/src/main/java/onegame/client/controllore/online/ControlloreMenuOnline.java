package onegame.client.controllore.online;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaMenuOnline;

public class ControlloreMenuOnline {
	private VistaMenuOnline vmo;
	private ClientSocket cs;

	public ControlloreMenuOnline(VistaMenuOnline vmo, ClientSocket cs, ConnectionMonitor cm) {
		this.vmo = vmo;
		this.cs = cs;
	    cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vmo.mostraHome();
	        }
	    });
		
	}

	public void aspettaLogout() {
		vmo.waitForLogoutBtnClick().thenRun(() -> {
			if (cs.getUtente() != null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
			}
			vmo.mostraHome();
		});
	}
}
