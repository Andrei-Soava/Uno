package modello;

import java.util.Random;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import modello.carte.Carta;
import modello.carte.CartaNumero;
import modello.carte.Colore;


public class MazzoTest {
	private Mazzo mazzo;
	private Random random = new Random();
	
	@Before
    public void setup() {
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
		mazzo.inizializzaNuovoMazzo();
        assertFalse("Il mazzo è vuoto, ma dovrebbe essere pieno", mazzo.isVuoto());
		mazzo.pescaN(mazzo.getNumeroCarte());
		assertTrue("Il mazzo non è stato svuotato", mazzo.isVuoto());
	}
	
	@Test
	public void testPescaUnaCarta() {
		mazzo.inizializzaNuovoMazzo();
		int count1 = mazzo.getNumeroCarte();
		mazzo.pesca();
		int count2 = mazzo.getNumeroCarte();
		assertTrue("Il pescaggio non funziona", count1 == count2 + 1);
	}
	
	@Test
	public void testPescaNCarte() {
		mazzo.inizializzaNuovoMazzo();
		int count1 = mazzo.getNumeroCarte();
		int numeroCarteDaPescare = random.nextInt(count1);
		mazzo.pescaN(numeroCarteDaPescare);
		int count2 = mazzo.getNumeroCarte();
		assertTrue("Il pescaggio non funziona", count1 == count2 + numeroCarteDaPescare);
	}
}
