package onegame.client.controllore;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.VistaHome;

public class ControlloreHome {
	private ClientSocket cs;
	private VistaHome vh;
	
	public ControlloreHome(VistaHome vh, ClientSocket cs, ConnectionMonitor cm) {
		this.vh=vh;
		this.cs=cs;
		
		vh.aggiungiListener(cm, cs.getUtente());
	}
	
	public ClientSocket getCs() {
		return cs;
	}

	public void setCs(ClientSocket cs) {
		this.cs = cs;
	}

	/**
	 * funzione asincrona che slogga utente e mostra vista accesso
	 * DOPO che è stato premuto il logout btn (se viene premuto)
	 */
	public void aspettaLogout() {
		vh.waitForLogoutBtnClick().thenRun(()->{
			if(cs.getUtente()!=null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
			}
			vh.mostraAccesso();
		});
	}
	
	public void aspettaStatistiche() {
		if(!cs.getUtente().isAnonimo()) {
			vh.waitForStatisticheBtnClick().thenRun(()->{
				vh.compilaStatistiche(cs.getUtente().getUsername(), cs.getUtente().getStatistica());
			});
		}
	}
	
}
