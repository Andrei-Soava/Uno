package onegame.client.controllore.offline;


import onegame.client.controllore.Controllore;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.offline.VistaMenuOffline;

public class ControlloreMenuOffline extends Controllore {
	private VistaMenuOffline vmo;

	public ControlloreMenuOffline(VistaMenuOffline vmo, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vmo = vmo;

		// sezione di verifica connessione
		vmo.aggiungiListener(cm, cs.getUtente());
		
		aspettaLogout();
	}
	
	

	private void aspettaLogout() {
		vmo.waitForLogoutBtnClick().thenRun(() -> {
			cs.setUtente(null);
			vmo.mostraAccesso();
		});
	}
}
