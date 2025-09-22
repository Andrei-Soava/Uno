package modello;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import modello.carte.Carta;
import modello.carte.CartaNumero;
import modello.carte.Colore;


public class MazzoTest {
	private Mazzo mazzo;
	
	@Before
    public void setup() {
        mazzo = new Mazzo();
    }

	@Test
    public void testaMescolamento() {
        Carta c = mazzo.pesca();
        assertEquals("Colore non corrisponde", Colore.ROSSO, c.getColore());

        if (c instanceof CartaNumero) {
            assertEquals("Numero non corrisponde", 0, ((CartaNumero) c).getNumero());
        }
    }

}
