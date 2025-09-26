package onegame.client.esecuzione;

import org.apache.logging.log4j.*;

import javafx.application.Application;
import javafx.stage.Stage;
import onegame.client.controllore.*;
import onegame.client.vista.*;
import onegame.client.vista.offline.*;
import onegame.client.vista.online.*;


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
    
    
    public void mostraVistaAccesso(String username) {
    	VistaAccesso vista=new VistaAccesso(this);
    	vista.compilaUsername(username);
    	ControlloreAccesso ca=new ControlloreAccesso(vista,ch);
    	primaryStage.setScene(vista.getScene());
    	ca.eseguiAccesso();
    }
    
    public void mostraVistaAccesso() {
    	this.mostraVistaAccesso("");
    }
    
    public void mostraVistaRegistrazione() {
    	VistaRegistrazione vista=new VistaRegistrazione(this);
    	ControlloreRegistrazione cr=new ControlloreRegistrazione(vista);
    	primaryStage.setScene(vista.getScene());
    	cr.eseguiRegistrazione();
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
    
    public void mostraVistaMenuOnline() {
    	VistaMenuOnline vista = new VistaMenuOnline(this);
        primaryStage.setScene(vista.getScene());
    }
    
    public void mostraVistaConfigurazioneOnline() {
        VistaConfigurazioneOnline vista = new VistaConfigurazioneOnline(this);
        primaryStage.setScene(vista.getScene());
    }
    
    public void mostraVistaInserimentoCodice() {
    	VistaInserimentoCodice vista= new VistaInserimentoCodice(this);
    	ControlloreCodicePartita cc=new ControlloreCodicePartita(vista);
    	primaryStage.setScene(vista.getScene());
    	cc.eseguiAccesso();
    }

    public void mostraVistaMenuOffline() {
        VistaMenuOffline vista = new VistaMenuOffline(this);
        primaryStage.setScene(vista.getScene());
    }

    public void mostraVistaSalvataggi() {
        VistaSalvataggi vista = new VistaSalvataggi(this);
        primaryStage.setScene(vista.getScene());
    }

    public void mostraVistaConfigurazioneOffline() {
        VistaConfigurazioneOffline vista = new VistaConfigurazioneOffline(this);
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
