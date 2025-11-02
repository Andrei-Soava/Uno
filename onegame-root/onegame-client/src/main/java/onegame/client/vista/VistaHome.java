package onegame.client.vista;

import java.util.concurrent.CompletableFuture;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.net.ConnectionMonitor;
import onegame.client.net.Utente;
import onegame.client.vista.accessori.GestoreCallbackBottoni;
import onegame.modello.giocatori.Statistica;

public class VistaHome extends Vista {

	private Button giocaOnlineBtn;
	private Button giocaOfflineBtn;
	private Button statisticheBtn;
	private Label titoloStatisticheLabel;
	private Label contenutoStatisticheLabel;
	private Button logoutBtn;
	private Label statoConnessioneLabel;
	private Button utenteBtn;

	public VistaHome(AppWithMaven app) {
		super(app);
		
		Label titolo = new Label("SCEGLI MODALITÀ");
		titolo.getStyleClass().add("titolo");

		Region spacer0 = new Region();
		spacer0.setPrefHeight(25);

		giocaOnlineBtn = new Button("Gioca con amici");
		giocaOnlineBtn.setPrefWidth(200);
		giocaOnlineBtn.setOnAction(e -> app.mostraVistaMenuOnline());

		giocaOfflineBtn = new Button("Gioca contro computer");
		giocaOfflineBtn.setPrefWidth(200);
		giocaOfflineBtn.setOnAction(e -> app.mostraVistaMenuOffline());

		Button tutorialBtn = new Button("Regolamento");
		tutorialBtn.setPrefWidth(200);
		tutorialBtn.setOnAction(e->app.mostraVistaTutorial());
		
		statisticheBtn = new Button("Mostra statistiche");
		statisticheBtn.setPrefWidth(200);
		titoloStatisticheLabel = new Label();
	    titoloStatisticheLabel.getStyleClass().add("titolo");
	    contenutoStatisticheLabel = new Label();

		logoutBtn = new Button("Logout");
		logoutBtn.setPrefWidth(200);
		logoutBtn.getStyleClass().add("logout");

		VBox centro = new VBox(15, titolo, spacer0, giocaOnlineBtn, giocaOfflineBtn, tutorialBtn, statisticheBtn, logoutBtn);
		centro.setAlignment(Pos.CENTER);
		centro.setPadding(new Insets(20));

		//bottom bar per stato connessione e utente loggato
		BorderPane bottomBar=new BorderPane();
		bottomBar.setPadding(new Insets(20));
		
		statoConnessioneLabel = new Label();
		bottomBar.setLeft(statoConnessioneLabel);
		
		utenteBtn = new Button();
		utenteBtn.setMaxWidth(200);
		utenteBtn.setEllipsisString("...");
		utenteBtn.setTextOverrun(OverrunStyle.ELLIPSIS);
		utenteBtn.setPadding(new Insets(5));
		utenteBtn.getStyleClass().add("logout");
		utenteBtn.setOnAction(e->{app.mostraVistaImpostazioni();});
		bottomBar.setRight(utenteBtn);

		//layout principale
	    BorderPane layout = new BorderPane();
	    layout.setCenter(centro);
	    layout.setBottom(bottomBar);

	    //root come StackPane per supportare overlay
	    StackPane root = new StackPane(layout);
	    scene = new Scene(root);

		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
		
	    //azione del bottone statistiche
	    statisticheBtn.addEventHandler(ActionEvent.ACTION, e -> {
	        StackPane overlay = new StackPane();
	        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
	        overlay.setPrefSize(root.getWidth(), root.getHeight());

	        BorderPane dialogBox = new BorderPane();
	        dialogBox.setPadding(new Insets(20));
	        dialogBox.setMaxSize(400, 300);
	        dialogBox.getStyleClass().add("modale");

	        titoloStatisticheLabel.setMaxWidth(Double.MAX_VALUE); // la Label occupa tutta la larghezza disponibile
	        titoloStatisticheLabel.setAlignment(Pos.CENTER);      // centra il contenuto della Label
	        titoloStatisticheLabel.setTextAlignment(TextAlignment.CENTER); 
			titoloStatisticheLabel.setEllipsisString("...");
			titoloStatisticheLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
	        BorderPane.setAlignment(titoloStatisticheLabel, Pos.TOP_CENTER);
	        dialogBox.setTop(titoloStatisticheLabel);
	        
	        BorderPane.setAlignment(contenutoStatisticheLabel, Pos.CENTER);
	        dialogBox.setCenter(contenutoStatisticheLabel);

	        Button chiudiBtn = new Button("Chiudi");
	        chiudiBtn.setOnAction(ev -> root.getChildren().remove(overlay));
	        BorderPane.setAlignment(chiudiBtn, Pos.BOTTOM_CENTER);
	        dialogBox.setBottom(chiudiBtn);

	        overlay.getChildren().add(dialogBox);

	        overlay.setOnMouseClicked(ev -> {
	            if (!dialogBox.getBoundsInParent().contains(ev.getX(), ev.getY())) {
	                root.getChildren().remove(overlay);
	            }
	        });

	        root.getChildren().add(overlay);
	    });
	}
	
	/**
	 * funzione da utilizzare nel callback di waitForStatisticheBtnClick
	 * per mostrare le statistiche di un utente loggato
	 * @param utente, stringa per titolo sezione statistiche
	 * @param statistica, statistica associata ad utente (con vari getter)
	 */
	public void compilaStatistiche(String utente, Statistica statistica) {
		titoloStatisticheLabel.setText("Statistiche di "+ utente);
		contenutoStatisticheLabel.setText("Partite giocate: "+statistica.getPartiteGiocate()+"\n"
				+ "Vittorie: "+statistica.getVittorie()+"\n"
				+ "Sconfitte: "+statistica.getSconfitte()+"\n"
				+ "Punti totali: "+statistica.getPuntiTotali()+"\n");
	}
	
	/**
	 * funzione asincrona per catturare click statistiche btn (usato nel controllore)
	 * 
	 * @return click sul bottone (qualora avvenisse)
	 */
	public CompletableFuture<Void> waitForStatisticheBtnClick() {
		return GestoreCallbackBottoni.waitForClick(statisticheBtn);
	}

	/**
	 * funzione asincrona per gestione click logout btn
	 * 
	 * @return click sul bottone (qualora avvenisse)
	 */
	public CompletableFuture<Void> waitForLogoutBtnClick() {
		return GestoreCallbackBottoni.waitForClick(logoutBtn);
	}

	/**
	 * metodo pubblico usato dal controlloreHome per passare alla vista successiva
	 */
	public void mostraAccesso() {
		app.mostraVistaAccesso();
	}

	/**
	 * metodo utilizzato per cambiare stato bottoni in base a:
	 * 1) se c'è connessione con il server--> param monitor
	 * 2) se si è entrati come utente anonimo--> param utente
	 */
	public void aggiungiListener(ConnectionMonitor monitor, Utente utente) {
		boolean logged = !utente.isAnonimo();

	    BooleanBinding abilitato = monitor.connectedProperty().and(Bindings.createBooleanBinding(() -> logged));
		giocaOnlineBtn.disableProperty().bind(monitor.connectedProperty().not());
		giocaOnlineBtn.opacityProperty().bind(Bindings.when(monitor.connectedProperty()).then(1.0).otherwise(0.5));

		statisticheBtn.disableProperty().bind(abilitato.not());
		statisticheBtn.opacityProperty().bind(Bindings.when(abilitato).then(1.0).otherwise(0.5));
				
		BooleanBinding loggatoBinding = (Bindings.createBooleanBinding(() -> logged));
		utenteBtn.disableProperty().bind(abilitato.not());
		utenteBtn.opacityProperty().bind(Bindings.when(abilitato).then(1.0).otherwise(0.25));
		utenteBtn.textProperty().bind(Bindings.when(loggatoBinding).then("⛯ "+utente.getUsername()).otherwise("guest-"+utente.getUsername()));
		
		statoConnessioneLabel.textProperty()
				.bind(Bindings.when(monitor.connectedProperty()).then("Connesso ✅").otherwise("Disconnesso ❌"));
	}
	
}
