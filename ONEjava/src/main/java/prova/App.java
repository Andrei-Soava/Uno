package prova;

import java.util.ArrayList;

import org.apache.logging.log4j.*;

import modello.*;
import modello.giocatori.*;

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
        
        ArrayList<Giocatore> players=new ArrayList<>(); 
        Giocatore a=new GiocatoreAnonimo("Andrei");
        Giocatore b=new GiocatoreAnonimo("Barbabr");
        Giocatore c=new GiocatoreAnonimo("Cilecca");
        Giocatore d=new GiocatoreAnonimo("Mateo");
        players.add(a);
        players.add(b);
        players.add(c);
        players.add(d);
        Partita p=new Partita(players);
        //p.avvia();
        Mazzo m=new Mazzo();
        PilaScarti ps=new PilaScarti();
        m.setPila(ps);
        System.out.println(m+","+ps);
        ps.getCarte().addAll(m.pescaN(107));
        System.out.println(m+","+ps);
        ps.mettiCarta(m.pesca());
        System.out.println(m+","+ps);
        ps.mettiCarta(m.pesca());
        System.out.println(m+","+ps);
        //m.ricostruisciMazzo();
        
	}
}
