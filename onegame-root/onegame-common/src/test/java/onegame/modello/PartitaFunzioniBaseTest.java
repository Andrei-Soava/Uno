package onegame.modello;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import onegame.modello.giocatori.Giocatore;

public class PartitaFunzioniBaseTest {
	int numGiocatori = 4;
	Partita partita;
	ArrayList<Giocatore> giocatori;

	/*
	 * @BeforeClass public static void setUpBeforeClass() throws Exception { }
	 * 
	 * @AfterClass public static void tearDownAfterClass() throws Exception { }
	 */

	@Before
	public void setUp() throws Exception {
		giocatori = new ArrayList<Giocatore>();
		for (int i = 0; i < numGiocatori; i++) {
			giocatori.add(new Giocatore("Giocatore #" + i));
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
	}
	
	@Test //test1.8
	public void testNumeroGiocatori() {
		assertEquals("Numero di giocatori incoerente", numGiocatori, partita.getGiocatori().size());
		
		String str = partita.toJson();
		Partita partita2 = Partita.fromJson(str);
	}
	
	@Test //test1.9
	public void testPrimoGiocatore() {
		assertEquals("Non è il primo giocatore", giocatori.get(0), partita.getGiocatoreCorrente());
	}
	
	@Test //test1.10
	public void testEseguiUnTurnoOrario() {
		partita.eseguiUnTurno();
		assertEquals("Non è cambiato il giocatore", giocatori.get(1), partita.getGiocatoreCorrente());
	}
	
	@Test //test1.11
	public void testVerificaDirezione() {
		assertTrue("Non è senso orario", partita.isDirezione());
		partita.cambiaDirezione();
		assertFalse("Non è senso antiorario", partita.isDirezione());
	}
	
	@Test //test1.12
	public void testEseguiUnTurnoAntiOrario() {
		partita.cambiaDirezione();
		partita.eseguiUnTurno();
		assertEquals("Non è cambiato il giocatore", giocatori.get(3), partita.getGiocatoreCorrente());
	}
	
	@Test //test1.13
	public void testVediProssimoGiocatore() {
		Giocatore prossimoTurno = partita.vediProssimoGiocatore(); 
		assertNotEquals("Il giocatore corrente è stato modificato", partita.getGiocatoreCorrente(), prossimoTurno);
	}
	
	@Test //test1.14
	public void testEffettoAttivato() {
		assertTrue("L'effetto della carta iniziale non è true", partita.isEffettoAttivato());
	}
	

}
