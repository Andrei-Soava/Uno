package onegame.client.vista.online;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.Vista;
import onegame.client.vista.accessori.GestoreCallbackBottoni;

public class VistaStanza extends Vista{

    private Label numeroGiocatori;
    private Label nomeLbl;
    private Label codiceLbl;
    private final GridPane grigliaGiocatori;
    private Button abbandonaBtn;
    private final Button avviaBtn;

    public VistaStanza(AppWithMaven app, String codice) {
        super(app);
        BorderPane root = new BorderPane();

        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(10));

        abbandonaBtn = new Button("Abbandona");

        Label titolo = new Label("BENVENUTO NELLA STANZA");
        titolo.getStyleClass().add("titolo");
        titolo.setTranslateX(-40);
        
        numeroGiocatori = new Label();


        topBar.setLeft(abbandonaBtn);
        topBar.setCenter(titolo);
        topBar.setRight(numeroGiocatori);
        BorderPane.setAlignment(titolo, Pos.CENTER);

        root.setTop(topBar);

        //nome partita
        nomeLbl = new Label("prova");
        nomeLbl.setPadding(new Insets(10));
        nomeLbl.getStyleClass().add("titolo");
        BorderPane.setAlignment(nomeLbl, Pos.CENTER);

        
        //codice partita
        codiceLbl = new Label("Codice partita: " + "prova");
        codiceLbl.setPadding(new Insets(10));
        codiceLbl.getStyleClass().add("titolo");
        BorderPane.setAlignment(codiceLbl, Pos.CENTER);

        //griglia giocatori
        grigliaGiocatori = new GridPane();
        grigliaGiocatori.setHgap(20);
        grigliaGiocatori.setVgap(20);
        grigliaGiocatori.setAlignment(Pos.TOP_CENTER);
        grigliaGiocatori.setPadding(new Insets(20));
        VBox centro = new VBox(20, nomeLbl, codiceLbl, grigliaGiocatori);
        centro.setAlignment(Pos.TOP_CENTER);
        root.setCenter(centro);

        avviaBtn = new Button("Avvia Partita");
        avviaBtn.setVisible(false);
        //avviaBtn.setOnAction(e -> app.mostraVistaGiocoNuovo()); // da adattare
        HBox bottomBar = new HBox(avviaBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(20));

        root.setBottom(bottomBar);
        
        compilaCodicePartita(codice);
        
        scene = new Scene(root);
        scene.getStylesheets().add(
            getClass().getResource("/stile/base.css").toExternalForm()
        );
        
    }

    public void mostraMenuOnline() {
    	app.mostraVistaMenuOnline();
    }
    
    public CompletableFuture<Void> waitForAbbandonaBtnClick(){
    	return GestoreCallbackBottoni.waitForClick(abbandonaBtn);
    }
    
    public CompletableFuture<Void> waitForAvviaBtnClick(){
    	return GestoreCallbackBottoni.waitForClick(avviaBtn);
    }

    /**
     * metodo per aggiornare la lista dei giocatori connessi.
     */
    public void aggiornaGiocatori(List<String> nomiGiocatori, int maxGiocatori) {
    	numeroGiocatori.setText(nomiGiocatori.size()+"/"+maxGiocatori);
        grigliaGiocatori.getChildren().clear();
        int col = 0, row = 0;
        for (String nome : nomiGiocatori) {
            Label lbl = new Label(nome);
            lbl.getStyleClass().add("giocatore-box"); // stile CSS per rettangolino
            lbl.setPrefWidth(200);
            lbl.setMaxWidth(200);        
            lbl.setMinWidth(200);
            lbl.setEllipsisString("...");
			lbl.setTextOverrun(OverrunStyle.ELLIPSIS);
            lbl.setAlignment(Pos.CENTER);
            lbl.setPadding(new Insets(10));
            lbl.setStyle("-fx-border-color: white; -fx-background-color: red; -fx-background-radius: 5;"
            		+ " -fx-border-radius: 5; -fx-border-width: 2; -fx-font-size: 15px;");

            grigliaGiocatori.add(lbl, col, row);
            col++;
            if (col > 1) { // 2 colonne
                col = 0;
                row++;
            }
        }
    }
    
    public void compilaCodicePartita(String codice) {
    	codiceLbl.setText("Codice partita: "+codice);
    }
    
    public void compilaNomePartita(String nome) {
    	nomeLbl.setText(nome);
    }
    
    public void mostraAvviaBtn() {
    	avviaBtn.setVisible(true);
    }
    
    public void nascodiAvviaBtn() {
    	avviaBtn.setVisible(false);
    }
    
    public void disattivaAvviaBtn() {
    	avviaBtn.setDisable(true);
    	avviaBtn.setOpacity(0.5);
    }
    
    public void attivaAvviaBtn() {
    	avviaBtn.setDisable(false);
    	avviaBtn.setOpacity(1);
    }
    
    public void mostraHome() {
    	app.mostraVistaHome();
    }
    
    public void mostraGiocoOnline() {
    	app.mostraVistaGiocoOnline();
    }
    
}

