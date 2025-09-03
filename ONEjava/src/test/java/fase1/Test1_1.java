package fase1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import modello.Mazzo;
import modello.carte.Carta;
import modello.carte.CartaNumero;
import modello.carte.Colore;

/**
 * testa se la prima carta del mazzo dopo il mescolamento è la stessa di prima che venga letto il file mazzo (0 rosso)
 */
public class Test1_1 {
	private Mazzo m;

    @Before
    public void setUp() {
        m = new Mazzo();
    }

    @Test
    public void testaMescolamento() {
        Carta c = m.pesca();
        assertEquals("Colore non corrisponde", Colore.ROSSO, c.getColore());

        if (c instanceof CartaNumero) {
            assertEquals("Numero non corrisponde", 0, ((CartaNumero) c).getNumero());
        }
    }
}
