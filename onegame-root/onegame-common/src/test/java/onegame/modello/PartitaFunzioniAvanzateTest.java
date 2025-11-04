package onegame.modello;

import java.util.ArrayList;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import onegame.modello.Mossa.TipoMossa;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.giocatori.Giocatore;
import onegame.modello.carte.Colore;
import onegame.modello.carte.TipoSpeciale;

public class PartitaFunzioniAvanzateTest {
	int numGiocatori = 2;
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

		// mazzo svuotato, per quando si vuole far pescare forzatamente una carta
		// specifica
		partita.getMazzo().getCarte().clear();
	}

	@Test //test1.15
	public void testGiocaCartaCompatibile() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getGiocatoreCorrente().aggiungiCarta(new CartaNumero(Colore.ROSSO, 0));
		Mossa giocata = new Mossa(TipoMossa.GIOCA_CARTA, partita.getGiocatoreCorrente().getMano().getCarte().get(0));
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("Giocata non valida", giocata, risultato);
		assertEquals("Carta non è stata tolta dalla mano", 0, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

	@Test //test1.16
	public void testGiocaCartaNonCompatibile() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getGiocatoreCorrente().aggiungiCarta(new CartaNumero(Colore.BLU, 0));
		Mossa giocata = new Mossa(TipoMossa.GIOCA_CARTA, partita.getGiocatoreCorrente().getMano().getCarte().get(0));
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("Giocata valida", null, risultato);
		assertEquals("Carta è stata tolta dalla mano", 1, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

	@Test //test1.17
	public void testGiocaCartaNera() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getGiocatoreCorrente().aggiungiCarta(new CartaSpeciale(Colore.NERO, TipoSpeciale.PIU_QUATTRO));
		Mossa giocata = new Mossa(TipoMossa.GIOCA_CARTA, partita.getGiocatoreCorrente().getMano().getCarte().get(0));
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("Giocata valida", giocata, risultato);
		assertEquals("Carta è stata tolta dalla mano", 1, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

	@Test //test1.18
	public void testGiocaCartaNeraConCambioColore() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getGiocatoreCorrente().aggiungiCarta(new CartaSpeciale(Colore.NERO, TipoSpeciale.PIU_QUATTRO));
		Mossa giocata = new Mossa(TipoMossa.GIOCA_CARTA, partita.getGiocatoreCorrente().getMano().getCarte().get(0));
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("Giocata valida", giocata, risultato);
		assertEquals("Carta è stata tolta dalla mano", 1, partita.getGiocatoreCorrente().getMano().getNumCarte());

		// cambio tipo mossa e colore carta (simula comportamento dentro il controllore)
		risultato.setTipoMossa(TipoMossa.SCEGLI_COLORE);
		risultato.getCartaScelta().setColore(Colore.BLU);
		Mossa risultato2 = partita.applicaMossa(partita.getGiocatoreCorrente(), risultato);
		assertEquals("Giocata valida", risultato, risultato2);
		assertEquals("Carta non è stata tolta dalla mano", 0, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

	@Test //test1.19
	public void testPescaCartaGiocabile() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getMazzo().getCarte().add(new CartaNumero(Colore.ROSSO, 4));
		Mossa giocata = new Mossa(TipoMossa.PESCA);
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("La carta pescata si può giocare", giocata, risultato);
		assertEquals("La carta pescata giocabile non ha cambiato tipo mosso", TipoMossa.GIOCA_CARTA,
				risultato.getTipoMossa());
		assertEquals("Carta non è stata messa nella mano", 1, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

	@Test //test1.20
	public void testPescaCartaNonGiocabile() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getMazzo().getCarte().add(new CartaNumero(Colore.BLU, 4));
		Mossa giocata = new Mossa(TipoMossa.PESCA);
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("La carta pescata si può giocare", null, risultato);
		assertEquals("Carta non è stata messa nella mano", 1, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

	@Test //test1.21
	public void testPescaCartaGiocabileEGiocala() {
		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
		partita.getMazzo().getCarte().add(new CartaNumero(Colore.ROSSO, 4));
		Mossa giocata = new Mossa(TipoMossa.PESCA);
		Mossa risultato = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata);
		assertEquals("La carta pescata si può giocare", giocata, risultato);
		assertEquals("La carta pescata giocabile non ha cambiato tipo mosso", TipoMossa.GIOCA_CARTA,
				risultato.getTipoMossa());
		assertEquals("Carta non è stata messa nella mano", 1, partita.getGiocatoreCorrente().getMano().getNumCarte());

		// si vuole giocare la carta
		Mossa giocata2 = risultato;
		assertNotEquals("La carta della mossa non è stata settata", null, giocata2.getCartaScelta());
		Mossa risultato2 = partita.applicaMossa(partita.getGiocatoreCorrente(), giocata2);
		assertEquals("Giocata non valida", giocata2, risultato2);
		assertEquals("Carta non è stata tolta dalla mano", 0, partita.getGiocatoreCorrente().getMano().getNumCarte());
	}

//	@Test //test1.22
//	public void testApplicaEffettoCarta() {
//		// ripopolo il mazzo
//		partita.getMazzo().inizializzaNuovoMazzo();
//		// inizializzo il pre partita (per fornire 7 carte ad ogni giocatore)
//		partita.eseguiPrePartita();
//		// cambio artificialmente la carta corrente
//		partita.setCartaCorrente(new CartaNumero(Colore.ROSSO, 3));
//		// fornisco artificialmente al giocatore attuale un piu_due rosso (quindi in
//		// posizione 7 della sua mano)
//		Carta daGiocare = new CartaSpeciale(Colore.ROSSO, TipoSpeciale.PIU_DUE);
//		partita.getGiocatoreCorrente().aggiungiCarta(daGiocare);
//		Mossa mossa = new Mossa(TipoMossa.GIOCA_CARTA, daGiocare);
//		partita.applicaMossa(partita.getGiocatoreCorrente(), mossa);
//		partita.passaTurno();
//		assertEquals("Il giocatore#1 non ha pescato carte", 9, giocatori.get(1).getMano().getNumCarte());
//		assertEquals("Il giocatore#0 non è il giocatore corrente", partita.getGiocatoreCorrente(), giocatori.get(0));
//		assertEquals("Il giocatore#0 non ha 7 carte, partendo da 8", 7, giocatori.get(0).getMano().getNumCarte());
//	}

	@Test //test1.23
	public void testGiocoInAutomatico() {
		// ripopolo il mazzo
		partita.getMazzo().inizializzaNuovoMazzo();
		// inizializzo il pre partita (per fornire 7 carte ad ogni giocatore)
		partita.eseguiPrePartita();
		while(!partita.verificaFinePartita()) {
			partita.scegliMossaAutomatica();
			partita.passaTurno();
		}
		assertNotEquals("Non è presente un vincitore",null,partita.getVincitore());
	}

}
