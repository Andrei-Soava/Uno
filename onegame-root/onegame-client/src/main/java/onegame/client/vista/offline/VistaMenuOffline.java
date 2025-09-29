package onegame.client.vista.offline;


import java.util.concurrent.CompletableFuture;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreCallbackBottoni;

public class VistaMenuOffline {

    private Scene scene;
    private AppWithMaven app;
    private Button logoutBtn;

    public VistaMenuOffline(AppWithMaven app) {
    	this.app=app;
        BorderPane root = new BorderPane();
        
        Button homeBtn = new Button("← Home");
    	homeBtn.setOnAction(e -> app.mostraVistaHome());

    	Label titolo = new Label("Gioca contro computer");
    	titolo.getStyleClass().add("titolo");

    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(0);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);
    	
    	logoutBtn = new Button("Logout");
    	logoutBtn.getStyleClass().add("logout");
    	//logoutBtn.setOnAction(e->app.mostraVistaAccesso());

    	HBox topBar = new HBox(10);
    	topBar.setPadding(new Insets(10));
    	topBar.setAlignment(Pos.CENTER);
    	topBar.getChildren().addAll(homeBtn, leftSpacer, titolo, rightSpacer, logoutBtn);

    	root.setTop(topBar);
        
        Button nuovaBtn = new Button("Nuova partita");
        nuovaBtn.setPrefWidth(200);
        nuovaBtn.setOnAction(e -> app.mostraVistaConfigurazioneOffline());
        
        Button caricaBtn = new Button("Carica partita");
        caricaBtn.setPrefWidth(200);
        caricaBtn.setOnAction(e -> app.mostraVistaSalvataggi());
        
        VBox centro = new VBox(20, nuovaBtn, caricaBtn);
    	centro.setAlignment(Pos.CENTER);
    	
    	root.setCenter(centro);
        
        scene = new Scene(root);
        scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    }

    public Scene getScene() {
        return scene;
    }
    
    public CompletableFuture<Void> waitForLogoutBtnClick() {
		return GestoreCallbackBottoni.waitForClick(logoutBtn);
    }
    
    public void mostraAccesso() {
    	app.mostraVistaAccesso();
    }
}
