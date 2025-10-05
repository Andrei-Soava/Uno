package onegame.client.vista.partita;

import java.util.Map;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import onegame.client.controllore.offline.ControlloreGioco;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.persistenza_temporanea.ManagerPersistenza;
import onegame.client.vista.accessori.GestoreGraficaCarta;
import onegame.modello.Partita.StringWrapper;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;

public abstract class VistaPartita {
	protected Scene scene;
	protected AppWithMaven app;
	protected BorderPane root;
	protected Label turnoCorrenteLbl;
	protected Label prossimoTurnoLbl;
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
    	pescaBtn = new Button("Pesca");
    	sottoContenitoreSinistra.getChildren().addAll(spacerTop, sp, pescaBtn);
    	
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
    	prossimoTurnoLbl.setStyle("-fx-font-size: 18px;");
    	timerBox=new HBox();
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

    
    protected void impostaCartaCorrente(Carta cartaCorrente) {
    	this.cartaCorrente.getChildren().add(GestoreGraficaCarta.creaVistaCarta(cartaCorrente));
    }

    public void stampaTurnoCorrente(String giocatore) {
        Platform.runLater(() -> {
        	turnoCorrenteLbl.setText("Turno di "+giocatore.toUpperCase());
        });
    }
    
    /**
     * metodo di aggiornamento del prossimoTurnoLbl:
     * stampa l'ordine dei giocatori dopo il giocatore corrente
     * ed il numero di carte di ciascuno
     * 
     * @param turnazione mappa<StringWrapper,Integer> con nomeGiocatore-numeroCarte
     */
    public void stampaTurnazione(Map<StringWrapper,Integer> turnazione) {
    	Platform.runLater(()->{
    		String s="TURNAZIONE:\n";
    		int size=turnazione.size();
    		for(Map.Entry<StringWrapper, Integer> entry : turnazione.entrySet()) {
    			s += entry.getKey().getValue();
    			s += "\n("+entry.getValue()+" carte)\n";
    			size--;
    			if(size!=0) {
    				s+="    ↓\n";
    			}
    		}
    		prossimoTurnoLbl.setText(s);
    	});
    }
    
//    public void stampaProssimoTurno(String giocatore) {
//    	Platform.runLater(() -> {
//        	prossimoTurnoLbl.setText("Prossimo: \n"+giocatore.toUpperCase());
//        });
//    }
    
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
                app.mostraVistaMenuOffline();
            }
        });
    }

}
