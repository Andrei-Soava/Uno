package onegame.server.gioco;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.carte.Colore;
import onegame.server.PartitaObserver;
import onegame.server.StanzaPartita;
import onegame.server.eccezioni.CartaNonPossedutaException;
import onegame.server.eccezioni.ColoreNonValidoException;
import onegame.server.eccezioni.EccezionePartita;
import onegame.server.eccezioni.MossaNonValidaException;
import onegame.server.eccezioni.PartitaGiaFinitaException;
import onegame.server.eccezioni.TurnoNonValidoException;

public final class PartitaNET {
	private final List<GiocatoreNET> giocatori = new ArrayList<>();
	private final MazzoNET mazzo;
	private CartaNET cartaCorrente;
	private Colore coloreCorrente;
	private boolean direzioneCrescente = true;
	private int currentPlayerIndex = 0;

	private List<PartitaObserver> observers = new ArrayList<PartitaObserver>();

	private boolean partitaFinita = false;
	private int indiceVincitore = -1;

	// stato del turno corrente
	private boolean haPescatoNelTurno = false;
	private CartaNET cartaPescataCorrente = null;

	private final int TEMPO_DICHIARAZIONE_UNO = 3000; // ms
	private final int TEMPO_DI_GUARDIA = 3000; // ms
	private final int TEMPO_MAX_MOSSA = 10000; // ms

	// gestione timer
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	// Timer attivo
	private ScheduledFuture<?> timer = null;

	private static final Logger logger = LoggerFactory.getLogger(PartitaNET.class);

	private ReentrantLock lock = new ReentrantLock();

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
	}

	public List<GiocatoreNET> getGiocatori() {
		return Collections.unmodifiableList(giocatori);
	}

	public GiocatoreNET getCurrentPlayer() {
		return giocatori.get(currentPlayerIndex);
	}

	public CartaNET getCartaCorrente() {
		return cartaCorrente;
	}

	public Colore getColoreCorrente() {
		return coloreCorrente;
	}

	public int getIndiceGiocatoreCorrente() {
		return currentPlayerIndex;
	}

	public int getNumeroGiocatori() {
		return this.giocatori.size();
	}

	public void giocaCarta(CartaNET cartaGiocata, Colore coloreScelto, GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();

			// verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer())) {
				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
				throw new TurnoNonValidoException();
			}

			// verifica che il giocatore possieda la carta
			if (!giocatore.hasCarta(cartaGiocata)) {
				logger.warn("Il giocatore {} non possiede la carta giocata", giocatore.getNickname());
				throw new CartaNonPossedutaException();
			}

			// Se ha appena pescato, può giocare al più la carta pescata
			if (haPescatoNelTurno && cartaPescataCorrente != null && !cartaGiocata.equals(cartaPescataCorrente)) {
				logger.warn("Il giocatore {} ha pescato e può giocare solo la carta pescata", giocatore.getNickname());
				throw new MossaNonValidaException();
			}

			// Se è una carta nera, deve essere scelto il colore della carta
			if (cartaGiocata.getColore() == Colore.NERO) {
				if (coloreScelto == null || coloreScelto == Colore.NERO) {
					logger.error("Colore scelto non valido per carta nera");
					throw new ColoreNonValidoException();
				} else {
					this.coloreCorrente = coloreScelto;
				}
			} else {
				this.coloreCorrente = cartaGiocata.getColore();
			}

			// Verifica se la carta sia giocabile
			if (!this.isCartaGiocabile(cartaGiocata)) {
				logger.warn("La carta giocata da {} non è valida", giocatore.getNickname());
				throw new MossaNonValidaException();
			}

			// rimuove la carta dalla mano e la mette negli scarti
			giocatore.rimuoviCarta(cartaGiocata);
			this.cartaCorrente = cartaGiocata;

			// applica l'effetto della carta giocata
			List<CartaNET> cartePescate = applicaEffettoCarta(cartaGiocata);

			// Regola dell'UNO: se ha una sola carta, deve dichiarare UNO
			if (giocatore.getNumeroCarte() == 1) {
				avviaTimerDichiaraUNO(giocatore);
				logger.info("Il giocatore {} deve dichiarare UNO!", giocatore.getNickname());
			} else {
				giocatore.setHaDichiaratoUNO(false);
			}

			checkWinCondition(giocatore);
			// prima controlla che la partita non sia finita, e se non lo è passa il turno al prossimo giocatore
			if (!partitaFinita) {
				passaTurno(1);
			}
//		notifyObserverspartitaAggiornata(Map.of(giocatore, cartePescate));
			notifyObserverspartitaAggiornata(Map.of());
			logger.info("Il giocatore {} ha giocato la carta {}", giocatore.getNickname(), cartaGiocata.toString());
		} finally {
			lock.unlock();
		}
	}

	public void pesca(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();

			// verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer())) {
				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
				throw new TurnoNonValidoException();
			}

			// Impedisce la doppia pescata
			if (haPescatoNelTurno) {
				logger.warn("Il giocatore {} ha già pescato in questo turno", giocatore.getNickname());
				throw new MossaNonValidaException();
			}

			CartaNET cartaPescata = mazzo.pesca();
			if (cartaPescata != null) {
				giocatore.aggiungiCarta(cartaPescata);
				logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNickname());
			} else {
				logger.info("Il mazzo è vuoto", giocatore.getNickname());
			}

			if (cartaPescata != null) {
				cartaPescataCorrente = cartaPescata;
				haPescatoNelTurno = true;
				giocatore.setHaDichiaratoUNO(false);
				logger.info("Il giocatore {} ha pescato una carta", giocatore.getNickname());
			}
			avviaTimerTurno();

			notifyObserverspartitaAggiornata(Map.of(giocatore, List.of(cartaPescata)));
		} finally {
			lock.unlock();
		}
	}

	public void passa(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();

			// verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer())) {
				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
				throw new TurnoNonValidoException();
			}

			// Non può passare se non ha pescato né giocato
			if (!haPescatoNelTurno) {
				logger.warn("Il giocatore {} non può passare senza aver pescato", giocatore.getNickname());
				throw new MossaNonValidaException();
			}
			passaTurno(1);
			avviaTimerTurno();

			notifyObserverspartitaAggiornata(Map.of());
		} finally {
			lock.unlock();
		}
	}

	public void dichiaraUno(GiocatoreNET giocatore) throws EccezionePartita {
		lock.lock();
		try {
			verificaPartitaFinita();

//		// verifica il turno
//		if (!Objects.equals(giocatore, getCurrentPlayer())) {
//			logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
//			throw new TurnoNonValidoException();
//		}

			if (giocatore.getNumeroCarte() == 1) {
				giocatore.setHaDichiaratoUNO(true);
				cancellaTimer();
				logger.info("Il giocatore {} ha dichiarato UNO!", giocatore.getNickname());
			} else {
				logger.warn("Il giocatore {} non può dichiarare UNO con {} carte", giocatore.getNickname(),
						giocatore.getNumeroCarte());
				throw new MossaNonValidaException();
			}
		} finally {
			lock.unlock();
		}
	}

	private void verificaPartitaFinita() throws EccezionePartita {
		if (partitaFinita) {
			logger.warn("Partita già finita");
			throw new PartitaGiaFinitaException();
		}
	}

	private void effettuaMossaAutomatica() throws EccezionePartita {
		lock.lock();
		try {
			GiocatoreNET g = getCurrentPlayer();
			for (CartaNET c : g.getMano()) {
				if (isCartaGiocabile(c)) {
					giocaCarta(c, this.coloreCorrente, g);
				}
			}
			if (!haPescatoNelTurno) {
				pesca(g);
			}
			passa(g);
		} finally {
			lock.unlock();
		}
	}

	private List<CartaNET> applicaEffettoCarta(CartaNET carta) {
		if (!carta.isCartaNumero) {
			switch (carta.getTipo()) {
			case BLOCCA:
				passaTurno(1);
				return List.of();
			case INVERTI:
				if (giocatori.size() == 2) {
					// con 2 giocatori, l'inverti funziona come il blocca
					passaTurno(1);
					return List.of();
				}
				cambiaDirezione();
				return List.of();
			case PIU_DUE:
				passaTurno(1);
				GiocatoreNET prossimoGiocatore = getCurrentPlayer();
				List<CartaNET> cartePescate = mazzo.pescaN(2);
				prossimoGiocatore.aggiungiCarte(cartePescate);
				return cartePescate;
			case JOLLY:
				// il colore è già stato cambiato in effettuaMossa
				return List.of();
			case PIU_QUATTRO:
				// il colore è già stato cambiato in effettuaMossa
				passaTurno(1);
				GiocatoreNET prossimo = getCurrentPlayer();
				List<CartaNET> cartePescate4 = mazzo.pescaN(4);
				prossimo.aggiungiCarte(cartePescate4);
				return cartePescate4;
			default:
				return List.of();
			}
		} else {
			// carta numero, nessun effetto speciale
			return List.of();
		}
	}

	private boolean isCartaGiocabile(CartaNET carta) {
		if (carta.getColore() == Colore.NERO) {
			return true;
		}
		if (carta.getColore() == this.coloreCorrente) {
			return true;
		}
		if (carta.isCartaNumero() && carta.getNumero() == this.cartaCorrente.getNumero()) {
			return true;
		}
		if (!carta.isCartaNumero() && !this.cartaCorrente.isCartaNumero()
				&& carta.getTipo() == this.cartaCorrente.getTipo()) {
			return true;
		}
		return false;
	}

	private void cancellaTimer() {
		if (timer != null) {
			timer.cancel(false);
			timer = null;
		}
	}

	private void avviaTimerDichiaraUNO(GiocatoreNET giocatore) {
		cancellaTimer();
		timer = scheduler.schedule(() -> {
			lock.lock();
			try {
				if (!giocatore.haDichiaratoUNO()) {
					List<CartaNET> cartePescate = mazzo.pescaN(2);
					giocatore.aggiungiCarte(cartePescate);
					logger.info("Il giocatore {} non ha dichiarato UNO in tempo e pesca 2 carte",
							giocatore.getNickname());
					notifyObserverspartitaAggiornata(Map.of(giocatore, cartePescate));
				}
			} finally {
				lock.unlock();
			}
		}, TEMPO_DICHIARAZIONE_UNO + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
	}

	private void avviaTimerTurno() {
		cancellaTimer();
		timer = scheduler.schedule(() -> {
			logger.info("Il giocatore {} non ha effettuato la mossa in tempo", getCurrentPlayer().getNickname());
			try {
				effettuaMossaAutomatica();
			} catch (EccezionePartita e) {
				logger.error("Errore nell'esecuzione della mossa automatica");
			}
		}, TEMPO_MAX_MOSSA + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
	}

	private void passaTurno(int step) {
		haPescatoNelTurno = false;
		cartaPescataCorrente = null;

		int dir = direzioneCrescente ? 1 : -1;
		currentPlayerIndex = Math.floorMod(currentPlayerIndex + dir * step, giocatori.size());
	}

	private void checkWinCondition(GiocatoreNET giocatore) {
		if (giocatore.getNumeroCarte() == 0 && !partitaFinita) {
			partitaFinita = true;
			indiceVincitore = giocatori.indexOf(giocatore);
		}
		return;
	}

	public boolean isFinished() {
		return partitaFinita;
	}

	public GiocatoreNET getGiocatoreCorrente() {
		return getCurrentPlayer();
	}

	public int getIndiceVincitore() {
		return indiceVincitore;
	}

	private void cambiaDirezione() {
		direzioneCrescente = !direzioneCrescente;
	}

	public boolean getDirezione() {
		return direzioneCrescente;
	}

	public boolean addObserver(PartitaObserver ob) {
		return observers.add(ob);
	}

	public boolean removeObserver(PartitaObserver ob) {
		return observers.remove(ob);
	}

	private void notifyObserverspartitaAggiornata(Map<GiocatoreNET, List<CartaNET>> cartePescate) {
		for (PartitaObserver ob : observers) {
			ob.partitaAggiornata(cartePescate);
		}
	}

}
