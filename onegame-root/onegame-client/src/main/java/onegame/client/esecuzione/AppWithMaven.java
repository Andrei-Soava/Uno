package onegame.client.esecuzione;

import java.util.List;

import org.apache.logging.log4j.*;

import javafx.application.Application;
import javafx.stage.Stage;
import onegame.client.controllore.*;
import onegame.client.controllore.offline.*;
import onegame.client.controllore.online.*;
import onegame.client.controllore.online.stanza.*;
import onegame.client.net.*;
import onegame.client.vista.*;
import onegame.client.vista.offline.*;
import onegame.client.vista.online.*;
import onegame.client.vista.partita.*;

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
	private ClientSocket cs;
	private ConnectionMonitor cm;

	/**
	 * metodo chiamato per aprire interfaccia grafica
	 */
	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		try {
			cs = new ClientSocket("http://127.0.0.1:8080/");
			cm=new ConnectionMonitor(cs);
			cs.connect();
			System.out.println("connessione avvenuta con successo");
		} catch (Exception e) {
			e.printStackTrace();
		}

		stage.setTitle("ONE");
		stage.setWidth(900);
		stage.setHeight(600);

		// vista mostrata di default
		mostraVistaAccesso();

		stage.show();
	}
	
	
	/**
	 * metodo per gestire chiusura applicazione
	 */
	@Override
	public void stop() throws Exception {
		if (cs != null) {
			try {
				cs.disconnect();
				System.out.println("disconnessione avvenuta con successo");
				cm.stop();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			//Platform.exit();   // per JavaFX
			System.exit(0);    // forza la chiusura della JVM
		}
	}


	//SEZIONE GETTER GLOBALI
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	public ClientSocket getCs() {
		return this.cs;
	}

	//SEZIONE VISTE PRINCIPALI
	public void mostraVistaAccesso(String username) {
		VistaAccesso vista = new VistaAccesso(this);
		vista.compilaUsername(username);
		@SuppressWarnings("unused")
		ControlloreAccesso ca = new ControlloreAccesso(vista, cs,cm);
		primaryStage.setScene(vista.getScene());
		//ca.eseguiAccesso();
	}

	public void mostraVistaAccesso() {
		this.mostraVistaAccesso("");
	}

	public void mostraVistaRegistrazione() {
		VistaRegistrazione vista = new VistaRegistrazione(this);
		@SuppressWarnings("unused")
		ControlloreRegistrazione cr = new ControlloreRegistrazione(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//cr.eseguiRegistrazione();
	}

	public void mostraVistaHome() {
		VistaHome vista = new VistaHome(this);
		@SuppressWarnings("unused")
		ControlloreHome ch = new ControlloreHome(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//ch.aspettaLogout();
		//ch.aspettaStatistiche();
	}
	
	public void mostraVistaTutorial() {
		VistaTutorial vista = new VistaTutorial(this);
		primaryStage.setScene(vista.getScene());
	}
	
	public void mostraVistaImpostazioni() {
		VistaImpostazioni vista = new VistaImpostazioni(this);
		@SuppressWarnings("unused")
		ControlloreImpostazioni ci = new ControlloreImpostazioni(vista,cs,cm);
		primaryStage.setScene(vista.getScene());
		//ci.aspettaLogout();
		//ci.aspettaSelezione();
	}

	//SEZIONE VISTE GIOCO ONLINE
	public void mostraVistaMenuOnline() {
		VistaMenuOnline vista = new VistaMenuOnline(this);
		@SuppressWarnings("unused")
		ControlloreMenuOnline cmo=new ControlloreMenuOnline(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//cmo.aspettaLogout();
	}

	public void mostraVistaConfigurazioneOnline() {
		VistaConfigurazioneOnline vista = new VistaConfigurazioneOnline(this);
		@SuppressWarnings("unused")
		ControlloreConfigurazioneOnline cco= new ControlloreConfigurazioneOnline(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//cco.aspettaCreazioneStanza();
	}

	public void mostraVistaInserimentoCodice() {
		VistaInserimentoCodice vista = new VistaInserimentoCodice(this);
		@SuppressWarnings("unused")
		ControlloreCodicePartita cc = new ControlloreCodicePartita(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//cc.eseguiAccesso();
	}
	
	public void mostraVistaStanza(String codice, boolean host) {
		VistaStanza vista=new VistaStanza(this);
		vista.compilaCodicePartia(codice);
		ControlloreStanza cv;
		if(host) {
			cv=new ControlloreStanzaHost(vista,cs,cm);
		}
		else {
			cv=new ControlloreStanzaOspite(vista,cs,cm);
		}
		primaryStage.setScene(vista.getScene());
		cv.aspettaInizioPartita();
	}

	//SEZIONE VISTE GIOCO OFFLINE
	public void mostraVistaMenuOffline() {
		VistaMenuOffline vista = new VistaMenuOffline(this);
		@SuppressWarnings("unused")
		ControlloreMenuOffline cmo=new ControlloreMenuOffline(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//cmo.aspettaLogout();
	}

	public void mostraVistaSalvataggi() {
		VistaSalvataggi vista = new VistaSalvataggi(this);
		@SuppressWarnings("unused")
		ControlloreSalvataggi cslv=new ControlloreSalvataggi(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		//cslv.eseguiScelta();
	}

	public void mostraVistaConfigurazioneOffline() {
		VistaConfigurazioneOffline vista = new VistaConfigurazioneOffline(this);
		@SuppressWarnings("unused")
		ControlloreConfigurazioneOffline ccoff=new ControlloreConfigurazioneOffline(vista);
		primaryStage.setScene(vista.getScene());
		//ccoff.aspettaCreazionePartita();
	}

	public void mostraVistaPartitaCaricata(String salvataggio) {
		VistaGioco vista = new VistaGioco(this);
		@SuppressWarnings("deprecation")
		ControlloreGioco controllore = new ControlloreGioco(vista, new VistaSpettatore(this), cs); // Passo la vista al controller
		controllore.caricaPartita(salvataggio);
		//primaryStage.setScene(vista.getScene());
		controllore.avviaPartita();
	}
	
	public void mostraVistaPartitaCaricataWithDb(String nomeSalvataggio, String partitaSerializzata) {
		VistaGioco vista = new VistaGioco(this);
		@SuppressWarnings("deprecation")
		ControlloreGioco controllore = new ControlloreGioco(vista, new VistaSpettatore(this), cs); // Passo la vista al controller
		controllore.caricaPartitaWithDb(nomeSalvataggio, partitaSerializzata);
		//primaryStage.setScene(vista.getScene());
		controllore.avviaPartita();
	}

	public void mostraVistaPartitaNuova(int numGiocatori) {
		VistaGioco vista = new VistaGioco(this);
		@SuppressWarnings("deprecation")
		ControlloreGioco controllore = new ControlloreGioco(vista, new VistaSpettatore(this), cs); 
		// se si vuole giocatori tra persone umane toglie il "vsBot"
		controllore.configuraNuovaPartitaVsBot(numGiocatori);
		//primaryStage.setScene(vista.getScene());
		controllore.avviaPartita();
	}
	
	//SEZIONE SET SCENA PARTITA (vale per vistaGioco e vistaSpettatore)
	public void aggiornaVistaPartita(VistaPartita vp) {
		primaryStage.setScene(vp.getScene());
	}

}
