package onegame.client.vista.offline;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import onegame.client.esecuzione.AppWithMaven;

public class VistaConfigurazioneOffline {

    private Scene scene;

    public VistaConfigurazioneOffline(AppWithMaven app) {
    	BorderPane root = new BorderPane();

    	Button indietroBtn = new Button("Indietro");
    	indietroBtn.setOnAction(e -> app.mostraVistaMenuOffline());

    	Label titolo = new Label("CONFIGURA NUOVA PARTITA");
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

    	Label lblNumGiocatori = new Label("Numero Giocatori");
    	ComboBox<Integer> numGiocatori = new ComboBox<>();
    	numGiocatori.getItems().addAll(2, 3, 4);
    	numGiocatori.setValue(2);

    	//possibile scelta modalità alternative QUI
    	
    	Button annullaBtn = new Button("Annulla");
    	annullaBtn.setOnAction(e -> app.mostraVistaMenuOffline());

    	Button avviaBtn = new Button("Avvia Partita");
    	avviaBtn.setOnAction(e -> app.mostraVistaGiocoNuovo(numGiocatori.getValue()));

    	HBox pulsanti = new HBox(10, annullaBtn, avviaBtn);
    	pulsanti.setAlignment(Pos.CENTER);

    	VBox centro = new VBox(20, lblNumGiocatori, numGiocatori, pulsanti);
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
    
}
