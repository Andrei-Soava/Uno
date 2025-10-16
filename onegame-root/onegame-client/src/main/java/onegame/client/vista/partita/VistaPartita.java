package onegame.client.vista.partita;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import onegame.client.controllore.offline.ControlloreGioco;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreGraficaCarta;
import onegame.client.vista.accessori.LayoutGiocatori;
import onegame.modello.util.Wrappers.IntegerAndBooleanWrapper;
import onegame.modello.util.Wrappers.StringWrapper;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.Colore;

public abstract class VistaPartita {
	protected Scene scene;
	protected AppWithMaven app;
	protected BorderPane root;
	protected Label turnoCorrenteLbl;
	protected Label prossimoTurnoLbl;
	protected ArrayList<Label> giocatoriLbl = new ArrayList<>();
	protected BorderPane centroPane;
    protected HBox timerBox;
    protected Label logAreaLbl;
    protected StackPane cartaCorrente;
    protected Button ONEBtn;
    protected Button pescaBtn;
    public ControlloreGioco cg;

    
	protected VistaPartita(AppWithMaven app) {
		this.app=app;
		root=new BorderPane();
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
    	        	cg.interrompiPartita();
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
    	timerBox=new HBox();
    	//hbox che contiene tutti gli elementi che staranno in alto
    	BorderPane contenitoreSuperiore = new BorderPane();
    	contenitoreSuperiore.setPadding(new Insets(10));
    	//contenitoreSuperiore.setAlignment(Pos.CENTER_LEFT);
    	contenitoreSuperiore.setLeft(abbandonaBtn);
    	contenitoreSuperiore.setCenter(turnoCorrenteLbl);
    	BorderPane.setAlignment(turnoCorrenteLbl, Pos.CENTER);
    	turnoCorrenteLbl.setTranslateX(0);
    	contenitoreSuperiore.setRight(timerBox);
    	BorderPane.setAlignment(timerBox, Pos.CENTER_RIGHT);
    	//contenitoreSuperiore.setStyle("-fx-border-color:black;");
    	//contenitoreSuperiore.getChildren().addAll(abbandonaBtn, leftSpacer, turnoCorrenteLbl, rightSpacer, timerBox);
    	//imposto il contenitore superiore in alto
    	root.setTop(contenitoreSuperiore);
    	
    	//---------------------------------------------------------------------------------    	
    	//center del root
    	
    	//gridPane centrale
        GridPane grid = new GridPane();

        //incoli proporzionali: 5 colonne, 4 righe
        for (int i = 0; i < 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 5);
            grid.getColumnConstraints().add(col);
        }
        for (int i = 0; i < 4; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 4);
            grid.getRowConstraints().add(row);
        }

        //riga 1: celle A1–E1--> giocatoriLbl da 0 a 4
        for (int col = 0; col < 5; col++) {
            Label p = new Label("Cella "+(0+col));
            p.getStyleClass().add("contenitore");
            p.setVisible(false);
            p.setPadding(new Insets(5));
            giocatoriLbl.add(p);
            GridPane.setHalignment(p, HPos.CENTER);
            GridPane.setValignment(p, VPos.CENTER);
            GridPane.setMargin(p, new Insets(0, 20, 0, 20));
            grid.add(p, col, 0);
        }

        //colonna A (righe 2–4)--> giocatoriLbl da 5 a 7
        for (int row = 1; row < 4; row++) {
        	Label p = new Label("Cella "+(4+row));
        	p.getStyleClass().add("contenitore");
        	p.setVisible(false);
        	p.setPadding(new Insets(5));
        	giocatoriLbl.add(p);
        	GridPane.setHalignment(p, HPos.CENTER);
            GridPane.setValignment(p, VPos.CENTER);
        	GridPane.setMargin(p, new Insets(0, 20, 0, 20));
            grid.add(p, 0, row);
        }

        //colonna E (righe 2–4)--> giocatoriLbl da 8 a 10
        for (int row = 1; row < 4; row++) {
            Label p = new Label("Cella "+(7+row));
            p.getStyleClass().add("contenitore");
            p.setVisible(false);
            p.setPadding(new Insets(5));
            giocatoriLbl.add(p);
            GridPane.setHalignment(p, HPos.CENTER);
            GridPane.setValignment(p, VPos.CENTER);
            GridPane.setMargin(p, new Insets(0, 20, 0, 20));
            grid.add(p, 4, row);
        }

        //BLOCCO CENTRALE UNICO
        centroPane = new BorderPane();

        //centro sopra
        logAreaLbl = new Label(); // manteniamo il nome logArea
    	logAreaLbl.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5;");
    	logAreaLbl.setVisible(false);
        StackPane topWrapper = new StackPane(logAreaLbl);
        topWrapper.setPadding(new Insets(10)); // padding interno
        centroPane.setTop(topWrapper);
        //centralPane.setTop(logAreaLbl);
        BorderPane.setAlignment(logAreaLbl, Pos.CENTER); // centrato in alto

        //CENTRO DEL PANE 3x3
        
        //centro sinistra
    	VBox sottoContenitoreSinistra = new VBox(10);
    	Region spacerTop = new Region();
    	spacerTop.setPrefHeight(50);
    	//sottoContenitoreSinistra.setStyle("-fx-border-color:black;");
    	sottoContenitoreSinistra.setAlignment(Pos.TOP_CENTER);
    	StackPane sp=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,null));
    	pescaBtn = new Button("Pesca");
    	Label mazzoLbl = new Label("Mazzo");
    	mazzoLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    	sottoContenitoreSinistra.getChildren().addAll(mazzoLbl, sp, pescaBtn);

    	//centro destra
        VBox sottoContenitoreDestra = new VBox(10);
        Label cartaCorrenteLbl = new Label("Sul tavolo");
    	cartaCorrenteLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    	BorderPane.setAlignment(cartaCorrenteLbl, Pos.CENTER);
    	cartaCorrente=new StackPane();
        sottoContenitoreDestra.getChildren().addAll(cartaCorrenteLbl, cartaCorrente);

        //centro UNITO
        HBox centerBox = new HBox(20, sottoContenitoreSinistra, sottoContenitoreDestra); // spaziatura orizzontale
        centerBox.setAlignment(Pos.CENTER); // tutto centrato
        centroPane.setCenter(centerBox);

        //centro sotto
        ONEBtn=new Button("ONE!");
    	ONEBtn.setVisible(false);
        centroPane.setBottom(ONEBtn);
        BorderPane.setAlignment(ONEBtn, Pos.CENTER_RIGHT);
     	BorderPane.setMargin(ONEBtn, new Insets(40, 40, 40, 40));
     	
     	
        grid.add(centroPane, 1, 1, 3, 3); // col=1 (B), row=1 (riga2), span 3x3
        //grid.setGridLinesVisible(true);
    	
    	//imposto il grid al centro
    	root.setCenter(grid);
    }
	
	/**
	 * metodo richiamato ogni volta che deve essere mostrata questa vista
	 */
	public void mostraVista() {
		app.aggiornaVistaPartita(this);
	}
	
	
	public Scene getScene() {
		return scene;
	}
	
	//---------------------------------------------------------------------------------
    /**
     * sezione stampa testo/carte/
     * 
     */
	/**
	 * metodo che stampa un messaggio nella logAreaLbl con fade-in/fade-out
	 * 
	 * @param s, stringa da stampare
	 */
    public void stampaMessaggio(String s) {
        Platform.runLater(() -> {
            logAreaLbl.setText(s);
            logAreaLbl.setOpacity(0); // inizia trasparente
            logAreaLbl.setVisible(true);

            //fade-in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), logAreaLbl);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            //pausa visibile
            PauseTransition stayOn = new PauseTransition(Duration.seconds(2));

            //fade-out
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), logAreaLbl);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> logAreaLbl.setVisible(false));

            //sequenza: fade-in → pausa → fade-out
            SequentialTransition seq = new SequentialTransition(fadeIn, stayOn, fadeOut);
            seq.play();
        });
    }

    
    protected void stampaCartaCorrente(Carta cartaCorrente) {
    	this.cartaCorrente.getChildren().add(GestoreGraficaCarta.creaVistaCarta(cartaCorrente));
    }

    /**
     * metodo che fa brillare la turnoCorrenteLbl
     */
    public void evidenziaTurnoCorrente() {
    	Platform.runLater(()->{
    		makeGlowingPulse(turnoCorrenteLbl);
    	});
    }
    
    /**
     * metodo che stampa il turno in alto
     * @param giocatore
     */
    public void stampaTurnoCorrente(String giocatore) {
        Platform.runLater(() -> {
        	turnoCorrenteLbl.setEffect(null);
        	turnoCorrenteLbl.setText("Turno di "+giocatore.toUpperCase());
        });
    }
    
    public static void makeGlowingPulse(Label label) {
        DropShadow glow = new DropShadow();
        Color color=Color.RED;
        glow.setColor(color);
        glow.setRadius(30);
        glow.setSpread(0.8);
        label.setEffect(glow);

        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(glow.radiusProperty(), 10),
                new KeyValue(glow.colorProperty(), color.deriveColor(1, 1, 1, 0.3))
            ),
            new KeyFrame(Duration.seconds(1),
                new KeyValue(glow.radiusProperty(), 40),
                new KeyValue(glow.colorProperty(), color.deriveColor(1, 1, 1, 1.0))
            )
        );

        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }

    
    /**
     * metodo che imposta l'immagine che indica la direzione (orario/antiorario)
     * @param direzione, true=orario, false=antiorario
     */
    private void stampaDirezione(boolean direzione) {
    	String percorso="/immagini/";
    	if(direzione)
    		percorso+="orario.png";
    	else
    		percorso+="antiorario.png";	
    	Image immagineDirezione = new Image(VistaPartita.class.getResourceAsStream(percorso)); // metti il path corretto
     	BackgroundImage immagineSfondo = new BackgroundImage(
     	    immagineDirezione,
     	    BackgroundRepeat.NO_REPEAT,
     	    BackgroundRepeat.NO_REPEAT,
     	    BackgroundPosition.CENTER,
     	    new BackgroundSize(350, 350, false, false, false, false)
     	);
     	centroPane.setBackground(new Background(immagineSfondo));
    }
    
    /**
     * metodo che dispone i giocatori nell'ordine prestabilito in LayoutGiocatori
     * + richiama metodo stampa della ruota della direzione
     * + elimina eventuali effettui grafici residui sulle lbl di giocatoriLbl
     * 
     * @param turnazione mappa<StringWrapper,Integer> con nomeGiocatore-numeroCarte
     * @param direzione, boolean per stampare la ruota della direzione
     */
    public void stampaTurnazione(Map<StringWrapper,IntegerAndBooleanWrapper> turnazione, boolean direzione) {
    	Platform.runLater(()->{
    		for(Label l:giocatoriLbl)
    			l.setEffect(null);
    		stampaDirezione(direzione);
    		int size=turnazione.size();
    		String campo="";
    		int i=0;
    		int posizione;
    		LayoutGiocatori layout=LayoutGiocatori.values()[size-1];
    		
    		for(Map.Entry<StringWrapper, IntegerAndBooleanWrapper> entry : turnazione.entrySet()) {
    			campo="";
				campo += entry.getKey().getValue();
        		campo += "\n("+entry.getValue().getNumero()+" carte)";
        		posizione=layout.getPosizioneCasella(i);
        		giocatoriLbl.get(posizione).setText(campo);
        		giocatoriLbl.get(posizione).setVisible(true);
        		if(entry.getValue().isFlag())
        			makeGlowingPulse(giocatoriLbl.get(posizione));
        		i++;
    		}
    	});
    }
    
    /**
     * metodo che imposta una label con un timer (da far partire dentro il controllore)
     * NON gestisce la logica del timer, ma solo il lato grafico
     * 
     * @param secondsLeft
     */
    public void setTimer(SimpleIntegerProperty secondsLeft) {
    	Label timerLabel = new Label();
		timerLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: red;");
		timerLabel.textProperty().bind(secondsLeft.asString("Tempo: %d s"));
    	timerBox.getChildren().clear();
    	timerBox.getChildren().add(timerLabel);
    }
    
    /**
     * metodo che fa uscire un pop-up alla fine della partita (se invocato da controllore)
     * se utente preme X o pulsante "Torna al menù", allora viene avviato il runnable
     * 
     * @param vincitore, stringa con nome vincitore
     * @param azioneTornaMenu, runnable su cui fare gestire il callback nel controllore
     */
    public void stampaFinePartita(String vincitore, Runnable azioneTornaMenu) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fine partita");
            alert.setHeaderText("La partita è terminata!");
            alert.setContentText(vincitore+" ha vinto!");
            alert.setGraphic(null);

            //creo un solo pulsante personalizzato
            ButtonType tornaMenuBtn = new ButtonType("Torna al menu iniziale", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(tornaMenuBtn);

            //mostro la finestra e catturo il risultato
            Optional<ButtonType> result = alert.showAndWait();

            //se l'utente preme il pulsante o chiude con X/ESC -> 
            if (!result.isPresent() || result.get() == tornaMenuBtn) {
            	if (azioneTornaMenu != null) {
                    azioneTornaMenu.run();
                }
            }
        });
    }
    
    public void mostraMenuOffline() {
    	app.mostraVistaMenuOffline();
    }

}
