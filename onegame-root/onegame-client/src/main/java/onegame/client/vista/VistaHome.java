package onegame.client.vista;

import java.util.concurrent.CompletableFuture;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.accessori.GestoreCallbackBottoni;
import onegame.modello.net.Utente;

public class VistaHome {
	private Scene scene;
	private AppWithMaven app;

	private Button giocaOnlineBtn;
	private Button giocaOfflineBtn;
	private Button statisticheBtn;
	private Button logoutBtn;
	private Label statoConnessioneLabel;

	public VistaHome(AppWithMaven app) {
		this.app = app;
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

		VBox centro = new VBox(15, titolo, spacer0, giocaOnlineBtn, giocaOfflineBtn, statisticheBtn, logoutBtn);
		centro.setAlignment(Pos.CENTER);
		centro.setPadding(new Insets(20));

		// label per stato connessione
		statoConnessioneLabel = new Label();
		statoConnessioneLabel.setPadding(new Insets(20));

		BorderPane root = new BorderPane();
		root.setCenter(centro);
		root.setBottom(statoConnessioneLabel);
		scene = new Scene(root);

		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());

	}

	public Scene getScene() {
		return scene;
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
		statoConnessioneLabel.textProperty()
				.bind(Bindings.when(monitor.connectedProperty()).then("Connesso ✅").otherwise("Disconnesso ❌"));
	}
	
}
