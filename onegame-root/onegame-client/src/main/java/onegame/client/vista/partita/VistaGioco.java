package onegame.client.vista.partita;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreEffettiGenerici;
import onegame.client.vista.accessori.GestoreGraficaCarta;
import onegame.client.vista.accessori.GestoreHoverCarta;
import onegame.modello.Mossa;
import onegame.modello.Mossa.TipoMossa;
import onegame.modello.carte.Carta;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class VistaGioco extends VistaPartita {
    private Pane mano;
    private ScrollPane contenitoreManoPane;
    private Dialog<?> finestraAperta;
    private Pane overlay;

    public VistaGioco(AppWithMaven app) {
    	super(app);
    	
    	//contenitore inferiore
    	HBox contenitoreInferiore = new HBox(10);
    	contenitoreInferiore.setAlignment(Pos.CENTER);
    	mano=new Pane();
    	//mano.getStyleClass().add("contenitore-trasparente");
        contenitoreManoPane = new ScrollPane(mano);
        contenitoreManoPane.setStyle(
        	    "-fx-background-color: transparent;" +   
        	    "-fx-background: transparent;");
        ((Region) contenitoreManoPane.getContent()).setStyle("-fx-background-color: transparent;");
        contenitoreManoPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contenitoreManoPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        contenitoreManoPane.setFitToWidth(false);
        contenitoreManoPane.setFitToHeight(false); // si adatta in altezza
        contenitoreManoPane.setPannable(false);
        contenitoreManoPane.setMinHeight(Region.USE_COMPUTED_SIZE);
        contenitoreInferiore.getChildren().add(contenitoreManoPane);
        //contenitoreInferiore.setMinHeight(200);
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
    	scene = new Scene(sceneRoot, app.getPrimaryStage().getWidth(), app.getPrimaryStage().getHeight());
    	scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    	
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
    	Platform.runLater(() -> {
    		overlay.getChildren().clear();
    		for (Node c : mano.getChildren()) {
    		    c.setOpacity(1);
    		    c.getProperties().remove("overlayView");
    		}
    		contenitoreManoPane.setEffect(null);
    		GestoreEffettiGenerici.assegnaPulsazioneColorata(contenitoreManoPane, Color.CRIMSON);
    		pescaBtn.setVisible(true);
    		timerBox.setVisible(true);
    		stampaCartaCorrente(cartaCorrente);
    		GestoreEffettiGenerici.assegnaPulsazioneColorata(this.cartaCorrente, Color.AZURE);
            mano.getChildren().clear();
            int i=0;
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
//                Tooltip tooltip = new Tooltip("Gioca questa carta");
//                tooltip.setStyle(""
//                		+ "-fx-background-color: rgba(0,0,0,0.5);" + // nero al 50% di opacità
//                	    "-fx-text-fill: white;" +
//                	    "-fx-padding: 4px;" +
//                	    "-fx-background-radius: 4;");
//                tooltip.setShowDelay(Duration.millis(10));
//                Tooltip.install(carta, tooltip);
                
                //collega overlay su hover
                //GestoreHoverCarta.bindOverlay(carta, overlay, contenitoreManoPane, /*liftPx*/ 18);
                
                //prove particolari
                carta.setLayoutX(i * 65);
                carta.setLayoutY(0);
                i++;
                
                //fine prove particolari
                mano.getChildren().add(carta);
            }
            GestoreHoverCarta.bindOverlayNuovo(mano, overlay, contenitoreManoPane, 18, false);
        });
    	
    	pescaBtn.setOnAction(e -> {
    		callback.accept(new Mossa(TipoMossa.PESCA));
        });
    }
    
    public void stampaManoReadOnly(Carta cartaCorrente, Giocatore g) {
    	Platform.runLater(() -> {
    		overlay.getChildren().clear();
    		for (Node c : mano.getChildren()) {
    		    c.setOpacity(1);
    		    c.getProperties().remove("overlayView");
    		}
    		contenitoreManoPane.setEffect(null);
    		this.cartaCorrente.setEffect(null);
    		int i=0;
    		pescaBtn.setVisible(false);
    		timerBox.setVisible(false);
    		stampaCartaCorrente(cartaCorrente);
            mano.getChildren().clear();
            for (Carta c : g.getMano().getCarte()) {
            	StackPane carta=GestoreGraficaCarta.creaVistaCarta(c);
                carta.setStyle(
                		"-fx-cursor: hand;"+
                		" -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);"
                		);
                
                //collega overlay su hover
                //GestoreHoverCarta.bindOverlay(carta, overlay, contenitoreManoPane, /*liftPx*/ 18);
                
              //prove particolari
                carta.setLayoutX(i * 65);
                carta.setLayoutY(0);
                i++;
                
                //fine prove particolari
                mano.getChildren().add(carta);
            }
            GestoreHoverCarta.bindOverlayNuovo(mano, overlay, contenitoreManoPane, 18, true);
        });
    }

	@Override
	public void mostraVista() {
		cartaCorrente.getChildren().clear();
		mano.getChildren().clear();
		super.mostraVista();
	}

    
    
}
