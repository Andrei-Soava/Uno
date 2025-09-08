package fase1;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import modello.Mazzo;
import modello.PilaScarti;

/**
 * testa se, dopo che il mazzo si svuota, il mazzo viene ricostruito dopo che si tenta di pescare da esso
 */
public class Test7 {
	private Mazzo m;
	private PilaScarti ps;

    @Before
    public void setUp() {
    	m=new Mazzo();
        ps=new PilaScarti();
        m.setPila(ps);
    }

    @Test
    public void testaRicostruzioneMazzo() {
        ps.getScarti().addAll(m.pescaN(108));
        assertSame("Mazzo non vuoto",0,m.getNumeroCarte());
        ps.mettiCarta(m.pesca());
        assertSame("Mazzo non ricostruito",107,m.getNumeroCarte());
        
    }
}
