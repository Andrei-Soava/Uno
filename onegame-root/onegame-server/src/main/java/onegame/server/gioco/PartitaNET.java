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
import onegame.modello.net.DTOUtils;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.MossaDTO.TipoMossa;
import onegame.modello.net.util.JsonHelper;
import onegame.server.PartitaObserver;
import onegame.server.StanzaPartita;
import onegame.server.utils.DTOServerUtils;

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

	public PartitaNET(List<GiocatoreNET> giocatori, StanzaPartita stanza) {
		this.giocatori.addAll(giocatori);
		this.mazzo = new MazzoNET(new MazzoONEFactory());
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

	public boolean effettuaMossa(MossaDTO mossa, GiocatoreNET giocatore) {
		lock.lock();
		try {
			if (mossa == null || giocatore == null || partitaFinita) {
				logger.warn("Mossa o giocatore nulli, o partita già finita");
				return false;
			}
			// verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer())) {
				logger.warn("Non è il turno del giocatore {}", giocatore.getNickname());
				return false;
			}

			switch (mossa.tipo) {
			case GIOCA_CARTA: {
				CartaNET cartaGiocata = DTOServerUtils.fromCartaDTOtoNET(mossa.carta);

				if (cartaGiocata == null) {
					return false;
				}

				// verifica che il giocatore possieda la carta
				if (!giocatore.hasCarta(cartaGiocata)) {
					logger.warn("Il giocatore {} non possiede la carta giocata", giocatore.getNickname());
					return false;
				}

				// Se ha appena pescato, può giocare al più la carta pescata
				if (haPescatoNelTurno && cartaPescataCorrente != null && !cartaGiocata.equals(cartaPescataCorrente)) {
					logger.warn("Il giocatore {} ha pescato e può giocare solo la carta pescata",
							giocatore.getNickname());
					return false;
				}

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
					logger.warn("La carta giocata da {} non è valida", giocatore.getNickname());
					return false;
				}

				// rimuove la carta dalla mano e la mette negli scarti
				giocatore.rimuoviCarta(cartaGiocata);
				this.cartaCorrente = cartaGiocata;

				// applica l'effetto della carta giocata
				List<CartaNET> cartePescate = giocaCarta(cartaGiocata);

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
//				notifyObserverspartitaAggiornata(Map.of(giocatore, cartePescate));
				notifyObserverspartitaAggiornata(Map.of());
				logger.info("Il giocatore {} ha giocato la carta {}", giocatore.getNickname(), cartaGiocata.toString());

				return true;
			}

			case PESCA: {
				// Impedisce la doppia pescata
				if (haPescatoNelTurno) {
					logger.warn("Il giocatore {} ha già pescato in questo turno", giocatore.getNickname());
					return false;
				}

				CartaNET cartaPescata = pesca(giocatore);
				if (cartaPescata != null) {
					cartaPescataCorrente = cartaPescata;
					haPescatoNelTurno = true;
					giocatore.setHaDichiaratoUNO(false);
					logger.info("Il giocatore {} ha pescato una carta", giocatore.getNickname());
				}
				avviaTimerTurno();

				notifyObserverspartitaAggiornata(Map.of(giocatore, List.of(cartaPescata)));
				return true;
			}

			case PASSA: {
				// Non può passare se non ha pescato né giocato
				if (!haPescatoNelTurno) {
					logger.warn("Il giocatore {} non può passare senza aver pescato", giocatore.getNickname());
					return false;
				}
				passaTurno(1);
				avviaTimerTurno();

				notifyObserverspartitaAggiornata(Map.of());
				return true;
			}
			case DICHIARA_UNO: {
				if (giocatore.getNumeroCarte() == 1) {
					giocatore.setHaDichiaratoUNO(true);
					cancellaTimer();
					logger.info("Il giocatore {} ha dichiarato UNO!", giocatore.getNickname());
					return true;
				}
				return false;
			}
			case PESCA_E_PASSA: {
				if (!haPescatoNelTurno) {
					pesca(giocatore);
				} else {
					logger.warn("Il giocatore {} non può pescare di nuovo prima di passare", giocatore.getNickname());
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
		GiocatoreNET g = getCurrentPlayer();
		for (CartaNET c : g.getMano()) {
			if (isCartaGiocabile(c)) {
				MossaDTO mossa = new MossaDTO(TipoMossa.GIOCA_CARTA);
				mossa.carta = DTOServerUtils.fromCartaNETtoDTO(c);
				if (c.getColore() == Colore.NERO) {
					mossa.coloreScelto = Colore.ROSSO; // o scegli dinamicamente
				}
				effettuaMossa(mossa, g);
				return;
			}
		}
		effettuaMossa(new MossaDTO(TipoMossa.PESCA_E_PASSA), g);
	}
	
	private List<CartaNET> giocaCarta(CartaNET carta) {
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

	private CartaNET pesca(GiocatoreNET giocatore) {
		CartaNET pescata = mazzo.pesca();
		if (pescata != null) {
			giocatore.aggiungiCarta(pescata);
			logger.info("Il giocatore {} ha pescato una carta e passa il turno", giocatore.getNickname());
		} else {
			logger.info("Il mazzo è vuoto", giocatore.getNickname());
		}
		return pescata;
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
					logger.info("Il giocatore {} non ha dichiarato UNO in tempo e pesca 2 carte", giocatore.getNickname());
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
			effettuaMossaAutomatica();
		}, TEMPO_MAX_MOSSA + TEMPO_DI_GUARDIA, TimeUnit.MILLISECONDS);
	}

	private void passaTurno(int step) {
		haPescatoNelTurno = false;
		cartaPescataCorrente = null;

		int dir = direzioneCrescente ? 1 : -1;
		currentPlayerIndex = Math.floorMod(currentPlayerIndex + dir * step, giocatori.size());
	}

	public void prossimoGiocatore() {
		passaTurno(1);
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

	public MazzoNET getMazzo() {
		return mazzo;
	}

	public void cambiaDirezione() {
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
