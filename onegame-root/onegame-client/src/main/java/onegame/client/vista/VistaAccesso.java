package onegame.client.vista;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.net.ClientSocket;

public class VistaAccesso {

	private Scene scene;
	AppWithMaven app;
	private TextField usernameField;
	private PasswordField passwordField;
	private Label erroreLabel;
	private Button accediBtn;
	private Button registratiBtn;
	private Button ospiteBtn;
	private Label statoConnessioneLabel;

	public VistaAccesso(AppWithMaven app) {
		this.app=app;
		// titolo
		Label titolo = new Label("Benvenuto su ONE! Scegli un opzione");
		titolo.getStyleClass().add("titolo");

		// spaziatore
		Region spacer0 = new Region();
		spacer0.setPrefHeight(25);

		// campo nome utente
		usernameField = new TextField();
		usernameField.setPromptText("Inserisci nome utente");
		usernameField.setMaxWidth(200);

		// campo password
		passwordField = new PasswordField();
		passwordField.setPromptText("Inserisci password");
		passwordField.setMaxWidth(200);

		// campo errore 
		erroreLabel = new Label();
		erroreLabel.getStyleClass().add("errore");
		erroreLabel.setVisible(false);
		erroreLabel.setManaged(false);
		
		// pulsanti "accedi" e "registrati"
		accediBtn = new Button("Accedi");
		registratiBtn = new Button("Registrati");
		registratiBtn.setOnAction(e->app.mostraVistaRegistrazione());
		HBox bottoni = new HBox(10, accediBtn, registratiBtn);
		bottoni.setAlignment(Pos.CENTER);
		bottoni.setMaxWidth(200);
		accediBtn.setPrefWidth(95);
		registratiBtn.setPrefWidth(95);

		// scritta "oppure"
		Label oppureLabel = new Label("oppure");

		// bottone "Entra come ospite"
		ospiteBtn = new Button("Entra come ospite");

		// costruzione scena centrale
		VBox centro = new VBox(15, titolo, spacer0, usernameField, passwordField, erroreLabel, bottoni, oppureLabel, ospiteBtn);
		centro.setAlignment(Pos.CENTER);
		centro.setPadding(new Insets(20));

		//label per stato connessione
		statoConnessioneLabel=new Label();
		statoConnessioneLabel.setPadding(new Insets(20));
		
		BorderPane root=new BorderPane();
		root.setCenter(centro);
		root.setBottom(statoConnessioneLabel);
		
		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
		Platform.runLater(() -> root.requestFocus());
	}

	public Scene getScene() {
		return scene;
	}

	public void mostraHome() {
		app.mostraVistaHome();
	}
	
	public void compilaUsername(String user) {
		this.usernameField.setText(user);
	}
	
	public void compilaMessaggioErrore(String messaggio) {
		erroreLabel.setText(messaggio);
		erroreLabel.setVisible(true);
		erroreLabel.setManaged(true);
	}
	
	public void svuotaCampi() {
		usernameField.clear();
		passwordField.clear();
	}
	
	public void compilaStatoConnessione(boolean connected) {
		Platform.runLater(() -> {
            statoConnessioneLabel.setText(connected ? "Connesso ✅" : "Disconnesso ❌");
        });
	}
	
	public void disableOnlineBtns() {
		accediBtn.setDisable(true);
		accediBtn.setOpacity(0.5);
		registratiBtn.setDisable(true);
		registratiBtn.setOpacity(0.5);
	}
	
	public void enableOnlineBtns() {
		accediBtn.setDisable(false);
		accediBtn.setOpacity(1);
		registratiBtn.setDisable(false);
		registratiBtn.setOpacity(1);
	}
	/**
	 * sezione ottenimento dati da campo di login
	 */
	@FunctionalInterface
	public interface DatiAccessoCallback {
		void mandaDatiAccesso(String username, String password);
	}

	public void ottieniDati(DatiAccessoCallback callback) {
		accediBtn.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			callback.mandaDatiAccesso(username, password);
		});
		
		ospiteBtn.setOnAction(e -> {
			callback.mandaDatiAccesso(null, null);
		});
	}
	
	//fine sezione
}
