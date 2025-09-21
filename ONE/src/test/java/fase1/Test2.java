package fase1;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import modello.Mazzo;
import modello.carte.Carta;

/**
 * testa se, dopo aver pescato una carta da un nuovo mazzo (108 carte):
 * 1) la carta restituita non è null
 * 2) il numero di carte è 107
 */
public class Test2 {
	private Mazzo m;

    @Before
    public void setUp() {
        m = new Mazzo();
    }

    @Test
    public void testaPescaggio() {
        Carta c=m.pesca();
        assertNotNull("Non è stata restituita una carta",c);
        assertSame("Non è diminuito in numero di carte", 107, m.getNumeroCarte());
    }
}
