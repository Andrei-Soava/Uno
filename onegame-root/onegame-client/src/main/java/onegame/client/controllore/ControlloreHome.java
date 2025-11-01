package onegame.client.controllore;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.VistaHome;

public class ControlloreHome extends Controllore {
	private VistaHome vh;
	
	public ControlloreHome(VistaHome vh, ClientSocket cs, ConnectionMonitor cm) {
		super(cs,cm);
		this.vh=vh;
		
		vh.aggiungiListener(cm, cs.getUtente());
		
		aspettaLogout();
		aspettaStatistiche();
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
	private void aspettaLogout() {
		vh.waitForLogoutBtnClick().thenRun(()->{
			cs.setUtente(null);
			vh.mostraAccesso();
		});
	}
	
	private void aspettaStatistiche() {
		if(!cs.getUtente().isAnonimo()) {
			vh.waitForStatisticheBtnClick().thenRun(()->{
//				vh.compilaStatistiche(cs.getUtente().getUsername(), cs.getUtente().getStatistica());
			});
		}
	}
	
}
