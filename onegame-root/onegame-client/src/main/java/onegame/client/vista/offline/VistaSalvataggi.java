package onegame.client.vista.offline;


import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.persistenza_temporanea.ManagerPersistenza;

public class VistaSalvataggi {

    private Scene scene;

    public VistaSalvataggi(AppWithMaven app) {
    	BorderPane root = new BorderPane();

    	Button indietroBtn = new Button("Indietro");
    	indietroBtn.setOnAction(e -> app.mostraVistaMenuOffline());

    	Label titolo = new Label("SALVATAGGI");
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

    	VBox lista = new VBox(10);
    	lista.setAlignment(Pos.TOP_CENTER);
    	lista.setMaxWidth(Region.USE_PREF_SIZE);

    	List<String> salvataggi = ManagerPersistenza.listaSalvataggi();

    	for (String s : salvataggi) {
    	    // Nome
    	    Label nome = new Label(s);
    	    nome.setMinWidth(200); 
    	    nome.setMaxWidth(200);
    	    nome.setEllipsisString("...");
    	    nome.setTextOverrun(OverrunStyle.ELLIPSIS);
    	    // Pulsanti
    	    Button giocaBtn = new Button("Gioca");
    	    giocaBtn.setOnAction(e -> app.mostraVistaGiocoCaricato(s));

    	    Button rinominaBtn = new Button("Rinomina");
    	    rinominaBtn.setOnAction(e -> {
    	        TextInputDialog dialog = new TextInputDialog(s);
    	        dialog.setTitle("Rinomina salvataggio");
    	        dialog.setHeaderText("Rinomina in:");
    	        Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
    	        okBtn.setDisable(true);
    	        dialog.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
    	            okBtn.setDisable(newValue.trim().isEmpty()
    	                || !ManagerPersistenza.verificaRinominaSalvataggio(s, newValue));
    	        });
    	        if (dialog.showAndWait().isPresent()) {
    	            ManagerPersistenza.rinominaSalvataggio(s, dialog.getResult());
    	            app.mostraVistaSalvataggi();
    	        }
    	    });

    	    Button eliminaBtn = new Button("Elimina");
    	    eliminaBtn.setOnAction(e -> {
    	        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	        alert.setTitle("Elimina salvataggio");
    	        alert.setHeaderText("Sei sicuro di voler eliminare?");
    	        alert.showAndWait().ifPresent(response -> {
    	            if (response == ButtonType.OK) {
    	                // Solo se l'utente ha premuto OK
    	                ManagerPersistenza.eliminaSalvataggio(s);
    	                app.mostraVistaSalvataggi();
    	            }
    	            //in tutti gli altri casi (CANCEL, chiusura finestra) non fa nulla
    	        });
    	    });

    	    // Riga con nome a sinistra e pulsanti a destra
    	    HBox riga = new HBox(20, nome, giocaBtn, rinominaBtn, eliminaBtn);
    	    riga.setAlignment(Pos.CENTER_LEFT);

    	    lista.getChildren().add(riga);
    	}

    	// --- Contenitore verticale per topBar e lista ---
    	VBox contenitore = new VBox(20); // spazio tra topBar e lista
    	contenitore.setAlignment(Pos.TOP_CENTER); // tutto in alto e centrato orizzontalmente
    	contenitore.getChildren().addAll(topBar, lista);

    	root.setCenter(contenitore);

    	scene = new Scene(root);
    	
    	scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    }

    public Scene getScene() {
        return scene;
    }
}
