package onegame.client.esecuzione;

import org.apache.logging.log4j.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import onegame.client.controllore.*;
import onegame.client.controllore.offline.ControlloreGioco;
import onegame.client.controllore.offline.ControlloreMenuOffline;
import onegame.client.controllore.online.ControlloreCodicePartita;
import onegame.client.controllore.online.ControlloreMenuOnline;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
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
	private ClientSocket cs;
	private ConnectionMonitor cm;

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

		primaryStage.setOnCloseRequest(e -> {
			if (cs != null) {
				try {
					cs.disconnect();
					System.out.println("disconnessione avvenuta con successo");
					cm.stop();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
				Platform.exit();   // per JavaFX
				System.exit(0);    // forza la chiusura della JVM
			}

		});
		stage.setTitle("ONE");
		stage.setWidth(900);
		stage.setHeight(600);

		// vista mostrata di default
		mostraVistaAccesso();

		stage.show();
	}

	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	public ClientSocket getCs() {
		return this.cs;
	}

	public void mostraVistaAccesso(String username) {
		VistaAccesso vista = new VistaAccesso(this);
		vista.compilaUsername(username);
		ControlloreAccesso ca = new ControlloreAccesso(vista, cs,cm);
		primaryStage.setScene(vista.getScene());
		ca.eseguiAccesso();
	}

	public void mostraVistaAccesso() {
		this.mostraVistaAccesso("");
	}

	public void mostraVistaRegistrazione() {
		VistaRegistrazione vista = new VistaRegistrazione(this);
		ControlloreRegistrazione cr = new ControlloreRegistrazione(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		cr.eseguiRegistrazione();
	}

	public void mostraVistaHome() {
		VistaHome vista = new VistaHome(this);
		ControlloreHome ch = new ControlloreHome(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		ch.aspettaLogout();
	}

	public void mostraVistaMenuOnline() {
		VistaMenuOnline vista = new VistaMenuOnline(this);
		ControlloreMenuOnline cmo=new ControlloreMenuOnline(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		cmo.aspettaLogout();
	}

	public void mostraVistaConfigurazioneOnline() {
		VistaConfigurazioneOnline vista = new VistaConfigurazioneOnline(this);
		primaryStage.setScene(vista.getScene());
	}

	public void mostraVistaInserimentoCodice() {
		VistaInserimentoCodice vista = new VistaInserimentoCodice(this);
		ControlloreCodicePartita cc = new ControlloreCodicePartita(vista);
		primaryStage.setScene(vista.getScene());
		cc.eseguiAccesso();
	}

	public void mostraVistaMenuOffline() {
		VistaMenuOffline vista = new VistaMenuOffline(this);
		ControlloreMenuOffline cmo=new ControlloreMenuOffline(vista, cs, cm);
		primaryStage.setScene(vista.getScene());
		cmo.aspettaLogout();
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
		vista.cg = controllore;
		controllore.caricaPartita(salvataggio);
		primaryStage.setScene(vista.getScene());
		controllore.avviaPartita();
	}

	public void mostraVistaGiocoNuovo(int numGiocatori) {
		VistaGioco vista = new VistaGioco(this);
		ControlloreGioco controllore = new ControlloreGioco(vista); // passo la vista al controller (serve per
																	// interrompere il gioco)
		vista.cg = controllore;
		// se si vuole giocatori tra persone umane toglie il "vsBot"
		controllore.configuraNuovaPartitaVsBot(numGiocatori);
		primaryStage.setScene(vista.getScene());
		controllore.avviaPartita();
	}

}
