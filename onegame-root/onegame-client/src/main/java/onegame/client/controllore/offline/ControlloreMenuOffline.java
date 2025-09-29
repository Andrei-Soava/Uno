package onegame.client.controllore.offline;

import onegame.client.net.ClientSocket;
import onegame.client.vista.offline.VistaMenuOffline;

public class ControlloreMenuOffline {

	private VistaMenuOffline vmo;
	private ClientSocket cs;
	
	public ControlloreMenuOffline(VistaMenuOffline vmo, ClientSocket cs) {
		this.vmo=vmo;
		this.cs=cs;
	}
	
	public void aspettaLogout() {
		vmo.waitForLogoutBtnClick().thenRun(()->{
			if(cs.getUtente()!=null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
			}
			vmo.mostraAccesso();
		});
	}
}
