package vista;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import modello.Mossa;
import modello.Mossa.TipoMossa;
import modello.carte.Carta;
import modello.carte.CartaSpeciale;
import modello.carte.Colore;
import modello.giocatori.Giocatore;
import persistenza.ManagerPersistenza;
import prova.AppWithMaven;
import vista.accessori.GestoreHoverCarta;
import vista.accessori.GestoreGraficaCarta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import controllore.ControlloreGioco;

public class VistaGioco {

	private AppWithMaven app;
	private Scene scene;
	
	public ControlloreGioco cg;
	
    private Label turnoCorrenteLbl;
    private Label prossimoTurnoLbl;
    private StackPane cartaCorrente;
    private Label logAreaLbl;
    
    private Dialog<?> finestraAperta;
    
    private HBox mano;
    private ScrollPane contenitoreManoPane;
    
    private Button ONEBtn;
    private HBox timerBox;
    private Button pescaBtn;
    private Pane overlay;

    public VistaGioco(AppWithMaven app) {
    	this.app=app;
    	this.cg=null;
    	BorderPane root = new BorderPane();
    	root.setPadding(new Insets(10));
    	root.setStyle("-fx-background-color: orange;");

    	//---------------------------------------------------------------------------------
    	//barra superiore con pulsante Home & label per indicare il turno
    	//bottone per abbandonare
    	Button abbandonaBtn = new Button("← Home");
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
    	            app.mostraVistaIniziale();
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
    	//sottoContenitoreSinistra.setStyle("-fx-border-color:black;");
    	sottoContenitoreSinistra.setAlignment(Pos.CENTER);
    	StackPane sp=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,null));
    	pescaBtn = new Button("Pesca");
    	sottoContenitoreSinistra.getChildren().addAll(sp, pescaBtn);
    	
    	//
    	//zona carta corrente (centrale al centro)
    	VBox sottoContenitoreCentrale = new VBox(10);
    	//sottoContenitoreCentrale.setStyle("-fx-border-color:black;");
    	sottoContenitoreCentrale.setAlignment(Pos.CENTER);
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
    	//zona messaggi e futuro pulsante ONE (centrale a destra)
    	VBox sottoContenitoreDestra = new VBox(10);
    	sottoContenitoreDestra.setPadding(new Insets(0));
    	//sottoContenitoreDestra.setStyle("-fx-border-color:black;");
    	prossimoTurnoLbl=new Label();
    	prossimoTurnoLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    	timerBox=new HBox();
    	//logAreaLbl = new Label(); // manteniamo il nome logArea
    	//logAreaLbl.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 5;");
    	//logAreaLbl.setVisible(false);
    	Region spacer = new Region();
    	VBox.setVgrow(spacer, Priority.ALWAYS);
    	ONEBtn=new Button("ONE!");
    	ONEBtn.setVisible(false);
    	sottoContenitoreDestra.getChildren().addAll(prossimoTurnoLbl,timerBox, spacer, ONEBtn);
    	
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
    	mano=new HBox();
        contenitoreManoPane = new ScrollPane(mano);
        contenitoreManoPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contenitoreManoPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contenitoreManoPane.setFitToWidth(false);
        contenitoreManoPane.setFitToHeight(false); // si adatta in altezza
        contenitoreManoPane.setPannable(false);
        contenitoreManoPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        contenitoreInferiore.getChildren().add(contenitoreManoPane);
        //imposto il contenitore inferiore sotto
        root.setBottom(contenitoreInferiore);
    	
        //---------------------------------------------------------------------------------
        /**
         * sezione hover ai lati della scrollPane
         */
        final double edgeSize = 40;      // px vicino ai bordi attivi
        final double scrollStep = 0.03;  // velocità per frame (0..1)
        // proprietà velocità condivisa tra eventi e timer
        SimpleDoubleProperty scrollSpeed = new SimpleDoubleProperty(0);

        // timer che legge la velocità e aggiorna hvalue
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double speed = scrollSpeed.get();
                if (speed != 0) {
                    double newValue = contenitoreManoPane.getHvalue() + speed;
                    contenitoreManoPane.setHvalue(Math.max(0, Math.min(1, newValue)));
                }
            }
        };
        timer.start();

        // aggiorna la velocità in base alla posizione del mouse nella viewport
        contenitoreManoPane.setOnMouseMoved(e -> {
            double x = e.getX();
            double width = contenitoreManoPane.getViewportBounds().getWidth();

            if (x < edgeSize) {
                scrollSpeed.set(-scrollStep); // sinistra
            } else if (x > width - edgeSize) {
                scrollSpeed.set(scrollStep);  // destra
            } else {
                scrollSpeed.set(0);
            }
        });

        // ferma lo scroll quando il mouse esce
        contenitoreManoPane.setOnMouseExited(e -> scrollSpeed.set(0));   
        //---------------------------------------------------------------------------------

        //overlay in cima a tutto
        overlay = new Pane();
        overlay.setPickOnBounds(false);
        overlay.setMouseTransparent(true);


        StackPane sceneRoot = new StackPane(root, overlay);
    	scene = new Scene(sceneRoot, 900, 600);
    	
    }

    public Scene getScene() {
        return scene;
    }


    /**
     * sezione inserimento stringhe/numeri 
     * 
     */
    public String inserisciStringa(String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(message);
        return dialog.showAndWait().orElse("NESSUN NOME");
    }

    /**
     * 
     * @deprecated l'unico che lo usa ancora è il ControllorePersistenza, ma de facto no
     */
    public int scegliTraN(String message, int minValue, int maxValue) {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(minValue,
                java.util.stream.IntStream.rangeClosed(minValue, maxValue).boxed().toList());
        dialog.setHeaderText(message);
        return dialog.showAndWait().orElse(minValue);
    }


    //---------------------------------------------------------------------------------
    /**
     * sezione stampa testo/carte/
     * 
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

    
    private void impostaCartaCorrente(Carta cartaCorrente) {
    	this.cartaCorrente.getChildren().add(GestoreGraficaCarta.creaVistaCarta(cartaCorrente));
    }

    public void stampaTurno(String giocatore) {
        Platform.runLater(() -> {
        	turnoCorrenteLbl.setText("Turno di "+giocatore.toUpperCase());
        });
    }
    
    public void stampaProssimoTurno(String giocatore) {
    	Platform.runLater(() -> {
        	prossimoTurnoLbl.setText("Prossimo: \n"+giocatore.toUpperCase());
        });
    }
    
    public void setTimer(SimpleIntegerProperty secondsLeft) {
    	Label timerLabel = new Label();
		timerLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: red;");
		timerLabel.textProperty().bind(secondsLeft.asString("Tempo: %d s"));
    	timerBox.getChildren().clear();
    	timerBox.getChildren().add(timerLabel);
    }
    
    public void stampaFinePartita(Giocatore vincitore, Runnable azioneTornaMenu) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fine partita");
            alert.setHeaderText("La partita è terminata!");
            alert.setContentText(vincitore.getNome()+" ha vinto!");
            alert.setGraphic(null);

            //creo un solo pulsante personalizzato
            ButtonType tornaMenuBtn = new ButtonType("Torna al menu iniziale", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(tornaMenuBtn);

            //mostro la finestra e catturo il risultato
            Optional<ButtonType> result = alert.showAndWait();

            //se l'utente preme il pulsante o chiude con X/ESC -> 
            if (!result.isPresent() || result.get() == tornaMenuBtn) {
            	String salvataggio=cg.getCp().getSalvataggioCorrente();
            	ManagerPersistenza.eliminaSalvataggio(salvataggio);
                app.mostraVistaIniziale();
            }
        });
    }

    
    //---------------------------------------------------------------------------------
    /**
     * sezione per gestione finestre di dialog (aperte o chiuse)
     * 
     */
    private void registraFinestra(Dialog<?> dialog) {
        //chiudi eventuale finestra precedente
        if (finestraAperta != null && finestraAperta.isShowing()) {
            finestraAperta.close();
        }
        finestraAperta = dialog;
        dialog.setOnHidden(e -> finestraAperta = null); // reset automatico
    }

    //serve per timer scaduto
    public void chiudiFinestraAperta() {
        if (finestraAperta != null && finestraAperta.isShowing()) {
            finestraAperta.close();
            finestraAperta = null;
        }
    }
    
    //---------------------------------------------------------------------------------
    /**
     * sezione azioni extra durante il gioco:
     * -scegliere se tenere o meno carta pescata
     * -scegliere il colore
     * -premere (o meno) pulsante ONE
     * 
     */
    //mostra la carta pescata
    public void stampaCartaPescataAsync(Carta pescata, Consumer<Integer> callback) {
    	System.out.println("dentro il scegli azione su carta");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Scegli");
            alert.setHeaderText("Vuoi giocarla?");
            alert.setGraphic(null);
            alert.getDialogPane().setContent(GestoreGraficaCarta.creaVistaCarta(pescata));

            ButtonType btn0 = new ButtonType("Tienila", ButtonBar.ButtonData.CANCEL_CLOSE);
            ButtonType btn1 = new ButtonType("Giocala", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(btn0, btn1);

            registraFinestra(alert);
            alert.setOnHidden(e -> {
                ButtonType result = alert.getResult();
                int scelta = (result==btn1) ? 1:0;
                callback.accept(scelta);
            });
            
            alert.show();
        });
    }

    //mostra la scelta colore
    public void stampaColoriAsync(Consumer<Colore> callback) {
    	System.out.println("dentro il scegli colore");
        Platform.runLater(() -> {
            List<Colore> colori = Arrays.asList(Colore.values()).subList(0, 4);
            Colore coloreDefault = Colore.ROSSO;

            Dialog<Colore> dialog = new Dialog<>();
            dialog.setTitle("Scegli un colore");
            dialog.setHeaderText("Scegli un colore");
            dialog.setResizable(false);

            GridPane grid = new GridPane();
            grid.setHgap(15);
            grid.setVgap(15);
            grid.setPadding(new Insets(20));
            grid.setAlignment(Pos.CENTER);

            for (int row = 0, col = 0, i = 0; i < colori.size(); i++) {
                Colore c = colori.get(i);
                Rectangle quadrato = new Rectangle(60, 60);
                quadrato.setFill(GestoreGraficaCarta.mapColore(c));
                quadrato.setStroke(Color.BLACK);
                quadrato.setStrokeWidth(2);

                quadrato.setOnMouseEntered(e -> quadrato.setScaleX(1.1));
                quadrato.setOnMouseExited(e -> quadrato.setScaleX(1.0));
                quadrato.setOnMouseClicked(e -> {
                    dialog.setResult(c);
                    dialog.close();
                });

                grid.add(quadrato, col, row);
                if (++col > 1) { col = 0; row++; }
            }

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.setResultConverter(button -> coloreDefault);

            registraFinestra(dialog);
            
            dialog.setOnHidden(e -> {
                Colore result = dialog.getResult();
                if (result == null) result = coloreDefault;
                callback.accept(result);
            });
            
            dialog.show();
        });
    }
    
    //mostra pulsante ONE
    public void mostraPulsanteONE(Consumer<Boolean> scelta) {
        AtomicBoolean premuto = new AtomicBoolean(false);

        Platform.runLater(() -> {
            //mostra pulsante
            ONEBtn.setVisible(true);

            //se click entro scadenza-> scelta = true
            ONEBtn.setOnAction(e -> {
                if (premuto.compareAndSet(false, true)) {
                    ONEBtn.setVisible(false);
                    scelta.accept(true);
                }
            });

            //se scade timer-> scelta = false;
            PauseTransition timer = new PauseTransition(Duration.seconds(5));
            timer.setOnFinished(ev -> {
                if (premuto.compareAndSet(false, true)) {
                    ONEBtn.setVisible(false);
                    scelta.accept(false);
                }
            });
            timer.play();
        });
    }


    //---------------------------------------------------------------------------------
    /**
     * sezione scelta principale durante il turno
     */
    public void scegliMossaAsync(Carta cartaCorrente, Giocatore g, Consumer<Mossa> callback) {
    	System.out.println("dentro il scegli mossa");
    	Platform.runLater(() -> {
    		impostaCartaCorrente(cartaCorrente);
            mano.getChildren().clear();
            for (Carta c : g.getMano().getCarte()) {
            	StackPane carta=GestoreGraficaCarta.creaVistaCarta(c);
            	//associazione click carta ad evento di callback
            	carta.setOnMouseClicked(e -> {
            		//countdown.stop();
            		//timer.stop();
            		callback.accept(new Mossa(TipoMossa.GIOCA_CARTA, c));
            		
            	});
                carta.setStyle(
                		"-fx-cursor: hand;"+
                		" -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);"
                		);
                
                //scritta sopra il cursore
                Tooltip tooltip = new Tooltip("Gioca questa carta");
                tooltip.setStyle(""
                		+ "-fx-background-color: rgba(0,0,0,0.5);" + // nero al 50% di opacità
                	    "-fx-text-fill: white;" +
                	    "-fx-padding: 4px;" +
                	    "-fx-background-radius: 4;");
                tooltip.setShowDelay(Duration.millis(10));
                Tooltip.install(carta, tooltip);
                
                //collega overlay su hover
                GestoreHoverCarta.bindOverlay(carta, overlay, contenitoreManoPane, /*liftPx*/ 18);
                
                mano.getChildren().add(carta);
            }
        });
    	
    	pescaBtn.setOnAction(e -> {
    		callback.accept(new Mossa(TipoMossa.PESCA));
        });
    }
    
    
}
