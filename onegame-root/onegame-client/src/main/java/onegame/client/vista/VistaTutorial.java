package onegame.client.vista;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.Node;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreGraficaCarta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
import onegame.modello.carte.Colore;

public class VistaTutorial {

	private Scene scene;
	private AppWithMaven app;
	private int slideIndex = 0;
    private final List<Node> slideViews = new ArrayList<>();
    private final StackPane slideContainer = new StackPane();
    private final Button indietroBtn = new Button("◀ Indietro");
    private final Button avantiBtn = new Button("Avanti ▶");
	
    public VistaTutorial(AppWithMaven app) {
    	this.app=app;
        BorderPane root = new BorderPane();

    	BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(10));

        Button homeBtn = new Button("← Home");
    	homeBtn.setOnAction(e -> app.mostraVistaHome());

        Label titolo = new Label("Regole di gioco");
        titolo.getStyleClass().add("titolo");
        titolo.setTranslateX(-40);


        topBar.setLeft(homeBtn);
        topBar.setCenter(titolo);
        BorderPane.setAlignment(titolo, Pos.CENTER);

    	root.setTop(topBar);
        
        //centro con tutorial
        VBox centro = new VBox(20);
        centro.setAlignment(Pos.CENTER);
        centro.setPrefSize(600, 400);
        centro.setMaxSize(600, 400);
        centro.setPadding(new Insets(20));

        //slide di tutorial
        slideViews.add(creaSlide1());
        slideViews.add(creaSlide2());
        slideViews.add(creaSlide3());
        slideViews.add(creaSlide4());
        slideViews.add(creaSlide5());
        slideViews.add(creaSlide6());
        slideViews.add(creaSlide7());

        //contenitore slide
        slideContainer.getChildren().add(slideViews.get(slideIndex));
        slideContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(slideContainer, Priority.ALWAYS);
        centro.getChildren().add(slideContainer);
        root.setCenter(centro);

        //bottom bar con bottoni di navigazione
        indietroBtn.setOnAction(e -> mostraSlide(slideIndex - 1));
        avantiBtn.setOnAction(e -> mostraSlide(slideIndex + 1));

        HBox bottomBar = new HBox(10, indietroBtn, avantiBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(10));
        root.setBottom(bottomBar);

        aggiornaBottoni();
        
        scene = new Scene(root);
        scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    }

    public Scene getScene() {
        return scene;
    }
    
    //SEZIONE SLIDE SINGOLE
    
    /**
     * @return prima slide (introduzione generica)
     */
    private BorderPane creaSlide1() {
    	Label titoloLbl = new Label("Introduzione");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("Benvenuto in questa piccola introduzione alle regole di ONE!\n\n"
        		+ "Qualora già conoscessi le regole del gioco di carte UNO, allora sai già tutto, altrimenti clicca su 'avanti'.\n"
        		+ " \n");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");
        StackPane carta1=GestoreGraficaCarta.creaVistaCarta(new CartaNumero(Colore.VERDE,5));
        StackPane carta2=GestoreGraficaCarta.creaVistaCarta(new CartaNumero(Colore.GIALLO,0));
        StackPane carta3=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,TipoSpeciale.PIU_QUATTRO));
        StackPane carta4=GestoreGraficaCarta.creaVistaCarta(new CartaNumero(Colore.BLU,8));
        StackPane carta5=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.ROSSO,TipoSpeciale.INVERTI));
        HBox grafica=new HBox(10,carta1,carta2,carta3,carta4,carta5);
        grafica.setAlignment(Pos.CENTER);
        descrizioneLbl.setGraphic(grafica);
        descrizioneLbl.setContentDisplay(ContentDisplay.BOTTOM);

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(40));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * @return seconda slide (obiettivo del gioco)
     */
    private BorderPane creaSlide2() {
    	Label titoloLbl = new Label("Obiettivo del gioco");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("Lo scopo del gioco è essere il primo a rimanere senza carte in mano.\n\n"
        		+ "Si gioca a turni, scartando una carta che corrisponde per colore o numero/simbolo a quella sul tavolo.");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(40));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * @return terza slide (preparazione)
     */
    private BorderPane creaSlide3() {
    	Label titoloLbl = new Label("Preparazione");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("All'inizio, ogni giocatore riceve 7 carte.\n"
        		+ "\n"
        		+ "La prima carta del mazzo diventa la prima carta sul tavolo.\n"
        		+ "\n"
        		+ "Il resto delle carte forma il mazzo pesca.\n");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");
        Label mazzo=new Label("MAZZO");
        StackPane carta=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,null));
        VBox grafica=new VBox(10,mazzo,carta);
        grafica.setAlignment(Pos.CENTER);
        descrizioneLbl.setGraphic(grafica);
        descrizioneLbl.setContentDisplay(ContentDisplay.BOTTOM);

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(10));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * @return quarta slide (svolgimento del turno)
     */
    private BorderPane creaSlide4() {
    	Label titoloLbl = new Label("Svolgimento del turno");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("Nel proprio turno un giocatore può:\n"
        		+ "\n"
        		+ "1. Giocare una carta che corrisponde per colore o numero/simbolo;\n"
        		+ "2. Giocare una carta speciale (+4 o JOLLY); \n"
        		+ "3. Se non può giocare, deve pescare una carta;\n"
        		+ "\n"
        		+ "Se la carta pescata è giocabile, può subito giocarla.\n"
        		+ "Se il giocatore non esegue una mossa entro il tempo limite, pesca senza possibilità di giocare la carta.");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(40));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * @return quinta slide (carte speciali)
     */
    private BorderPane creaSlide5() {
    	Label titoloLbl = new Label("Carte Speciali");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("+2 → il giocatore successivo pesca due carte e salta il turno;\n"
        		+ "Inverti → cambia il senso di gioco;\n"
        		+ "Salta → il giocatore successivo salta il turno;\n"
        		+ "Jolly → scegli il colore e fa perdere il turno al giocatore successivo;\n"
        		+ "+4 → il giocatore successivo pesca quattro carte e salta il turno, puoi anche scegliere il colore (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");
        StackPane piu_due=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.ROSSO,TipoSpeciale.PIU_DUE));
        StackPane inverti=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.GIALLO,TipoSpeciale.INVERTI));
        StackPane salta=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.BLU,TipoSpeciale.BLOCCA));
        StackPane jolly=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,TipoSpeciale.JOLLY));
        StackPane piu_quattro=GestoreGraficaCarta.creaVistaCarta(new CartaSpeciale(Colore.NERO,TipoSpeciale.PIU_QUATTRO));
        HBox grafica=new HBox(10,piu_due,inverti,salta,jolly,piu_quattro);
        grafica.setAlignment(Pos.CENTER);
        descrizioneLbl.setGraphic(grafica);
        descrizioneLbl.setContentDisplay(ContentDisplay.BOTTOM);

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(10));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * @return sesta slide (regola ONE!)
     */
    private BorderPane creaSlide6() {
    	Label titoloLbl = new Label("Regola 'ONE!'");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("Quando un giocatore resta con una sola carta, deve dichiarare “ONE!” (premendo un pulsante temporaneo entro un tempo limite).\n"
        		+ "\n"
        		+ "Se il giocatore non dichiara 'ONE!' entro il tempo limite, quest'ultimo deve pescare 2 carte di penalità.\n"
        		+ " \n");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");
        descrizioneLbl.setGraphic(new Button("ONE!"));
        descrizioneLbl.setContentDisplay(ContentDisplay.BOTTOM);

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(40));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * @return settima slide (conclusione)
     */
    private BorderPane creaSlide7() {
    	Label titoloLbl = new Label("Conclusione");
        titoloLbl.getStyleClass().add("titolo");

        Label descrizioneLbl = new Label("La mano termina quando un giocatore gioca l’ultima carta.\n\n"
        		+ "Ora conosci tutte le regole di ONE! Ora di iniziare a giocare!\n"
        		+ " \n");
        descrizioneLbl.setWrapText(true);
        descrizioneLbl.getStyleClass().add("grassetto");
        Button iniziaBtn=new Button("Inizia a giocare!");
        iniziaBtn.setOnAction(e->{
        	app.mostraVistaHome();
        });
        descrizioneLbl.setGraphic(iniziaBtn);
        descrizioneLbl.setContentDisplay(ContentDisplay.BOTTOM);

        BorderPane slide = new BorderPane();
        slide.setTop(titoloLbl);
        slide.setCenter(descrizioneLbl);
        BorderPane.setAlignment(titoloLbl, Pos.TOP_CENTER);
        BorderPane.setAlignment(descrizioneLbl, Pos.CENTER);
        slide.setPadding(new Insets(40));
        slide.getStyleClass().add("contenitore");

        return slide;
    }
    
    /**
     * metodo per modificare slide attuamente mostrata
     * 
     * @param nuovoIndice, numero di slide selezionata
     */
    private void mostraSlide(int nuovoIndice) {
        if (nuovoIndice < 0 || nuovoIndice >= slideViews.size()) return;
        Node nuovaSlide = slideViews.get(nuovoIndice);
        slideContainer.getChildren().setAll(nuovaSlide);

        // opzionale: fade-in
        FadeTransition ft = new FadeTransition(Duration.millis(300), nuovaSlide);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        slideIndex = nuovoIndice;
        aggiornaBottoni();
    }
    
    /**
     * metodo ausiliario per evitare che:
     * -bottone 'indietro' appaia per la prima slide;
     * -bottone 'avanti' appaia per l'ultima slide;
     */
    private void aggiornaBottoni() {
        indietroBtn.setVisible(slideIndex > 0);
        avantiBtn.setVisible(slideIndex < slideViews.size() - 1);
    }

}
