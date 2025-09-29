package onegame.client.vista;

import java.util.concurrent.CompletableFuture;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreCallbackBottoni;

public class VistaHome {
	private Scene scene;
	private AppWithMaven app;
	
	private Button giocaOnlineBtn;
	private Button giocaOfflineBtn;
	private Button statisticheBtn;
	private Button logoutBtn;

	public VistaHome(AppWithMaven app) {	
		this.app=app;
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
		
		logoutBtn = new Button("Logout");
		logoutBtn.setPrefWidth(200);
		logoutBtn.getStyleClass().add("logout");
		//orribile, sarebbe da fare dentro il controllore
//		logoutBtn.setOnAction(e -> {
//			if(app.getCs().getUtente()!=null) {
//				app.getCs().getUtente().setAnonimo(true);
//				app.getCs().getUtente().setUsername("anonimo");
//			}
//			app.mostraVistaAccesso();
//			});

		VBox root = new VBox(15, titolo, spacer0, giocaOnlineBtn, giocaOfflineBtn, statisticheBtn, logoutBtn);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));

		scene = new Scene(root);

		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());

	}

	public Scene getScene() {
		return scene;
	}
	
	/**
	 * funzione asincrona per gestione click logout btn
	 * @return click sul bottone (qualora avvenisse)
	 */
	public CompletableFuture<Void> waitForLogoutBtnClick() {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        logoutBtn.setOnAction(e -> {
//            if (!future.isDone()) {
//                future.complete(null);
//            }
//        });
//        return future;
		return GestoreCallbackBottoni.waitForClick(logoutBtn);
    }
	
	/**
	 * metodo pubblico usato dal controlloreHome per passare alla vista successiva
	 */
	public void mostraAccesso() {
		app.mostraVistaAccesso();
	}
	
	/**
	 * da usare quando sono connesso come utente anonimo
	 */
	public void disableStatisticheBtn() {
		statisticheBtn.setDisable(true);
		statisticheBtn.setOpacity(0.5);
	}
	
	/**
	 * da usare quando non sono connesso al server
	 */
	public void disableOnlineBtns() {
		giocaOnlineBtn.setDisable(true);
		giocaOnlineBtn.setOpacity(0.5);
		disableStatisticheBtn();
	}

}
