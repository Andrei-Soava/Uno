package onegame.client.vista;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;

public class VistaAccesso {

	private Scene scene;
	AppWithMaven app;
	private TextField usernameField;
	private PasswordField passwordField;
	private Label erroreLabel;
	private Button accediBtn;
	private Button registratiBtn;
	private Button ospiteBtn;

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

		// costruzione scena
		VBox root = new VBox(15, titolo, spacer0, usernameField, passwordField, erroreLabel, bottoni, oppureLabel, ospiteBtn);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));

		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
		Platform.runLater(() -> root.requestFocus());
	}

	public Scene getScene() {
		return scene;
	}

	public void svuotaCampi() {
		usernameField.clear();
		passwordField.clear();
	}
	
	public void visualizzaHome() {
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
