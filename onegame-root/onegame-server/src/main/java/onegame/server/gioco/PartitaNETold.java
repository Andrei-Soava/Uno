//package onegame.server.gioco;
//
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.ReentrantLock;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
//import onegame.modello.carte.Colore;
//import onegame.server.StanzaPartita;
//import onegame.server.eccezioni.CartaNonPossedutaException;
//import onegame.server.eccezioni.ColoreNonValidoException;
//import onegame.server.eccezioni.EccezionePartita;
//import onegame.server.eccezioni.MossaNonValidaException;
//import onegame.server.eccezioni.PartitaGiaFinitaException;
//import onegame.server.eccezioni.TurnoNonValidoException;
//
///**
// * Gestisce lo stato di una partita di Uno tra più giocatori
// */
//public final class PartitaNETold {
//	private final List<GiocatoreNET> giocatori = new ArrayList<>();
//	private final MazzoNET mazzo;
//	private CartaNET cartaCorrente;
//	private Colore coloreCorrente;
//	private boolean direzioneCrescente = true;
//	private int currentPlayerIndex = 0;
//
//	private List<PartitaObserver> observers = new ArrayList<PartitaObserver>();
//
//	private boolean partitaFinita = false;
//	private int indiceVincitore = -1;
//
//	// stato del turno corrente
//	private boolean haPescatoNelTurno = false;
//	private boolean haGiocatoNelTurno = false;
//	private CartaNET cartaPescataCorrente = null;
//
//	private final int TEMPO_DICHIARAZIONE_UNO = 2000; // ms
//	private final int TEMPO_DI_GUARDIA = 3000; // ms
//	private final int TEMPO_MAX_MOSSA = 8000; // ms
//
//	// gestione timer
//	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//	// Timer attivo
//	private ScheduledFuture<?> timer = null;
//
//	private static final Logger logger = LoggerFactory.getLogger(PartitaNETold.class);
//
//	private ReentrantLock lock = new ReentrantLock();
//
//	/**
//	 * Crea una nuova partita con i giocatori specificati
//	 * @param giocatori la lista dei giocatori partecipanti
//	 * @param stanza la stanza di appartenenza della partita
//	 * @param mazzoFactory la fabbrica per creare il mazzo di carte
//	 */
//	public PartitaNETold(List<GiocatoreNET> giocatori, StanzaPartita stanza, MazzoFactory mazzoFactory) {
//		this.giocatori.addAll(giocatori);
//		this.mazzo = new MazzoNET(mazzoFactory);
//		init();
//	}
//
//	private void init() {
//		// Distribuisce 7 carte a ogni giocatore e inizializza pila scarti con prima carta valida
//		for (GiocatoreNET g : giocatori) {
//			g.aggiungiCarte(mazzo.pescaN(7));
//		}
//
//		CartaNET cartaIniziale;
//		do {
//			cartaIniziale = mazzo.pesca();
//		} while (cartaIniziale.getColore() == Colore.NERO);
//		this.cartaCorrente = cartaIniziale;
//		this.coloreCorrente = cartaIniziale.getColore();
//	}
//
//	public List<GiocatoreNET> getGiocatori() {
//		return Collections.unmodifiableList(giocatori);
//	}
//
//	public GiocatoreNET getCurrentPlayer() {
//		return giocatori.get(currentPlayerIndex);
//	}
//
//	public CartaNET getCartaCorrente() {
//		return cartaCorrente;
//	}
//
//	public Colore getColoreCorrente() {
//		return coloreCorrente;
//	}
//
//	public int getIndiceGiocatoreCorrente() {
//		return currentPlayerIndex;
//	}
//
//	public int getNumeroGiocatori() {
//		return this.giocatori.size();
//	}
//
//	/**
//	 * Esegue la mossa di giocare una carta
//	 * @param cartaGiocata la carta da giocare
//	 * @param coloreScelto il colore scelto (se la carta è nera)
//	 * @param giocatore il giocatore che effettua la mossa
//	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
//	 */
//	public void giocaCarta(CartaNET cartaGiocata, Colore coloreScelto, GiocatoreNET giocatore) throws EccezionePartita {
//		lock.lock();
//		try {
//			verificaPartitaFinita();
//
//			// verifica il turno
//			if (!Objects.equals(giocatore, getCurrentPlayer())) {
//				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
//				throw new TurnoNonValidoException();
//			}
//
//			// verifica che il giocatore possieda la carta
//			if (!giocatore.hasCarta(cartaGiocata)) {
//				logger.warn("Il giocatore {} non possiede la carta giocata", giocatore.getNickname());
//				throw new CartaNonPossedutaException();
//			}
//
//			if (haGiocatoNelTurno) {
//				logger.warn("Il giocatore {} ha già giocato in questo turno", giocatore.getNickname());
//				throw new MossaNonValidaException();
//			}
//
//			// Se ha appena pescato, può giocare al più la carta pescata
//			if (haPescatoNelTurno && cartaPescataCorrente != null && !cartaGiocata.equals(cartaPescataCorrente)) {
//				logger.warn("Il giocatore {} ha pescato e può giocare solo la carta pescata", giocatore.getNickname());
//				throw new MossaNonValidaException();
//			}
//
//			// Se è una carta nera, deve essere scelto il colore della carta
//			if (cartaGiocata.getColore() == Colore.NERO) {
//				if (coloreScelto == null || coloreScelto == Colore.NERO) {
//					logger.error("Colore scelto non valido per carta nera");
//					throw new ColoreNonValidoException();
//				} else {
//					this.coloreCorrente = coloreScelto;
//				}
//			} else {
//				this.coloreCorrente = cartaGiocata.getColore();
//			}
//
//			// Verifica se la carta sia giocabile
//			if (!this.isCartaGiocabile(cartaGiocata)) {
//				logger.warn("Carta giocata da {} non valida", giocatore.getNickname());
//				throw new MossaNonValidaException();
//			}
//
//			// rimuove la carta dalla mano e la mette negli scarti
//			giocatore.rimuoviCarta(cartaGiocata);
//			this.cartaCorrente = cartaGiocata;
//
////			// applica l'effetto della carta giocata
//			applicaEffettoCarta(cartaGiocata);
//			this.haGiocatoNelTurno = true;
//
//			// Regola dell'UNO: se ha una sola carta, deve dichiarare UNO
//			if (giocatore.getNumeroCarte() == 1) {
//				avviaTimerDichiaraUNO(giocatore);
//				logger.info("Il giocatore {} deve dichiarare UNO!", giocatore.getNickname());
//			} else {
//				giocatore.setHaDichiaratoUNO(false);
//				if (!partitaFinita) {
//					passaTurno(1);
//					avviaTimerTurno();
//				}
//			}
//
//			checkWinCondition(giocatore);
//
//			// prima controlla che la partita non sia finita, e se non lo è passa il turno al prossimo giocatore
//
//			notifyObserverspartitaAggiornata(Map.of());
//			logger.info("Il giocatore {} ha giocato la carta {}", giocatore.getNickname(), cartaGiocata.toString());
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	/**
//	 * Esegue la mossa di pescare una carta
//	 * @param giocatore il giocatore che effettua la mossa
//	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
//	 */
//	public void pesca(GiocatoreNET giocatore) throws EccezionePartita {
//		lock.lock();
//		try {
//			verificaPartitaFinita();
//
//			// verifica il turno
//			if (!Objects.equals(giocatore, getCurrentPlayer())) {
//				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
//				throw new TurnoNonValidoException();
//			}
//
//			// Impedisce la doppia pescata
//			if (haPescatoNelTurno) {
//				logger.warn("Il giocatore {} ha già pescato in questo turno", giocatore.getNickname());
//				throw new MossaNonValidaException();
//			}
//
//			CartaNET cartaPescata = mazzo.pesca();
//			if (cartaPescata != null) {
//				giocatore.aggiungiCarta(cartaPescata);
//				logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNickname());
//			} else {
//				logger.info("Il mazzo è vuoto", giocatore.getNickname());
//			}
//
//			if (cartaPescata != null) {
//				cartaPescataCorrente = cartaPescata;
//				haPescatoNelTurno = true;
//				giocatore.setHaDichiaratoUNO(false);
//				logger.info("Il giocatore {} ha pescato una carta", giocatore.getNickname());
//			}
//			avviaTimerTurno();
//
//			notifyObserverspartitaAggiornata(Map.of(giocatore, cartaPescata));
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	/**
//	 * Esegue la mossa di passare il turno
//	 * @param giocatore il giocatore che effettua la mossa
//	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
//	 */
//	public void passa(GiocatoreNET giocatore) throws EccezionePartita {
//		lock.lock();
//		try {
//			verificaPartitaFinita();
//
//			// verifica il turno
//			if (!Objects.equals(giocatore, getCurrentPlayer())) {
//				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
//				throw new TurnoNonValidoException();
//			}
//
//			// Non può passare se non ha pescato né giocato
//			if (!haPescatoNelTurno) {
//				logger.warn("Il giocatore {} non può passare senza aver pescato", giocatore.getNickname());
//				throw new MossaNonValidaException();
//			}
//			passaTurno(1);
//			avviaTimerTurno();
//
//			notifyObserverspartitaAggiornata(Map.of());
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	/**
//	 * Esegue la mossa di dichiarare UNO
//	 * @param giocatore il giocatore che effettua la mossa
//	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
//	 */
//	public void dichiaraUno(GiocatoreNET giocatore) throws EccezionePartita {
//		lock.lock();
//		try {
//			verificaPartitaFinita();
//
//			if (giocatore.getNumeroCarte() == 1) {
//				giocatore.setHaDichiaratoUNO(true);
//				cancellaTimer();
//				if (!partitaFinita) {
//					passaTurno(1);
//					avviaTimerTurno();
//				}
//				notifyObserverspartitaAggiornata(Map.of());
//				logger.info("Il giocatore {} ha dichiarato UNO!", giocatore.getNickname());
//			} else {
//				logger.warn("Il giocatore {} non può dichiarare UNO con {} carte", giocatore.getNickname(),
//						giocatore.getNumeroCarte());
//				throw new MossaNonValidaException();
//			}
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	private void verificaPartitaFinita() throws EccezionePartita {
//		if (partitaFinita) {
//			logger.warn("Partita già finita");
//			throw new PartitaGiaFinitaException();
//		}
//	}
//
//	/**
//	 * Esegue la mossa di pescare una carta e passare il turno
//	 * @param giocatore il giocatore che effettua la mossa
//	 * @throws EccezionePartita se la mossa non è valida o la partita è finita
//	 */
//	public void pescaEPassa(GiocatoreNET giocatore) throws EccezionePartita {
//		lock.lock();
//		try {
//			verificaPartitaFinita();
//
//			if (!haPescatoNelTurno) {
//				CartaNET cartaPescata = mazzo.pesca();
//				if (cartaPescata != null) {
//					giocatore.aggiungiCarta(cartaPescata);
//					logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNickname());
//				} else {
//					logger.info("Il mazzo è vuoto", giocatore.getNickname());
//				}
//
//				cartaPescataCorrente = null;
//			}
//			passaTurno(1);
//			avviaTimerTurno();
//			notifyObserverspartitaAggiornata(Map.of());
//		} finally {
//			lock.unlock();
//		}
//	}
//
//	/**
//	 * Applica l'effetto della carta giocata
//	 * @param carta la carta giocata
//	 */
//	private void applicaEffettoCarta(CartaNET carta) {
//		if (!carta.isCartaNumero) {
//			switch (carta.getTipo()) {
//			case BLOCCA:
//				passaTurno(1);
//				break;
//			case INVERTI:
//				if (giocatori.size() == 2) {
//					// con 2 giocatori, l'inverti funziona come il blocca
//					passaTurno(1);
//				}
//				cambiaDirezione();
//				break;
//			case PIU_DUE:
//				passaTurno(1);
//				GiocatoreNET prossimoGiocatore = getCurrentPlayer();
//				prossimoGiocatore.aggiungiCarte(mazzo.pescaN(2));
//				break;
//			case JOLLY:
//				// il colore è già stato cambiato in effettuaMossa
//				break;
//			case PIU_QUATTRO:
//				// il colore è già stato cambiato in effettuaMossa
//				passaTurno(1);
//				GiocatoreNET prossimo = getCurrentPlayer();
//				List<CartaNET> cartePescate4 = mazzo.pescaN(4);
//				prossimo.aggiungiCarte(cartePescate4);
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	/**
//	 * Verifica se la carta può essere giocata in base alle regole del gioco
//	 * @param carta la carta da verificare
//	 * @return true se la carta è giocabile, false altrimenti
//	 */
//	private boolean isCartaGiocabile(CartaNET carta) {
//		// verifica se la carta è un +4 e se il giocatore può giocarlo
//		if (!carta.isCartaNumero && carta.getTipo() == TipoSpeciale.PIU_QUATTRO) {
//			GiocatoreNET giocatoreCorrente = getCurrentPlayer();
//			if (!verificaPiuQuattroBluff(giocatoreCorrente)) {
//				return false;
//			}
//		}
//		return carta.isCartaCompatibile(coloreCorrente, cartaCorrente);
//	}
//
//	/**
//	 * Verifica se il giocatore può giocare un +4 in base alle regole del gioco
//	 * @param g il giocatore da verificare
//	 * @return true se il giocatore può giocare un +4, false altrimenti
//	 */
//	private boolean verificaPiuQuattroBluff(GiocatoreNET g) {
//		if (!getGiocatori().contains(g))
//			return false;
//		else {
//			ArrayList<CartaNET> carteInMano = new ArrayList<>();
//			carteInMano.addAll(g.getMano());
//			// istruzione in cui vengono rimossi tutti i +4 dalla mano di una giocatore per fare controlli
//			carteInMano.removeIf(carta -> !carta.isCartaNumero && carta.getTipo() == TipoSpeciale.PIU_QUATTRO);
//
//			// ciclo di verifica possibilità di giocare altre carte oltre ai +4
//			for (CartaNET carta : carteInMano) {
//				if (isCartaGiocabile(carta))
//					return false;
//			}
//			// si arriva qui solo se nessuna delle carte in mano OLTRE ai +4 è giocabile
//			return true;
//		}
//	}
//
//	private void cancellaTimer() {
//		if (timer != null) {
//			timer.cancel(false);
//			timer = null;
//		}
//	}
//
//	/**
//	 * Avvia il timer per la dichiarazione di UNO
//	 * @param giocatore il giocatore che deve dichiarare UNO
//	 */
//	private void avviaTimerDichiaraUNO(GiocatoreNET giocatore) {
//		cancellaTimer();
//		timer = scheduler.schedule(() -> {
//			lock.lock();
//			try {
//				if (!giocatore.haDichiaratoUNO()) {
//					verificaPartitaFinita();
//					giocatore.aggiungiCarte(mazzo.pescaN(2));
//					logger.info("Il giocatore {} non ha dichiarato UNO in tempo e pesca 2 carte",
//							giocatore.getNickname());
//					notifyObserverspartitaAggiornata(Map.of());
//				}
//				if (!partitaFinita) {
//					passaTurno(1);
//					avviaTimerTurno();
//					notifyObserverspartitaAggiornata(Map.of());
//				}
//			} catch (Exception e) {
//				logger.error("Errore durante il timer di dichiarazione UNO", e);
//			} finally {
//				lock.unlock();
//			}
//		}, TEMPO_DICHIARAZIONE_UNO + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
//	}
//
//	/**
//	 * Avvia il timer per il turno del giocatore corrente
//	 */
//	private void avviaTimerTurno() {
//		cancellaTimer();
//		timer = scheduler.schedule(() -> {
//			logger.info("Il giocatore {} non ha effettuato la mossa in tempo", getCurrentPlayer().getNickname());
//			try {
//				pescaEPassa(getGiocatoreCorrente());
//			} catch (EccezionePartita e) {
//				logger.error("Errore nell'esecuzione della mossa automatica");
//			}
//		}, TEMPO_MAX_MOSSA + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
//	}
//
//	/**
//	 * Passa il turno al prossimo giocatore
//	 * @param step il numero di posizioni da saltare
//	 */
//	private void passaTurno(int step) {
//		haPescatoNelTurno = false;
//		haGiocatoNelTurno = false;
//		cartaPescataCorrente = null;
//
//		int dir = direzioneCrescente ? 1 : -1;
//		currentPlayerIndex = Math.floorMod(currentPlayerIndex + dir * step, giocatori.size());
//	}
//
//	private void checkWinCondition(GiocatoreNET giocatore) {
//		if (giocatore.getNumeroCarte() == 0 && !partitaFinita) {
//			partitaFinita = true;
//			indiceVincitore = giocatori.indexOf(giocatore);
//		}
//		return;
//	}
//
//	public boolean isFinished() {
//		return partitaFinita;
//	}
//
//	public GiocatoreNET getGiocatoreCorrente() {
//		return getCurrentPlayer();
//	}
//
//	public int getIndiceVincitore() {
//		return indiceVincitore;
//	}
//
//	/**
//	 * Restituisce il giocatore vincitore della partita, o null se la partita non è ancora finita
//	 * @return il giocatore vincitore, o null se la partita non è finita
//	 */
//	public GiocatoreNET getVincitore() {
//		if (indiceVincitore >= 0 && indiceVincitore < giocatori.size()) {
//			return giocatori.get(indiceVincitore);
//		}
//		return null;
//	}
//
//	/**
//	 * Cambia la direzione del gioco
//	 */
//	private void cambiaDirezione() {
//		direzioneCrescente = !direzioneCrescente;
//	}
//
//	public boolean getDirezione() {
//		return direzioneCrescente;
//	}
//
//	public boolean addObserver(PartitaObserver ob) {
//		return observers.add(ob);
//	}
//
//	public boolean removeObserver(PartitaObserver ob) {
//		return observers.remove(ob);
//	}
//
//	/**
//	 * Notifica gli osservatori che la partita è stata aggiornata
//	 * @param cartePescate le carte pescate dai giocatori durante l'ultimo aggiornamento
//	 */
//	private void notifyObserverspartitaAggiornata(Map<GiocatoreNET, CartaNET> cartePescate) {
//		for (PartitaObserver ob : observers) {
//			ob.partitaAggiornata(cartePescate);
//		}
//	}
//
//}
