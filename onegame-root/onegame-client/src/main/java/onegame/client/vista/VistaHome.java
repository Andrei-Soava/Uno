package onegame.client.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.controllore.ControlloreHome;
import onegame.client.esecuzione.AppWithMaven;

public class VistaHome {
	private Scene scene;
	
	private Button giocaOnlineBtn;
	private Button giocaOfflineBtn;
	private Button statisticheBtn;
	private Button logoutBtn;
	
	private ControlloreHome ch;

	public VistaHome(AppWithMaven app, ControlloreHome ch) {
		this.ch=ch;
		System.out.println(ch.getUtente());
		
		Label titolo = new Label("Scegli modalità");
		titolo.getStyleClass().add("titolo");
		
		Region spacer0 = new Region();
    	spacer0.setPrefHeight(25);
		
		giocaOnlineBtn = new Button("Gioca con amici");
		giocaOnlineBtn.setPrefWidth(200);
		giocaOnlineBtn.setOnAction(e -> app.mostraVistaMenuOnline());
		
		giocaOfflineBtn = new Button("Gioca contro computer");
		giocaOfflineBtn.setPrefWidth(200);
		giocaOfflineBtn.setOnAction(e -> app.mostraVistaMenuOffline());

		statisticheBtn = new Button("Mostra statistiche");
		statisticheBtn.setPrefWidth(200);
		if(ch.getUtente()==null) {
			statisticheBtn.setDisable(true);
			statisticheBtn.setOpacity(0.5);
		}

		logoutBtn = new Button("Logout");
		logoutBtn.setPrefWidth(200);
		logoutBtn.getStyleClass().add("logout");
		logoutBtn.setOnAction(e -> {
			ch.setUtente(null);
			app.mostraVistaAccesso();
			});

		VBox root = new VBox(15, titolo, spacer0, giocaOnlineBtn, giocaOfflineBtn, statisticheBtn, logoutBtn);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));

		scene = new Scene(root);

		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());

	}

	public Scene getScene() {
		return scene;
	}

}
