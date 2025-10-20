package onegame.client.vista.online;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import onegame.client.esecuzione.AppWithMaven;

public class VistaConfigurazioneOnline {

    private Scene scene;
    private AppWithMaven app;
    private ComboBox<Integer> numGiocatori;
    private TextField nomeStanza;
    private Button creaBtn;
    
    public VistaConfigurazioneOnline(AppWithMaven app) {
    	this.app=app;
    	
    	BorderPane root = new BorderPane();

    	Button indietroBtn = new Button("Indietro");
    	indietroBtn.setOnAction(e -> app.mostraVistaMenuOnline());

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
    	
    	Label lblNomeStanza = new Label("Nome Stanza");
    	nomeStanza = new TextField();
    	nomeStanza.setMaxWidth(200);
    	nomeStanza.setPromptText("Inserisci il nome della partita");
    	
    	Label lblNumGiocatori = new Label("Numero Giocatori");
    	numGiocatori = new ComboBox<>();
    	numGiocatori.getItems().addAll(2, 3, 4);
    	numGiocatori.setValue(2);

    	//possibile scelta modalità alternative QUI
    	
    	Button annullaBtn = new Button("Annulla");
    	annullaBtn.setOnAction(e -> app.mostraVistaMenuOnline());

    	creaBtn = new Button("Crea stanza");

    	HBox pulsanti = new HBox(10, annullaBtn, creaBtn);
    	pulsanti.setAlignment(Pos.CENTER);

    	VBox centro = new VBox(20, lblNomeStanza, nomeStanza, lblNumGiocatori, numGiocatori, pulsanti);
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
    
    @FunctionalInterface
    public interface ConfiguraPartitaCallback {
        void onConfigura(String nomePartita, int numGiocatori);
    }
    
    public void configuraPartita(ConfiguraPartitaCallback callback) {
    	creaBtn.setOnAction(e->{
    		// Recupero valori
    		String nomePartita = nomeStanza.getText();
    		if (nomePartita == null || nomePartita.isBlank()) {
    			nomePartita = "Partita senza nome"; // default
    		}
    		
    		int giocatori = numGiocatori.getValue();
    		
    		// Invoco la callback
    		callback.onConfigura(nomePartita, giocatori);
    	});
    }
    
    public void mostraHome() {
    	app.mostraVistaHome();
    }
    
    public void mostraStanza(String codiceStanza) {
    	app.mostraVistaStanza(codiceStanza, true);
    }
}
