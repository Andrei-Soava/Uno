package onegame.client.vista.partita;

import javafx.scene.Scene;
import onegame.client.esecuzione.AppWithMaven;

public abstract class VistaPartita {
	protected AppWithMaven app;
	protected VistaPartita(AppWithMaven app) {
		this.app=app;
	}
	public void mostraVista() {
		app.aggiornaVistaPartita(this);
	}
	public abstract Scene getScene();
}
