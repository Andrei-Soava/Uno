package esecuzione;

import org.apache.logging.log4j.*;

import controllore.ControlloreGioco;
import javafx.application.Application;
import javafx.stage.Stage;
import vista.VistaAccesso;
import vista.VistaConfigurazione;
import vista.VistaGioco;
import vista.VistaIniziale;
import vista.VistaSalvataggi;


/**
 * Hello world!
 *
 */
public class AppWithMaven extends Application {
	public static Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		launch(args);
	}
	
	private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("UNO - JavaFX");
        stage.setWidth(900);
        stage.setHeight(600);
        
        //vista mostrata di default
        mostraVistaAccesso();

        stage.show();
    }
    
    public Stage getPrimaryStage() {
    	return this.primaryStage;
    }
    
    public void mostraVistaAccesso() {
    	VistaAccesso vista=new VistaAccesso(this);
    	primaryStage.setScene(vista.getScene());
    }

    public void mostraVistaIniziale() {
        VistaIniziale vista = new VistaIniziale(this);
        primaryStage.setScene(vista.getScene());
    }

    public void mostraVistaSalvataggi() {
        VistaSalvataggi vista = new VistaSalvataggi(this);
        primaryStage.setScene(vista.getScene());
    }

    public void mostraVistaConfigurazione() {
        VistaConfigurazione vista = new VistaConfigurazione(this);
        primaryStage.setScene(vista.getScene());
    }

    public void mostraVistaGiocoCaricato(String salvataggio) {
        VistaGioco vista = new VistaGioco(this);
        ControlloreGioco controllore = new ControlloreGioco(vista); // Passo la vista al controller
        vista.cg=controllore;
        controllore.caricaPartita(salvataggio);
        primaryStage.setScene(vista.getScene());
        controllore.avviaPartita();
    }
    
    public void mostraVistaGiocoNuovo(int numGiocatori) {
        VistaGioco vista = new VistaGioco(this);
        ControlloreGioco controllore = new ControlloreGioco(vista); //passo la vista al controller (serve per interrompere il gioco)
        vista.cg=controllore;
        //se si vuole giocatori tra persone umane toglie il "vsBot"
        controllore.configuraNuovaPartitaVsBot(numGiocatori);
        primaryStage.setScene(vista.getScene());
        controllore.avviaPartita();
    }

}
