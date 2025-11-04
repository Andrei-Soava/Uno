package onegame.modello;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import onegame.modello.giocatori.Giocatore;
import onegame.net.util.JsonHelper;

public class PartitaSerializzazioneTest {
	private Partita partita;
	private static Random random;
	private static int numGiocatori;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		random = new Random();
		numGiocatori = random.nextInt(2, 5);
	}

	@Before
	public void setUp() throws Exception {
		List<Giocatore> giocatori = new ArrayList<Giocatore>();
		for (int i = 0; i < numGiocatori; i++) {
			giocatori.add(new Giocatore("Giocatore #" + i));
		}
		partita = new Partita(giocatori);
		partita.eseguiPrePartita();
		
	}
	
	// Incompleto
	@Test
	public void verificaCoerenzaSerializzazione() {
		int numMosse = random.nextInt(20);
		for (int i = 0; i < numMosse; i++) {
			if (partita.verificaFinePartita()) {
				break;
			}
		}
		String str = JsonHelper.toJson(partita);
		Partita partita2 = JsonHelper.fromJson(str, Partita.class);
		
	}

}
