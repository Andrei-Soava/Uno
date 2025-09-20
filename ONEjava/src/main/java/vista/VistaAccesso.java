package vista;

import esecuzione.AppWithMaven;
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

public class VistaAccesso {

	private Scene scene;
	AppWithMaven app;
	private TextField usernameField;
	private PasswordField passwordField;
	private Button accediBtn;
	private Button registratiBtn;

	public VistaAccesso(AppWithMaven app) {
		this.app=app;
		// titolo
		Label titolo = new Label("Benvenuto su ONE! Scegli un opzione");
		titolo.getStyleClass().add("titolo");

		// spaziatore
		Region spacer0 = new Region();
		spacer0.setPrefHeight(50);

		// campo nome utente
		usernameField = new TextField();
		usernameField.setPromptText("Inserisci nome utente");
		usernameField.setMaxWidth(200);

		// campo password
		passwordField = new PasswordField();
		passwordField.setPromptText("Inserisci password");
		passwordField.setMaxWidth(200);

		// pulsanti "accedi" e "registrati"
		accediBtn = new Button("Accedi");
		registratiBtn = new Button("Registrati");
		HBox bottoni = new HBox(10, accediBtn, registratiBtn);
		bottoni.setAlignment(Pos.CENTER);
		bottoni.setMaxWidth(200);
		accediBtn.setPrefWidth(95);
		registratiBtn.setPrefWidth(95);

		// scritta "oppure"
		Label oppureLabel = new Label("oppure");

		// bottone "Entra come ospite"
		Button ospiteBtn = new Button("Entra come ospite");
		ospiteBtn.setOnAction(e -> {
			app.mostraVistaHomeOspite();
			});

		// costruzione scena
		VBox root = new VBox(15, titolo, spacer0, usernameField, passwordField, bottoni, oppureLabel, ospiteBtn);
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
	
	@FunctionalInterface
	public interface DatiLoginCallback {
		void mandaDatiLogin(String username, String password);
	}

	/**
	 * Metodo asincrono che restituisce username e password nel callback.
	 */
	public void ottieniDati(DatiLoginCallback callback) {
		accediBtn.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			callback.mandaDatiLogin(username, password);
		});

	}
}
