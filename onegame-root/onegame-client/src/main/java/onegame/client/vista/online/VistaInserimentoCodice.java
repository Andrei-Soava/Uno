package onegame.client.vista.online;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.Vista;


public class VistaInserimentoCodice extends Vista {
	private TextField codiceField;
	private Label erroreLabel;
	private Button entraBtn;
	
	public VistaInserimentoCodice(AppWithMaven app) {
		super(app);
		BorderPane root=new BorderPane();
		
		Button indietroBtn = new Button("Indietro");
    	indietroBtn.setOnAction(e -> app.mostraVistaMenuOnline());

    	Label titolo = new Label("Unisciti a partita esistente");
    	titolo.getStyleClass().add("titolo");

    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(50);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);

    	HBox topBar = new HBox(10);
    	topBar.setPadding(new Insets(10));
    	topBar.setAlignment(Pos.CENTER_LEFT);
    	topBar.getChildren().addAll(indietroBtn, leftSpacer, titolo, rightSpacer);

    	root.setTop(topBar);

		// spaziatore
		Region spacer0 = new Region();
		spacer0.setPrefHeight(0);

		// campo 
		codiceField = new TextField();
		codiceField.setPromptText("Inserisci codice partita");
		codiceField.setMaxWidth(200);

		// campo errore 
		erroreLabel = new Label();
		erroreLabel.getStyleClass().add("errore");
		erroreLabel.setVisible(false);
		erroreLabel.setManaged(false);
		
		// pulsanti "entra" e "annulla"
		entraBtn = new Button("Entra");
		Button annullaBtn=new Button("Annulla");
		annullaBtn.setOnAction(e->app.mostraVistaMenuOnline());
		
		HBox bottoni = new HBox(10, entraBtn, annullaBtn);
		bottoni.setAlignment(Pos.CENTER);
		bottoni.setMaxWidth(200);
		entraBtn.setPrefWidth(95);
		annullaBtn.setPrefWidth(95);

		// costruzione scena
		VBox centro = new VBox(15, codiceField, erroreLabel, bottoni);
		centro.setAlignment(Pos.CENTER);
		centro.setPadding(new Insets(20));
		
		root.setCenter(centro);

		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
		Platform.runLater(() -> root.requestFocus());
	}

	public void svuotaCampoCodice() {
		codiceField.clear();
	}
	
	public void mostraStanza(String codice) {
		app.mostraVistaStanza(codice);
	}
	
	public void mostraHome() {
		app.mostraVistaHome();
	}
	
	public void compilaMessaggioErrore(String messaggio) {
		erroreLabel.setText(messaggio);
		erroreLabel.setVisible(true);
		erroreLabel.setManaged(true);
	}
	
	/**
	 * sezione ottenimento codice inserito
	 */
	@FunctionalInterface
	public interface CodiceCallback {
		void mandaCodice(String codice);
	}

	public void ottieniCodice(CodiceCallback callback) {
		entraBtn.setOnAction(e -> {
			String codice = codiceField.getText();
			callback.mandaCodice(codice);
		});

	}
	
	//fine sezione


}
