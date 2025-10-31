package onegame.client.vista;

import javafx.scene.Scene;
import onegame.client.esecuzione.AppWithMaven;

public abstract class Vista {
	protected AppWithMaven app;
	protected Scene scene;
	
	protected Vista(AppWithMaven app) {
		this.app = app;
	}
	
	public Scene getScene() {
		return scene;
	}
}
