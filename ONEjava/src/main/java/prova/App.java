package prova;


import org.apache.logging.log4j.*;

import controllore.ControlloreGioco;
import modello.Partita;
import vista.VistaTemporanea;

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
        
        ControlloreGioco tc=new ControlloreGioco();
        VistaTemporanea tv=new VistaTemporanea();
        if(tv.scegliTraDue("Scegli cosa fare:", "caricare partita", "nuova partita")==1) {
        	tc.configuraNuovaPartitaVsBot();
        	tc.avviaPartita();
        }
        else
        {
        	tc.caricaPartita();
        	tc.avviaPartita();
        }
        
        
	}
}
