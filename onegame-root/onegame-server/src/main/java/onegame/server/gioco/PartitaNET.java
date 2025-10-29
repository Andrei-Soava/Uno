package onegame.server.gioco;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.Mazzo;
import onegame.modello.PartitaIF;
import onegame.modello.PilaScarti;
import onegame.modello.carte.Carta;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.DTOUtils;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.MossaDTO.TipoMossa;
import onegame.server.PartitaObserver;
import onegame.server.StanzaPartita;

public final class PartitaNET implements PartitaIF {
	private final List<Giocatore> giocatori = new ArrayList<>();
	private final Mazzo mazzo;
	private final PilaScarti pilaScarti = new PilaScarti();
	private Colore coloreCorrente;
	private boolean direzioneCrescente = true;
	private int currentPlayerIndex = 0;

	private List<PartitaObserver> observers = new ArrayList<PartitaObserver>();

	private boolean partitaFinita = false;
	private int indiceVincitore = -1;

	// stato del turno corrente
	private boolean haPescatoNelTurno = false;
	private Carta cartaPescataCorrente = null;

	private final int TEMPO_DICHIARAZIONE_UNO = 3000; // ms
	private final int TEMPO_DI_GUARDIA = 3000; // ms
	private final int TEMPO_MAX_MOSSA = 14000; // ms

	// gestione timer
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	// Timer attivo
	private ScheduledFuture<?> timerUNO = null;
	private ScheduledFuture<?> timerTurno = null;

	private static final Logger logger = LoggerFactory.getLogger(PartitaNET.class);

	private ReentrantLock lock = new ReentrantLock();
	private Semaphore mutexDichiaraUNO = new Semaphore(1);

	public PartitaNET(List<Giocatore> giocatori, StanzaPartita stanza) {
		this.giocatori.addAll(giocatori);
		this.mazzo = new Mazzo();
		this.mazzo.inizializzaNuovoMazzo();
		init();
	}

	private void init() {
		// Distribuisce 7 carte a ogni giocatore e inizializza pila scarti con prima carta valida
		for (Giocatore g : giocatori) {
			g.getMano().aggiungiCarte(mazzo.pescaN(7));
		}

		Carta cartaIniziale;
		do {
			cartaIniziale = mazzo.pesca();
			pilaScarti.mettiCarta(cartaIniziale);
		} while (cartaIniziale.getColore() == Colore.NERO);
		this.coloreCorrente = cartaIniziale.getColore();
	}

	public List<Giocatore> getGiocatori() {
		return Collections.unmodifiableList(giocatori);
	}

	public Giocatore getCurrentPlayer() {
		return giocatori.get(currentPlayerIndex);
	}

	public Carta getCartaCorrente() {
		return pilaScarti.getTop();
	}

	public Colore getColoreCorrente() {
		return coloreCorrente;
	}

	public int getIndiceGiocatoreCorrente() {
		return currentPlayerIndex;
	}

	@Override
	public int getNumeroGiocatori() {
		return this.giocatori.size();
	}

	public boolean effettuaMossa(MossaDTO mossa, Giocatore giocatore) {
		lock.lock();
		try {
			if (mossa == null || giocatore == null || partitaFinita) {
				logger.warn("Mossa o giocatore nulli, o partita già finita");
				return false;
			}
			if (mossa.tipo != TipoMossa.DICHIARA_UNO) {
				mutexDichiaraUNO.acquireUninterruptibly();
				mutexDichiaraUNO.release();
			}
			// verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer())) {
				logger.warn("Non è il turno del giocatore {}", giocatore.getNome());
				return false;
			}

			switch (mossa.tipo) {
			case GIOCA_CARTA: {
				Carta cartaGiocata = DTOUtils.convertiDTOinCarta(mossa.carta);
				if (cartaGiocata == null) {
					return false;
				}

				// verifica che il giocatore possieda la carta
				if (!giocatore.getMano().contieneCarta(cartaGiocata)) {
					logger.warn("Il giocatore {} non possiede la carta giocata", giocatore.getNome());
					return false;
				}

				// Se ha appena pescato, può giocare al più la carta pescata
				if (haPescatoNelTurno && cartaPescataCorrente != null && !cartaGiocata.equals(cartaPescataCorrente)) {
					logger.warn("Il giocatore {} ha pescato e può giocare solo la carta pescata", giocatore.getNome());
					return false;
				}

				Carta cartaTop = getCartaCorrente();

				// Se è una carta nera, deve essere scelto il colore della carta
				if (cartaGiocata.getColore() == Colore.NERO) {
					if (mossa.coloreScelto == null || mossa.coloreScelto == Colore.NERO) {
						logger.error("Colore scelto non valido per carta nera");
						return false;
					} else {
						this.coloreCorrente = mossa.coloreScelto;
					}
				} else {
					this.coloreCorrente = cartaGiocata.getColore();
				}

				// Verifica se la carta sia giocabile
				if (!this.isCartaGiocabile(cartaGiocata)) {
					logger.warn("La carta giocata da {} non è valida", giocatore.getNome());
					return false;
				}

				// rimuove la carta dalla mano e la mette negli scarti
				giocatore.getMano().rimuoviCarta(cartaGiocata);
				pilaScarti.mettiCarta(cartaGiocata);

				// applica l'effetto della carta giocata
				List<Carta> cartePescate = cartaGiocata.applicaEffetto(this);

				// Regola dell'UNO: se ha una sola carta, deve dichiarare UNO
				if (giocatore.getMano().getNumCarte() == 1) {
					avviaTimerDichiaraUNO(giocatore);
				} else {
					giocatore.setHaDichiaratoUNO(false);
					cancellaTimerDichiaraUNO();
				}
				cancellaTimerTurno();

				checkWinCondition(giocatore);
				// prima controlla che la partita non sia finita, e se non lo è passa il turno al prossimo giocatore
				if (!partitaFinita) {
					passaTurno(1);
				}
//				notifyObserverspartitaAggiornata(Map.of(giocatore, cartePescate));
				notifyObserverspartitaAggiornata(Map.of());
				logger.info("Il giocatore {} ha giocato la carta {}", giocatore.getNome(), cartaGiocata.toString());

				return true;
			}

			case PESCA: {
				// Impedisce la doppia pescata
				if (haPescatoNelTurno) {
					logger.warn("Il giocatore {} ha già pescato in questo turno", giocatore.getNome());
					return false;
				}

				Carta cartaPescata = pesca(giocatore);
				if (cartaPescata != null) {
					cartaPescataCorrente = cartaPescata;
					haPescatoNelTurno = true;
					giocatore.setHaDichiaratoUNO(false);
					logger.info("Il giocatore {} ha pescato una carta", giocatore.getNome());
				}
				cancellaTimerTurno();
				avviaTimerTurno();

				notifyObserverspartitaAggiornata(Map.of(giocatore, List.of(cartaPescata)));
				return true;
			}

			case PASSA: {
				// Non può passare se non ha pescato né giocato
				if (!haPescatoNelTurno) {
					logger.warn("Il giocatore {} non può passare senza aver pescato", giocatore.getNome());
					return false;
				}
				cancellaTimerTurno();
				passaTurno(1);

				notifyObserverspartitaAggiornata(Map.of());
				return true;
			}
			case DICHIARA_UNO: {
				if (giocatore.getMano().getNumCarte() == 1) {
					giocatore.setHaDichiaratoUNO(true);
					cancellaTimerDichiaraUNO();
					mutexDichiaraUNO.release();
					logger.info("Il giocatore {} ha dichiarato UNO!", giocatore.getNome());
					return true;
				}
				return false;
			}
			case PESCA_E_PASSA: {
				if (!haPescatoNelTurno) {
					pesca(giocatore);
				} else {
					logger.warn("Il giocatore {} non può pescare di nuovo prima di passare", giocatore.getNome());
				}

				passaTurno(1);

				notifyObserverspartitaAggiornata(Map.of());
				return true;
			}
			}
			return false;
		} catch (Exception e) {
			logger.error("Errore durante l'effettuazione della mossa: {}", e);
			return false;
		} finally {
			lock.unlock();
		}
	}

	private void effettuaMossaAutomatica() {
		Giocatore g = getCurrentPlayer();
		for (Carta c : g.getMano().getCarte()) {
			if (isCartaGiocabile(c)) {
				MossaDTO mossa = new MossaDTO(TipoMossa.GIOCA_CARTA);
				mossa.carta = DTOUtils.convertiCartaInDTO(c);
				if (c.getColore() == Colore.NERO) {
					mossa.coloreScelto = Colore.ROSSO; // o scegli dinamicamente
				}
				effettuaMossa(mossa, g);
				return;
			}
		}
		effettuaMossa(new MossaDTO(TipoMossa.PESCA_E_PASSA), g);
	}

	private void cancellaTimerDichiaraUNO() {
		if (timerUNO != null) {
			timerUNO.cancel(false);
			timerUNO = null;
		}
	}

	private void cancellaTimerTurno() {
		if (timerTurno != null) {
			timerTurno.cancel(false);
			timerTurno = null;
		}
	}

	private Carta pesca(Giocatore giocatore) {
		Carta pescata = mazzo.pesca();
		if (pescata != null) {
			giocatore.getMano().aggiungiCarta(pescata);
			logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNome());
		} else {
			logger.info("Il mazzo è vuoto", giocatore.getNome());
		}
		return pescata;
	}

	private void avviaTimerDichiaraUNO(Giocatore giocatore) {
		cancellaTimerDichiaraUNO();
		mutexDichiaraUNO.acquireUninterruptibly();
		timerUNO = scheduler.schedule(() -> {
			lock.lock();
			try {
				if (!giocatore.haDichiaratoUNO()) {
					List<Carta> cartePescate = mazzo.pescaN(2);
					giocatore.getMano().aggiungiCarte(cartePescate);
					logger.info("Il giocatore {} non ha dichiarato UNO in tempo e pesca 2 carte", giocatore.getNome());
					notifyObserverspartitaAggiornata(Map.of(giocatore, cartePescate));
					mutexDichiaraUNO.release();
				}
			} finally {
				lock.unlock();
			}
		}, TEMPO_DICHIARAZIONE_UNO + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
	}

	private void avviaTimerTurno() {
		cancellaTimerTurno();
		timerTurno = scheduler.schedule(() -> {
			logger.info("Il giocatore {} non ha effettuato la mossa in tempo", getCurrentPlayer().getNome());
			effettuaMossaAutomatica();
		}, TEMPO_MAX_MOSSA + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
	}

	private void passaTurno(int step) {
		avviaTimerTurno();

		haPescatoNelTurno = false;
		cartaPescataCorrente = null;

		int dir = direzioneCrescente ? 1 : -1;
		currentPlayerIndex = Math.floorMod(currentPlayerIndex + dir * step, giocatori.size());
	}

	@Override
	public void prossimoGiocatore() {
		passaTurno(1);
	}

	private void checkWinCondition(Giocatore giocatore) {
		if (giocatore.getMano().getNumCarte() == 0 && !partitaFinita) {
			partitaFinita = true;
			indiceVincitore = giocatori.indexOf(giocatore);
		}
		return;
	}

	public boolean isFinished() {
		return partitaFinita;
	}

	@Override
	public Giocatore getGiocatoreCorrente() {
		return getCurrentPlayer();
	}

	public int getIndiceVincitore() {
		return indiceVincitore;
	}

	@Override
	public Mazzo getMazzo() {
		return mazzo;
	}

	@Override
	public void cambiaDirezione() {
		direzioneCrescente = !direzioneCrescente;
	}

	@Override
	public boolean getDirezione() {
		return direzioneCrescente;
	}

	public boolean addObserver(PartitaObserver ob) {
		return observers.add(ob);
	}

	public boolean removeObserver(PartitaObserver ob) {
		return observers.remove(ob);
	}

	private void notifyObserverspartitaAggiornata(Map<Giocatore, List<Carta>> cartePescate) {
		for (PartitaObserver ob : observers) {
			ob.partitaAggiornata(cartePescate);
		}
	}

}
