package onegame.client.vista;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.net.ConnectionMonitor;

public class VistaAccesso extends Vista{

	private TextField usernameField;
	private PasswordField passwordField;
	private Label erroreLabel;
	private Button accediBtn;
	private Button registratiBtn;
	private Button ospiteBtn;
	private Label statoConnessioneLabel;

	public VistaAccesso(AppWithMaven app, String username) {
		super(app);
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
		compilaUsername(username);
		
		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
		Platform.runLater(() -> root.requestFocus());
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
	
    /**
     * classe ausiliaria per gestione callback nickname ospite dentro controllore
     */
    public static class ContestoNicknameOspite {
        private final Dialog<?> dialog;
        private final String nickname;
        private final Label erroreLbl;

        private ContestoNicknameOspite(Dialog<?> dialog, String nuovoNome, Label erroreLbl) {
            this.dialog = dialog;
            this.nickname = nuovoNome;
            this.erroreLbl = erroreLbl;
        }

        public Dialog<?> getDialog() { return dialog; }
        public String getNickname() { return nickname; }
        public Label getErroreLbl() { return erroreLbl; }
    }

    /**
     * metodo asincrono che mostra una dialog di scelta nickname 
     * SENZA però gestire gli effetti della sua chiusura
     * 
     * @param onConferma, callback contenente elementi per gestione scelta nickname
     * @param onClose, runnable utilizzato da controllore durante la chiusura della dialog 
     */
    public void mostraDialogNicknameOspite(Consumer<ContestoNicknameOspite> onConferma, Runnable onClose) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Scegli nickname");

        Label lbl = new Label("Nickname:");
        Label erroreLbl = new Label();
        erroreLbl.setStyle("-fx-text-fill: red;");

        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Scegli un nickname");

        VBox content = new VBox(10, lbl, nicknameField, erroreLbl);
        dialog.getDialogPane().setContent(content);

        ButtonType confermaBtn = new ButtonType("Conferma", ButtonData.OK_DONE);
        ButtonType annullaBtn = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaBtn, annullaBtn);
        Button conferma = (Button) dialog.getDialogPane().lookupButton(confermaBtn);
        conferma.addEventFilter(ActionEvent.ACTION, ev -> {
            String nuovoNome = nicknameField.getText().trim();
            ContestoNicknameOspite ctx = new ContestoNicknameOspite(dialog, nuovoNome, erroreLbl);
            onConferma.accept(ctx);
            ev.consume();
        });
        
        dialog.setOnHidden(e -> {
            if (onClose != null) onClose.run();
        });
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
        dialog.show();
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
	
	/**
	 * metodo che attiva/disattiva i bottoni che funzionano solo con connessione
	 * @param monitor oggetto che verifica se c'è connessione con il server
	 * 	--> in base a questo modifica la disponibilità dei bottoni con binding
	 */
	public void aggiungiListener(ConnectionMonitor monitor) {
	    accediBtn.disableProperty().bind(monitor.connectedProperty().not());
	    accediBtn.opacityProperty().bind(
	        Bindings.when(monitor.connectedProperty())
	                .then(1.0)
	                .otherwise(0.5)
	    );
	    
	    registratiBtn.disableProperty().bind(monitor.connectedProperty().not());
	    registratiBtn.opacityProperty().bind(
	        Bindings.when(monitor.connectedProperty())
	                .then(1.0)
	                .otherwise(0.5)
	    );
	    statoConnessioneLabel.textProperty().bind(
	        Bindings.when(monitor.connectedProperty())
	                .then("Connesso ✅")
	                .otherwise("Disconnesso ❌")
	    );
	}

}
