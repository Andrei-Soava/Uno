package vista;

import controllore.ControlloreHome;
import esecuzione.AppWithMaven;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class VistaHome {
	private Scene scene;
	private ControlloreHome ch;

	public VistaHome(AppWithMaven app, ControlloreHome ch) {
		this.ch=ch;
		System.out.println(ch.getUtente());
		
		Label titolo = new Label("Scegli modalità");
		titolo.getStyleClass().add("titolo");
		
		Region spacer0 = new Region();
    	spacer0.setPrefHeight(25);
		
		Button giocaOnlineBtn = new Button("Gioca con amici");
		giocaOnlineBtn.setPrefWidth(200);
		
		Button giocaOfflineBtn = new Button("Gioca contro computer");
		giocaOfflineBtn.setPrefWidth(200);
		giocaOfflineBtn.setOnAction(e -> app.mostraVistaIniziale());

		Button statisticheBtn = new Button("Mostra statistiche");
		statisticheBtn.setPrefWidth(200);

		Button logoutBtn = new Button("Logout");
		logoutBtn.setPrefWidth(200);
		logoutBtn.getStyleClass().add("logout");
		logoutBtn.setOnAction(e -> app.mostraVistaAccesso());

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
