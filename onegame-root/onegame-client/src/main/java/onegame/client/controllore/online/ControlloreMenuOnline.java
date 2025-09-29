package onegame.client.controllore.online;

import onegame.client.net.ClientSocket;
import onegame.client.vista.online.VistaMenuOnline;

public class ControlloreMenuOnline {
	private VistaMenuOnline vmo;
	private ClientSocket cs;
	
	public ControlloreMenuOnline(VistaMenuOnline vmo, ClientSocket cs) {
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
