package onegame.client.controllore;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import onegame.client.net.ClientSocket;
import onegame.client.vista.VistaHome;

public class ControlloreHome {
	private ClientSocket cs;
	private VistaHome vh;
	
	public ControlloreHome(VistaHome vh, ClientSocket cs) {
		this.vh=vh;
		this.cs=cs;
		
		//sezione di verifica connessione
		vh.compilaStatoConnessione(false);
		vh.disableOnlineBtns();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
			boolean connected = cs.isConnected();
			if (connected)
				vh.enableOnlineBtns();
			else
				vh.disableOnlineBtns();
			vh.compilaStatoConnessione(connected);
		}));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
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
	
}
