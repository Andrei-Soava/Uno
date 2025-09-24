package onegame.modello;

import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import onegame.modello.Mazzo;
import onegame.modello.carte.Carta;


public class MazzoTest {
	private Mazzo mazzo;
	private PilaScarti pila;
	private Random random = new Random();
	
	@Before
    public void setUp() {
        mazzo = new Mazzo();
        mazzo.inizializzaNuovoMazzo();
        pila = new PilaScarti();
        mazzo.setPila(pila);
    }

	@Test //test1.1
    public void testInizializzaMazzo() {
        Carta c = mazzo.pesca();
        assertNotNull(c);
    }
	
	@Test //test1.2
	public void testSvuotaMazzo() {
        assertFalse("Il mazzo è vuoto, ma dovrebbe essere pieno", mazzo.isVuoto());
		mazzo.pescaN(mazzo.getNumeroCarte());
		assertTrue("Il mazzo non è stato svuotato", mazzo.isVuoto());
	}
	
	@Test //test1.3
	public void testPescaUnaCarta() {
		int count1 = mazzo.getNumeroCarte();
		mazzo.pesca();
		int count2 = mazzo.getNumeroCarte();
		assertEquals("Il pescaggio non funziona", count1-1, count2);
	}
	
	@Test //test1.4
	public void testPescaNCarte() {
		int count1 = mazzo.getNumeroCarte();
		int numeroCarteDaPescare = random.nextInt(count1);
		mazzo.pescaN(numeroCarteDaPescare);
		int count2 = mazzo.getNumeroCarte();
		assertEquals("Il pescaggio non funziona", count1-numeroCarteDaPescare, count2);
	}
	
	/**
	 * Dato un mazzo nuovo, test se prima carta pescata cambia dopo averla rimessa nel mazzo 
	 * ed averlo rimescolato
	 *
	 *N.B. il test può fallire, ma con 1/108 di probabilità
	 */
	@Test //test1.5
	public void testPrimaCarta() {
		Carta prima1=mazzo.pesca();
		mazzo.getCarte().add(prima1);
		mazzo.mescola();
		Carta prima2=mazzo.pesca();
		assertNotEquals("Le carte sono uguali", prima1, prima2);
	}
	
	@Test //test1.6
	public void testPilaScartiNonVuota() {
		pila.getScarti().add(mazzo.pesca());
		assertEquals("La pila non si è riempita", 1, pila.getScarti().size());
	}
	
	@Test //test1.7
	public void testRicostruzioneMazzo() {
		pila.getScarti().addAll(mazzo.pescaN(mazzo.getNumeroCarte()));
		mazzo.pesca();
		assertEquals("Il mazzo non si è ricostruito", 107, mazzo.getNumeroCarte());
		assertEquals("La pila non è vuota", 0, pila.getScarti().size());
	}
}
