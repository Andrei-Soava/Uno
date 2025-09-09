package prova;

import org.apache.logging.log4j.*;

import controllore.ControlloreGioco;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
//import vista.VistaIniziale;


/**
 * Hello world!
 *
 */
public class AppWithMaven extends Application {
	private static Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		logger.info("Non mostrato di default");
		logger.warn("Non mostrato di default");
		logger.error("MOSTRATO DI DEFAULT");
		System.out.println(System.getProperty("java.version"));
		
		ControlloreGioco tc = new ControlloreGioco();

		/*
		VistaIniziale vista = new VistaIniziale(primaryStage);

		// Mostro la schermata iniziale con due pulsanti
		vista.mostraSceltaIniziale("Scegli cosa fare:", "Caricare partita", "Nuova partita", scelta -> {
			if (scelta == 1) {
				tc.configuraNuovaPartitaVsBot();
				tc.avviaPartita2();
			} else {
				tc.caricaPartita();
				tc.avviaPartita2();
			}
		});
		*/
	}
}
