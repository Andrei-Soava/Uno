package modello;

import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import modello.carte.Carta;


public class MazzoTest {
	private Mazzo mazzo;
	private Random random = new Random();
	
	@Before
    public void setUp() {
        mazzo = new Mazzo();
        mazzo.inizializzaNuovoMazzo();
    }

	@Test
    public void testInizializzaMazzo() {
        Carta c = mazzo.pesca();
        assertNotNull(c);
    }
	
	@Test
	public void testSvuotaMazzo() {
        assertFalse("Il mazzo è vuoto, ma dovrebbe essere pieno", mazzo.isVuoto());
		mazzo.pescaN(mazzo.getNumeroCarte());
		assertTrue("Il mazzo non è stato svuotato", mazzo.isVuoto());
	}
	
	@Test
	public void testPescaUnaCarta() {
		int count1 = mazzo.getNumeroCarte();
		mazzo.pesca();
		int count2 = mazzo.getNumeroCarte();
		assertTrue("Il pescaggio non funziona", count1 == count2 + 1);
	}
	
	@Test
	public void testPescaNCarte() {
		int count1 = mazzo.getNumeroCarte();
		int numeroCarteDaPescare = random.nextInt(count1);
		mazzo.pescaN(numeroCarteDaPescare);
		int count2 = mazzo.getNumeroCarte();
		assertTrue("Il pescaggio non funziona", count1 == count2 + numeroCarteDaPescare);
	}
}
