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

public class VistaRegistrazione {
	private AppWithMaven app;
	private Scene scene;
	private TextField usernameField;
	private PasswordField passwordField;
	private PasswordField confermaPasswordField;
	private Label erroreLabel;
	private Button registratiBtn;
	
	
	public VistaRegistrazione(AppWithMaven app) {
		this.app=app;
		// titolo
		Label titolo = new Label("Compila i campi");
		titolo.getStyleClass().add("titolo");

		// spaziatore
		Region spacer0 = new Region();
		spacer0.setPrefHeight(0);

		// campo nome utente
		usernameField = new TextField();
		usernameField.setPromptText("Scegli nome utente");
		usernameField.setMaxWidth(200);

		// campo password
		passwordField = new PasswordField();
		passwordField.setPromptText("Scegli password");
		passwordField.setMaxWidth(200);
		
		//campo conferma password
		confermaPasswordField = new PasswordField();
		confermaPasswordField.setPromptText("Reinserisci password scelta");
		confermaPasswordField.setMaxWidth(200);
		
		// campo errore
		erroreLabel = new Label();
		erroreLabel.getStyleClass().add("errore");
		erroreLabel.setVisible(false);
		erroreLabel.setManaged(false);
		
		// pulsanti registrati ed annulla
		Button annullaBtn = new Button("Annulla");
		annullaBtn.setOnAction(e->app.mostraVistaAccesso());
		annullaBtn.setPrefWidth(95);
		registratiBtn = new Button("Registrati");
		registratiBtn.setPrefWidth(95);
		HBox bottoni = new HBox(10, registratiBtn, annullaBtn);
		bottoni.setAlignment(Pos.CENTER);
		bottoni.setMaxWidth(200);
		
		// costruzione scena
		VBox root = new VBox(15, titolo, spacer0, usernameField, passwordField, confermaPasswordField, erroreLabel, bottoni);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));

		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
		Platform.runLater(() -> root.requestFocus());
	}
	
	public Scene getScene() {
		return scene;
	}
	
	public void visualizzaAccesso() {
		app.mostraVistaAccesso(usernameField.getText());
	}
	
	public void svuotaPassword() {
		passwordField.clear();
		confermaPasswordField.clear();
	}
	
	public void compilaMessaggioErrore(String messaggio) {
		erroreLabel.setText(messaggio);
		erroreLabel.setVisible(true);
		erroreLabel.setManaged(true);
	}
	
	/**
	 * sezione ottenimento dati da campo di registrazione
	 */
	@FunctionalInterface
	public interface DatiRegistrazioneCallback {
		void mandaDatiRegistrazione(String username, String password, String confermaPassword);
	}

	public void ottieniDati(DatiRegistrazioneCallback callback) {
		registratiBtn.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			String confermaPassword = confermaPasswordField.getText();
			callback.mandaDatiRegistrazione(username, password, confermaPassword);
		});

	}
	
	//fine sezione
}
