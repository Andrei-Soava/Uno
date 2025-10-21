package onegame.client.controllore.offline;


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
