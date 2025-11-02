package onegame.client.vista.offline;


import java.util.concurrent.CompletableFuture;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.net.ConnectionMonitor;
import onegame.client.net.Utente;
import onegame.client.vista.Vista;
import onegame.client.vista.accessori.GestoreCallbackBottoni;

public class VistaMenuOffline extends Vista {

    private Button caricaBtn;
    private Button logoutBtn;
    private Label statoConnessioneLabel;
    private Button utenteBtn;

    public VistaMenuOffline(AppWithMaven app) {
    	super(app);
        BorderPane root = new BorderPane();
        
        Button homeBtn = new Button("← Home");
    	homeBtn.setOnAction(e -> app.mostraVistaHome());

    	Label titolo = new Label("GIOCA CONTRO COMPUTER");
    	titolo.getStyleClass().add("titolo");

    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(0);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);
    	
    	logoutBtn = new Button("Logout");
    	logoutBtn.getStyleClass().add("logout");

    	HBox topBar = new HBox(10);
    	topBar.setPadding(new Insets(10));
    	topBar.setAlignment(Pos.CENTER);
    	topBar.getChildren().addAll(homeBtn, leftSpacer, titolo, rightSpacer, logoutBtn);

    	root.setTop(topBar);
        
        Button nuovaBtn = new Button("Nuova partita");
        nuovaBtn.setPrefWidth(200);
        nuovaBtn.setOnAction(e -> app.mostraVistaConfigurazioneOffline());
        
        caricaBtn = new Button("Carica partita");
        caricaBtn.setPrefWidth(200);
        caricaBtn.setOnAction(e -> app.mostraVistaSalvataggi());
        
        VBox centro = new VBox(20, nuovaBtn, caricaBtn);
    	centro.setAlignment(Pos.CENTER);
    	
    	root.setCenter(centro);

		// bottom bar per stato connessione e utente loggato
		BorderPane bottomBar = new BorderPane();
		bottomBar.setPadding(new Insets(20));

		statoConnessioneLabel = new Label();
		bottomBar.setLeft(statoConnessioneLabel);

		utenteBtn = new Button();
		utenteBtn.setDisable(true);
		utenteBtn.setMaxWidth(200);
		utenteBtn.setEllipsisString("...");
		utenteBtn.setTextOverrun(OverrunStyle.ELLIPSIS);
		utenteBtn.setPadding(new Insets(5));
		utenteBtn.getStyleClass().add("logout");
		utenteBtn.setOnAction(e -> {
			app.mostraVistaImpostazioni();
		});
		bottomBar.setRight(utenteBtn);
		root.setBottom(bottomBar);
    	
        scene = new Scene(root);
        scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    }
    
    public CompletableFuture<Void> waitForLogoutBtnClick() {
		return GestoreCallbackBottoni.waitForClick(logoutBtn);
    }
    
    public void mostraAccesso() {
    	app.mostraVistaAccesso();
    }
    
    public void compilaStatoConnessione(boolean connected) {
		Platform.runLater(() -> {
            statoConnessioneLabel.setText(connected ? "Connesso ✅" : "Disconnesso ❌");
        });
	}
	
	public void disableOnlineBtns() {
		caricaBtn.setDisable(true);
		caricaBtn.setOpacity(0.5);
	}
	
	public void enableOnlineBtns() {
		caricaBtn.setDisable(false);
		caricaBtn.setOpacity(1);
	}
	
	public void aggiungiListener(ConnectionMonitor monitor, Utente utente) {
		boolean logged = !utente.isAnonimo();

	    BooleanBinding abilitato = monitor.connectedProperty().and(Bindings.createBooleanBinding(() -> logged));
		caricaBtn.disableProperty().bind(abilitato.not());
		caricaBtn.opacityProperty().bind(Bindings.when(abilitato).then(1.0).otherwise(0.5));
		
		BooleanBinding loggatoBinding = (Bindings.createBooleanBinding(() -> logged));
		utenteBtn.opacityProperty().bind(Bindings.when(loggatoBinding).then(1.0).otherwise(0.25));
		utenteBtn.textProperty().bind(Bindings.when(loggatoBinding).then(utente.getUsername()).otherwise("guest-"+utente.getUsername()));
		
		statoConnessioneLabel.textProperty()
				.bind(Bindings.when(monitor.connectedProperty()).then("Connesso ✅").otherwise("Disconnesso ❌"));
	}
}
