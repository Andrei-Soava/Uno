package onegame.modello;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import onegame.modello.Partita;
import onegame.modello.giocatori.Giocatore;

public class PartitaTest {
	int numGiocatori = 4;
	Partita partita;

	/*
	 * @BeforeClass public static void setUpBeforeClass() throws Exception { }
	 * 
	 * @AfterClass public static void tearDownAfterClass() throws Exception { }
	 */

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void test() {
		ArrayList<Giocatore> giocatori = new ArrayList<Giocatore>();
		for (int i = 0; i < numGiocatori; i++) {
			giocatori.add(new Giocatore("Giocatore #" + i));
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
		
		assertEquals("Numero di giocatori incoerente", numGiocatori, partita.getGiocatori().size());
		
		partita.getGiocatoreCorrente().scegliMossaAutomatica();
	}

}
