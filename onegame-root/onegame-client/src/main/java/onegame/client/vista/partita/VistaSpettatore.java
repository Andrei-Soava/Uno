package onegame.client.vista.partita;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreGraficaCarta;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.Colore;

public class VistaSpettatore extends VistaPartita {

	private Scene scene;
	private AppWithMaven app;
	private Label turnoCorrenteLbl;
	private Label numeroCarteLbl;
	
    private Label prossimoTurnoLbl;
    private HBox timerBox;
    
    private StackPane cartaCorrente;
    private Label logAreaLbl;
	
	public VistaSpettatore(AppWithMaven app) {
		super(app);
		//this.app=app;
		BorderPane root = new BorderPane();
    	root.setPadding(new Insets(10));
    	
    	//---------------------------------------------------------------------------------
    	//barra superiore con pulsante Home & label per indicare il turno
    	//bottone per abbandonare
    	Button abbandonaBtn = new Button("Abbandona");
    	abbandonaBtn.getStyleClass().add("logout");
    	abbandonaBtn.setOnAction(e -> {
    	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	    alert.setTitle("Conferma");
    	    alert.setHeaderText("Vuoi davvero tornare alla Home?");
    	    alert.setContentText("Eventuali progressi non salvati andranno persi.");

    	    ButtonType confermaBtn = new ButtonType("Conferma", ButtonBar.ButtonData.OK_DONE);
    	    ButtonType annullaBtn = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
    	    alert.getButtonTypes().setAll(confermaBtn, annullaBtn);

    	    alert.showAndWait().ifPresent(response -> {
    	        if (response == confermaBtn) {
    	        	//cg.interrompiPartita();
    	            app.mostraVistaMenuOffline();
    	        }
    	    });
    	});
    	//label con turno (aggiornato nel controllore)
    	turnoCorrenteLbl=new Label();
    	turnoCorrenteLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    	//spaziatori per centrare label turno corrente
    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(50);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);
    	
    	//hbox che contiene tutti gli elementi che staranno in alto
    	HBox contenitoreSuperiore = new HBox(10);
    	contenitoreSuperiore.setPadding(new Insets(10));
    	contenitoreSuperiore.setAlignment(Pos.CENTER_LEFT);
    	//contenitoreSuperiore.setStyle("-fx-border-color:black;");
    	contenitoreSuperiore.getChildren().addAll(abbandonaBtn, leftSpacer, turnoCorrenteLbl, rightSpacer);
    	//imposto il contenitore superiore in alto
    	root.setTop(contenitoreSuperiore);
    	
    	//---------------------------------------------------------------------------------
    	//zona pescaggio (centrale a sinistra)
    	VBox sottoContenitoreSinistra = new VBox(10);
    	Region spacerTop = new Region();
    	spacerTop.setPrefHeight(50);
    	//sottoContenitoreSinistra.setStyle("-fx-border-color:black;");
    	sottoContenitoreSinistra.setAlignment(Pos.TOP_CENTER);
    	StackPane sp=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,null));
    	sottoContenitoreSinistra.getChildren().addAll(spacerTop, sp);
    	
    	//
    	//zona carta corrente (centrale al centro)
    	VBox sottoContenitoreCentrale = new VBox(10);
    	//sottoContenitoreCentrale.setStyle("-fx-border-color:black;");
    	sottoContenitoreCentrale.setAlignment(Pos.TOP_CENTER);
    	logAreaLbl = new Label(); // manteniamo il nome logArea
    	logAreaLbl.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5;");
    	logAreaLbl.setVisible(false);
    	Region spacer0 = new Region();
    	spacer0.setPrefHeight(25);
    	//VBox.setVgrow(spacer0, Priority.ALWAYS);
    	Label cartaCorrenteLbl = new Label("Carta sul tavolo");
    	cartaCorrenteLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    	BorderPane.setAlignment(cartaCorrenteLbl, Pos.CENTER);
    	cartaCorrente=new StackPane();
    	sottoContenitoreCentrale.getChildren().addAll(logAreaLbl,spacer0,cartaCorrenteLbl, cartaCorrente);
    	
    	//
    	//zona prossimo turno e futuro pulsante ONE (centrale a destra)
    	VBox sottoContenitoreDestra = new VBox(10);
    	sottoContenitoreDestra.setPadding(new Insets(0));
    	//sottoContenitoreDestra.setStyle("-fx-border-color:black;");
    	prossimoTurnoLbl=new Label();
    	prossimoTurnoLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    	timerBox=new HBox();
    	Region spacer = new Region();
    	VBox.setVgrow(spacer, Priority.ALWAYS);
    	sottoContenitoreDestra.getChildren().addAll(prossimoTurnoLbl,timerBox);
    	
    	//
    	//contenitore di tutti i sotto contenitori centrali
    	GridPane contenitoreCentrale = new GridPane();
    	//contenitoreCentrale.setStyle("-fx-border-color:black;");
    	contenitoreCentrale.setPadding(new Insets(10));
    	contenitoreCentrale.setAlignment(Pos.CENTER);
    	contenitoreCentrale.add(sottoContenitoreSinistra, 0, 0);
    	contenitoreCentrale.add(sottoContenitoreCentrale, 1, 0);
    	contenitoreCentrale.add(sottoContenitoreDestra, 2, 0);
    	//vincoli su quanto sono larghe le tabelle
    	ColumnConstraints cc1 = new ColumnConstraints();
    	cc1.setPercentWidth((150.0/900)*100);
    	ColumnConstraints cc2 = new ColumnConstraints();
    	cc2.setPercentWidth((600.0/900)*100);
    	ColumnConstraints cc3 = new ColumnConstraints();
    	cc3.setPercentWidth((150.0/900)*100);
    	contenitoreCentrale.getColumnConstraints().addAll(cc1, cc2, cc3);
    	
    	//vincolo di altezza per la riga 0
    	RowConstraints rc = new RowConstraints();
    	rc.setVgrow(Priority.ALWAYS);     // fa crescere la riga
    	rc.setPercentHeight(100);         // occupa il 100% dell'altezza disponibile
    	contenitoreCentrale.getRowConstraints().add(rc);
    	//dico ai figli di crescere verticalmente
    	GridPane.setVgrow(sottoContenitoreSinistra, Priority.ALWAYS);
    	GridPane.setVgrow(sottoContenitoreCentrale, Priority.ALWAYS);
    	GridPane.setVgrow(sottoContenitoreDestra, Priority.ALWAYS);
    	// (opzionale) se i figli sono VBox/HBox/Pane, abilita anche questo:
    	sottoContenitoreSinistra.setMaxHeight(Double.MAX_VALUE);
    	sottoContenitoreCentrale.setMaxHeight(Double.MAX_VALUE);
    	sottoContenitoreDestra.setMaxHeight(Double.MAX_VALUE);
    	
    	
    	//imposto il contenitore centrale al centro
    	root.setCenter(contenitoreCentrale);
    	
    	//---------------------------------------------------------------------------------
    	//contenitore inferiore
    	HBox contenitoreInferiore = new HBox(10);
    	contenitoreInferiore.setAlignment(Pos.CENTER);
    	numeroCarteLbl=new Label();
    	numeroCarteLbl.setTextAlignment(TextAlignment.CENTER);
    	numeroCarteLbl.setAlignment(Pos.CENTER);
    	numeroCarteLbl.getStyleClass().add("titolo");
    	contenitoreInferiore.getChildren().add(numeroCarteLbl);
    	//contenitoreInferiore.setMinHeight(200);
        //imposto il contenitore inferiore sotto
        root.setBottom(contenitoreInferiore);
        
        scene = new Scene(root, app.getPrimaryStage().getWidth(), app.getPrimaryStage().getHeight());
    	scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
	}
	
	@Override
	public Scene getScene() {
		return scene;
	}
	
	public void impostaTurnoSpettatore(String giocatore, int numeroCarte, Carta cartaCorrente) {
		Platform.runLater(()->{
			turnoCorrenteLbl.setText("Turno di "+giocatore.toUpperCase());
			this.cartaCorrente.getChildren().add(GestoreGraficaCarta.creaVistaCarta(cartaCorrente));
			numeroCarteLbl.setText("Numero carte in mano: "+numeroCarte);
		});
	}


	//TODO stampaMessaggi e stampaFinePartita 
}
