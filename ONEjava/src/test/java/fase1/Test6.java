package fase1;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import modello.Partita;
import modello.giocatori.Giocatore;
import modello.giocatori.GiocatoreAnonimo;

/**
 * testa se, dopo aver invocato il metodo prossimoGiocatore(), il giocatore corrente cambia
 */
public class Test6 {
	private Partita p;

    @Before
    public void setUp() {
    	ArrayList<Giocatore> players=new ArrayList<>(); 
        Giocatore a=new GiocatoreAnonimo("Andrei");
        Giocatore b=new GiocatoreAnonimo("Barbabr");
        Giocatore c=new GiocatoreAnonimo("Cilecca");
        Giocatore d=new GiocatoreAnonimo("Mateo");
        players.add(a);
        players.add(b);
        players.add(c);
        players.add(d);
        p=new Partita(players);
    }

    @Test
    public void testaProssimoGiocatore() {
    	Giocatore p1=p.getGiocatoreCorrente();
    	p.prossimoGiocatore();
    	assertNotEquals("Giocatore non è cambiato",p1,p.getGiocatoreCorrente());
        
    }
}
