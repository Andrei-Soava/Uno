package vista;

import esecuzione.AppWithMaven;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class VistaConfigurazione {

    private Scene scene;

    public VistaConfigurazione(AppWithMaven app) {

    	BorderPane root = new BorderPane();

    	Button btnHome = new Button("← Home");
    	btnHome.setOnAction(e -> app.mostraVistaIniziale());

    	Label titolo = new Label("CONFIGURA NUOVA PARTITA");

    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(50);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);

    	HBox topBar = new HBox(10);
    	topBar.setPadding(new Insets(10));
    	topBar.setAlignment(Pos.CENTER_LEFT);
    	topBar.getChildren().addAll(btnHome, leftSpacer, titolo, rightSpacer);

    	root.setTop(topBar);

    	Label lblNumGiocatori = new Label("Numero Giocatori");

    	ComboBox<Integer> numGiocatori = new ComboBox<>();
    	numGiocatori.getItems().addAll(2, 3, 4);
    	numGiocatori.setValue(2);

    	Button btnAnnulla = new Button("Annulla");
    	btnAnnulla.setOnAction(e -> app.mostraVistaIniziale());

    	Button btnAvvia = new Button("Avvia Partita");
    	btnAvvia.setOnAction(e -> app.mostraVistaGiocoNuovo(numGiocatori.getValue()));

    	HBox pulsanti = new HBox(10, btnAnnulla, btnAvvia);
    	pulsanti.setAlignment(Pos.CENTER);

    	VBox centro = new VBox(20, lblNumGiocatori, numGiocatori, pulsanti);
    	centro.setAlignment(Pos.CENTER);

    	root.setCenter(centro);

        scene = new Scene(root);
    }

    public Scene getScene() {
        return scene;
    }
}
