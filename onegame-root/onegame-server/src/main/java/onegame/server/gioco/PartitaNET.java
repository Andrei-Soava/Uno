package onegame.server.gioco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.carte.Colore;
import onegame.modello.carte.TipoSpeciale;
import onegame.server.StanzaPartita;
import onegame.server.eccezioni.EccezionePartita;
import onegame.server.eccezioni.MossaNonValidaException;
import onegame.server.eccezioni.MossaNonValidaException.TipoMossaNonValida;
import onegame.server.eccezioni.PartitaGiaFinitaException;

/**
 * Rappresenta una partita di UNO gestita dal server.
 */
public class PartitaNET {
	private final List<GiocatoreNET> giocatori = new ArrayList<>();
	private final MazzoNET mazzo;
	private CartaNET cartaCorrente;
	private Colore coloreCorrente;
	private boolean direzioneCrescente = true;
	private int indiceGiocatoreCorrente = 0;
	private int indiceGiocatoreCorrenteMostrato = 0;

	private List<PartitaObserver> observers = new ArrayList<PartitaObserver>();

	private boolean partitaFinita = false;
	private int indiceVincitore = -1;

	// stato del turno corrente
	private boolean haPescatoNelTurno = false;
	private boolean haGiocatoNelTurno = false;
	private CartaNET cartaPescataCorrente = null;

	private final int TEMPO_DICHIARAZIONE_UNO = 2000; // ms
	private final int TEMPO_DI_GUARDIA = 4000; // ms
	private final int TEMPO_MAX_MOSSA = 8000; // ms
	private final int TEMPO_MOSSA_BOT = 3000; // ms

	// gestione timer
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	// Timer attivo
	private ScheduledFuture<?> timer = null;

	private static final Logger logger = LoggerFactory.getLogger(PartitaNET.class);

	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Crea una nuova partita con i giocatori specificati
	 * @param giocatori la lista dei giocatori partecipanti
	 * @param stanza la stanza di appartenenza della partita
	 * @param mazzoFactory la fabbrica per creare il mazzo di carte
	 */
	public PartitaNET(List<GiocatoreNET> giocatori, StanzaPartita stanza, MazzoFactory mazzoFactory) {
		this.giocatori.addAll(giocatori);
		this.mazzo = new MazzoNET(mazzoFactory);
		init();
	}

	private void init() {
		// Distribuisce 7 carte a ogni giocatore e inizializza pila scarti con prima carta valida
		for (GiocatoreNET g : giocatori) {
			g.aggiungiCarte(mazzo.pescaN(7));
		}

		CartaNET cartaIniziale;
		do {
			cartaIniziale = mazzo.pesca();
		} while (cartaIniziale.getColore() == Colore.NERO);
		this.cartaCorrente = cartaIniziale;
		this.coloreCorrente = cartaIniziale.getColore();
		avviaTimerTurno();
	}

	/**
	 * Passa il turno al prossimo giocatore
	 */
	private void passaTurno() {
		int dir = direzioneCrescente ? 1 : -1;
		indiceGiocatoreCorrente = Math.floorMod(indiceGiocatoreCorrente + dir, giocatori.size());
	}

	private void passaTurnoGiocatoreSuccessivo() {
		haPescatoNelTurno = false;
		haGiocatoNelTurno = false;
		cartaPescataCorrente = null;

		passaTurno();
		avviaTimerTurno();
		aggiornaGiocatoreCorrenteMostrato();
	}

	/**
	 * Esegue la mossa di giocare una carta
	 * @param cartaGiocata la carta giocata
	 * @param coloreScelto il colore scelto (se la carta è nera)
	 * @param giocatore il giocatore che effettua la mossa
	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
	 */
	public void giocaCarta(CartaNET cartaGiocata, Colore coloreScelto, GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();
			verificaTurno(giocatore);

			// verifica che il giocatore possieda la carta
			if (!giocatore.hasCarta(cartaGiocata)) {
				logger.warn("Il giocatore {} non possiede la carta giocata", giocatore.getNickname());
				throw new MossaNonValidaException(TipoMossaNonValida.CARTA_NON_POSSEDUTA);
			}

			if (haGiocatoNelTurno) {
				logger.warn("Il giocatore {} ha già giocato in questo turno", giocatore.getNickname());
				throw new MossaNonValidaException(TipoMossaNonValida.GIOCATORE_HA_GIA_GIOCATO);
			}

			// Se ha appena pescato, può giocare al più la carta pescata
			if (haPescatoNelTurno && cartaPescataCorrente != null && !cartaGiocata.equals(cartaPescataCorrente)) {
				logger.warn("Il giocatore {} ha pescato e può giocare solo la carta pescata", giocatore.getNickname());
				throw new MossaNonValidaException(TipoMossaNonValida.CARTA_GIOCATA_NON_VALIDA_DOPO_PESCA);
			}

			// Se è una carta nera, deve essere scelto il colore della carta
			if (cartaGiocata.getColore() == Colore.NERO) {
				if (coloreScelto == null || coloreScelto == Colore.NERO) {
					logger.error("Colore scelto non valido per carta nera");
					throw new MossaNonValidaException(TipoMossaNonValida.COLORE_SCELTO_NON_VALIDO);
				}
			}

			// Verifica se la carta sia giocabile
			if (!this.isCartaGiocabile(cartaGiocata)) {
				logger.warn("Carta {} giocata da {} non valida", cartaGiocata, giocatore.getNickname());
				throw new MossaNonValidaException(TipoMossaNonValida.CARTA_NON_GIOCABILE);
			}

			// Qui la carta è valida, viene giocata
			giocatore.rimuoviCarta(cartaGiocata);
			this.cartaCorrente = cartaGiocata;
			haGiocatoNelTurno = true;

			if (cartaGiocata.getColore() == Colore.NERO) {
				this.coloreCorrente = coloreScelto;
			} else {
				this.coloreCorrente = cartaGiocata.getColore();
			}

			checkWinCondition(giocatore);

			if (!partitaFinita) {
				applicaEffettoCarta(cartaGiocata);

				// Regola dell'UNO: se ha una sola carta, deve dichiarare UNO
				if (giocatore.getNumeroCarte() == 1) {
					avviaTimerDichiaraUNO(giocatore);
					logger.info("Il giocatore {} deve dichiarare UNO!", giocatore.getNickname());
				} else {
					giocatore.setHaDichiaratoUNO(false);

					passaTurnoGiocatoreSuccessivo();
				}
			}

			notifyObserverspartitaAggiornata(null);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Esegue la mossa di pescare una carta
	 * @param giocatore il giocatore che effettua la mossa
	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
	 */
	public void pesca(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();
			verificaTurno(giocatore);

			// Impedisce la doppia pescata
			if (haPescatoNelTurno) {
				logger.warn("Il giocatore {} ha già pescato in questo turno", giocatore.getNickname());
				throw new MossaNonValidaException(TipoMossaNonValida.GIOCATORE_HA_GIA_PESCATO);
			}

			// Qui il giocatore può pescare
			CartaNET cartaPescata = mazzo.pesca();
			if (cartaPescata != null) {
				giocatore.aggiungiCarta(cartaPescata);
				cartaPescataCorrente = cartaPescata;
				logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNickname());
			} else {
				logger.info("Il mazzo è vuoto", giocatore.getNickname());
			}
			haPescatoNelTurno = true;
			giocatore.setHaDichiaratoUNO(false);

			avviaTimerTurno();
			aggiornaGiocatoreCorrenteMostrato();

			notifyObserverspartitaAggiornata(cartaPescata);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Esegue la mossa di passare il turno
	 * @param giocatore il giocatore che effettua la mossa
	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
	 */
	public void passa(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();
			verificaTurno(giocatore);

			// Non può passare se non ha pescato né giocato
			if (!haPescatoNelTurno) {
				logger.warn("Il giocatore {} non può passare senza aver pescato", giocatore.getNickname());
				throw new MossaNonValidaException(TipoMossaNonValida.GIOCATORE_DEVE_PESCARE);
			}
			// Qui il giocatore può passare

			passaTurnoGiocatoreSuccessivo();

			notifyObserverspartitaAggiornata(null);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Esegue la mossa di pescare una carta e passare il turno
	 * @param giocatore il giocatore che effettua la mossa
	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
	 */
	public void pescaEPassa(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();
			verificaTurno(giocatore);

			if (!haPescatoNelTurno) {
				CartaNET cartaPescata = mazzo.pesca();
				if (cartaPescata != null) {
					giocatore.aggiungiCarta(cartaPescata);
					logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNickname());
				} else {
					logger.info("Il mazzo è vuoto", giocatore.getNickname());
				}

				cartaPescataCorrente = null;
			}
			passaTurnoGiocatoreSuccessivo();

			notifyObserverspartitaAggiornata(null);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Effettua una mossa automatica
	 * @param giocatore il giocatore che effettua la mossa
	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
	 */
	private void effettuaMossaAutomatica(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();
			verificaTurno(giocatore);

			// Logica semplice per i bot: se possono giocare, giocano la prima carta valida
			for (CartaNET carta : giocatore.getMano()) {
				if (isCartaGiocabile(carta)) {
					giocaCarta(carta, carta.getColore() == Colore.NERO ? Colore.scegliColoreCasuale() : null, giocatore);
					return;
				}
			}

			// Se non possono giocare, pescano e passano
			pescaEPassa(giocatore);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Esegue la mossa di dichiarare UNO
	 * @param giocatore il giocatore che effettua la mossa
	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
	 */
	public void dichiaraUno(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();

			if (giocatore.getNumeroCarte() == 1) {
				giocatore.setHaDichiaratoUNO(true);
				cancellaTimer();
				if (!partitaFinita) {
					passaTurnoGiocatoreSuccessivo();
					notifyObserverspartitaAggiornata(null);
				}
				logger.info("Il giocatore {} ha dichiarato UNO!", giocatore.getNickname());
			} else {
				logger.warn("Il giocatore {} non può dichiarare UNO con {} carte", giocatore.getNickname(),
						giocatore.getNumeroCarte());
				throw new MossaNonValidaException(TipoMossaNonValida.DICHIARAZIONE_UNO_NON_VALIDA);
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Applica l'effetto della carta giocata
	 * @param carta la carta giocata
	 */
	private void applicaEffettoCarta(CartaNET carta) {
		if (!carta.isCartaNumero) {
			switch (carta.getTipo()) {
			case BLOCCA:
				passaTurno();
				break;
			case INVERTI:
				if (giocatori.size() == 2) {
					// con 2 giocatori, l'inverti funziona come il blocca
					passaTurno();
				}
				cambiaDirezione();
				break;
			case PIU_DUE:
				passaTurno();
				GiocatoreNET prossimoGiocatore = getGiocatoreCorrente();
				prossimoGiocatore.aggiungiCarte(mazzo.pescaN(2));
				break;
			case JOLLY:
				// il colore è già stato cambiato in effettuaMossa
				break;
			case PIU_QUATTRO:
				// il colore è già stato cambiato in effettuaMossa
				passaTurno();
				GiocatoreNET prossimo = getGiocatoreCorrente();
				List<CartaNET> cartePescate4 = mazzo.pescaN(4);
				prossimo.aggiungiCarte(cartePescate4);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Verifica se la carta può essere giocata in base alle regole del gioco
	 * @param carta la carta da verificare
	 * @return true se la carta è giocabile, false altrimenti
	 */
	private boolean isCartaGiocabile(CartaNET carta) {
		// verifica se la carta è un +4 e se il giocatore può giocarlo
		if (!carta.isCartaNumero && carta.getTipo() == TipoSpeciale.PIU_QUATTRO) {
			GiocatoreNET giocatoreCorrente = getGiocatoreCorrente();
			return verificaPiuQuattroBluff(giocatoreCorrente);
		}
		return carta.isCartaCompatibile(getColoreCorrente(), cartaCorrente);
	}

	/**
	 * Verifica se il giocatore può giocare un +4 in base alle regole del gioco
	 * @param g il giocatore da verificare
	 * @return true se il giocatore può giocare un +4, false altrimenti
	 */
	private boolean verificaPiuQuattroBluff(GiocatoreNET g) {
		if (!giocatori.contains(g)) {
			return false;
		} else {
			ArrayList<CartaNET> carteInMano = new ArrayList<>();
			carteInMano.addAll(g.getMano());
			// istruzione in cui vengono rimossi tutti i +4 dalla mano di una giocatore per fare controlli
			carteInMano.removeIf(carta -> !carta.isCartaNumero && carta.getTipo() == TipoSpeciale.PIU_QUATTRO);

			// ciclo di verifica possibilità di giocare altre carte oltre ai +4
			for (CartaNET carta : carteInMano) {
				if (carta.isCartaCompatibile(getColoreCorrente(), getCartaCorrente())) {
					logger.debug("Verifica bluff +4: non valido, carta in mano: {}", carta);
					return false;
				}
			}
			// si arriva qui solo se nessuna delle carte in mano OLTRE ai +4 è giocabile
			logger.debug("Verifica bluff +4: valido");
			return true;
		}
	}

	/**
	 * Cambia la direzione del gioco
	 */
	private void cambiaDirezione() {
		direzioneCrescente = !direzioneCrescente;
	}

	public boolean getDirezione() {
		return direzioneCrescente;
	}

	public int getIndiceGiocatoreCorrenteMostrato() {
		return indiceGiocatoreCorrenteMostrato;
	}

	public int getNumeroGiocatori() {
		return this.giocatori.size();
	}

	public CartaNET getCartaCorrente() {
		return cartaCorrente;
	}

	public List<GiocatoreNET> getGiocatori() {
		return Collections.unmodifiableList(giocatori);
	}

	/**
	 * Restituisce il giocatore corrente
	 * @return il giocatore corrente
	 */
	private GiocatoreNET getGiocatoreCorrente() {
		return giocatori.get(indiceGiocatoreCorrente);
	}

	public Colore getColoreCorrente() {
		return coloreCorrente;
	}

	public int getIndiceVincitore() {
		return indiceVincitore;
	}

	public boolean isFinished() {
		return partitaFinita;
	}

	/**
	 * Restituisce il giocatore corrente da mostrare nel client
	 * @return il giocatore corrente mostrato
	 */
	public GiocatoreNET getGiocatoreCorrenteMostrato() {
		return giocatori.get(indiceGiocatoreCorrenteMostrato);
	}

	private void verificaPartitaFinita() throws EccezionePartita {
		if (partitaFinita) {
			logger.warn("Partita già finita");
			throw new PartitaGiaFinitaException();
		}
	}

	/**
	 * Restituisce il giocatore vincitore della partita, o null se la partita non è ancora finita
	 * @return il giocatore vincitore, o null se la partita non è finita
	 */
	public GiocatoreNET getVincitore() {
		if (indiceVincitore >= 0 && indiceVincitore < giocatori.size()) {
			return giocatori.get(indiceVincitore);
		}
		return null;
	}

	/**
	 * Verifica se è il turno del giocatore
	 * @param g il giocatore da verificare
	 * @throws EccezionePartita se non è il turno del giocatore
	 */
	private void verificaTurno(GiocatoreNET g) throws EccezionePartita {
		if (!Objects.equals(g, getGiocatoreCorrente())) {
			logger.warn("Non è il turno del giocatore {}", g.getNickname());
			throw new MossaNonValidaException(TipoMossaNonValida.GIOCATORE_NON_TURNO);
		}
	}

	private void checkWinCondition(GiocatoreNET giocatore) {
		if (giocatore.getNumeroCarte() == 0 && !partitaFinita) {
			partitaFinita = true;
			indiceVincitore = giocatori.indexOf(giocatore);
		}
		return;
	}

	private void aggiornaGiocatoreCorrenteMostrato() {
		indiceGiocatoreCorrenteMostrato = indiceGiocatoreCorrente;
	}

	// Sezione timers

	/**
	 * Avvia il timer per il turno del giocatore corrente
	 */
	private void avviaTimerTurno() {
		cancellaTimer();
		if (getGiocatoreCorrente().isBot()) {
			// Timer per bot
			timer = scheduler.schedule(() -> {
				logger.info("Esecuzione mossa automatica per il bot {}", getGiocatoreCorrente().getNickname());
				try {
					effettuaMossaAutomatica(getGiocatoreCorrente());
				} catch (EccezionePartita e) {
					logger.error("Errore nell'esecuzione della mossa automatica del bot", e);
				}
			}, TEMPO_MOSSA_BOT, TimeUnit.MILLISECONDS);
		} else {
			// Timer per giocatore umano
			timer = scheduler.schedule(() -> {
				logger.info("Il giocatore {} non ha effettuato la mossa in tempo",
						getGiocatoreCorrente().getNickname());
				try {
					pescaEPassa(getGiocatoreCorrente());
				} catch (EccezionePartita e) {
					logger.error("Errore nell'esecuzione della mossa automatica");
				}
			}, TEMPO_MAX_MOSSA + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Avvia il timer per la dichiarazione di UNO
	 * @param giocatore il giocatore che deve dichiarare UNO
	 */
	private void avviaTimerDichiaraUNO(GiocatoreNET giocatore) {
		cancellaTimer();
		if (giocatore.isBot()) {
			// I bot dichiarano sempre UNO immediatamente
			timer = scheduler.schedule(() -> {
				lock.lock();
				try {
					giocatore.setHaDichiaratoUNO(true);
					logger.info("Il bot {} ha dichiarato UNO automaticamente", giocatore.getNickname());
					if (!partitaFinita) {
						passaTurnoGiocatoreSuccessivo();

						notifyObserverspartitaAggiornata(null);
					}
				} catch (Exception e) {
					logger.error("Errore durante la dichiarazione automatica di UNO del bot", e);
				} finally {
					lock.unlock();
				}
			}, 800, TimeUnit.MILLISECONDS);
			return;
		} else {
			// Timer dichiara UNO per giocatore umano
			timer = scheduler.schedule(() -> {
				lock.lock();
				try {
					if (!giocatore.haDichiaratoUNO()) {
						verificaPartitaFinita();
						giocatore.aggiungiCarte(mazzo.pescaN(2));
						logger.info("Il giocatore {} non ha dichiarato UNO in tempo e pesca 2 carte",
								giocatore.getNickname());
					}
					if (!partitaFinita) {
						passaTurnoGiocatoreSuccessivo();

						notifyObserverspartitaAggiornata(null);
					}
				} catch (Exception e) {
					logger.error("Errore durante il timer di dichiarazione UNO", e);
				} finally {
					lock.unlock();
				}
			}, TEMPO_DICHIARAZIONE_UNO + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
		}
	}

	private void cancellaTimer() {
		if (timer != null) {
			timer.cancel(false);
			timer = null;
		}
	}

	// Sezione observers

	public boolean addObserver(PartitaObserver ob) {
		return observers.add(ob);
	}

	public boolean removeObserver(PartitaObserver ob) {
		return observers.remove(ob);
	}

	/**
	 * Notifica gli osservatori che la partita è stata aggiornata
	 * @param cartePescate le carte pescate dai giocatori durante l'ultimo aggiornamento
	 */
	private void notifyObserverspartitaAggiornata(CartaNET cartaPescata) {
		for (PartitaObserver ob : observers) {
			ob.partitaAggiornata(cartaPescata);
		}
	}

	/**
	 * Interrompe la partita in corso
	 */
	public void interrompiPartita() {
		lock.lock();
		try {
			partitaFinita = true;
			cancellaTimer();
			notifyObserverspartitaAggiornata(null);
		} finally {
			lock.unlock();
		}
	}

}