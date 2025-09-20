package esecuzione;

import org.apache.logging.log4j.*;

import controllore.ControlloreAccesso;
import controllore.ControlloreGioco;
import controllore.ControlloreHome;
import javafx.application.Application;
import javafx.stage.Stage;
import vista.VistaAccesso;
import vista.VistaConfigurazione;
import vista.VistaGioco;
import vista.VistaHome;
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
	private ControlloreHome ch;
	
	
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.ch=new ControlloreHome();
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
    	ControlloreAccesso ca=new ControlloreAccesso(vista,ch);
    	primaryStage.setScene(vista.getScene());
    	ca.eseguiAccesso();
    }
    
    public void mostraVistaHome() {
    	VistaHome vista=new VistaHome(this, ch);
    	primaryStage.setScene(vista.getScene());
    }
    
    public void mostraVistaHomeOspite() {
    	ch.setUtente(null);
    	VistaHome vista=new VistaHome(this, ch);
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
