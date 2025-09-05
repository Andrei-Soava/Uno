package prova;

import java.util.ArrayList;

import org.apache.logging.log4j.*;

import controllore.TemporaryController;
import modello.*;
import modello.giocatori.*;
import vista.TemporaryView;

/**
 * Hello world!
 *
 */
public class App {
	private static Logger logger = LogManager.getLogger();

	public static void main(String[] args) {
		logger.info("Non mostrato di default");
        logger.warn("Non mostrato di default");
        logger.error("MOSTRATO DI DEFAULT");
        
        TemporaryController tc=new TemporaryController();
        tc.configuraNuovaPartitaVsBot();
        tc.avviaPartita();
        
        
	}
}
