package onegame.server.gioco;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.Mazzo;
import onegame.modello.PartitaIF;
import onegame.modello.PilaScarti;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.DTOUtils;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.MossaDTO.TipoMossa;
import onegame.server.GestoreSessioni;
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

	// gestione timer
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	// Timer attivo
	private ScheduledFuture<?> timerAttivo = null;

	private static final Logger logger = LoggerFactory.getLogger(PartitaNET.class);

	public PartitaNET(List<Giocatore> giocatori, StanzaPartita stanza) {
		this.giocatori.addAll(giocatori);
		this.mazzo = new Mazzo();
		this.mazzo.inizializzaNuovoMazzo();
		init();
	}

	private void init() {
		// Distribuisce 7 carte a ogni giocatore e inizializza pila scarti con prima
		// carta valida
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
		try {
			if (mossa == null || giocatore == null || partitaFinita) {
				logger.warn("Mossa o giocatore nulli, o partita già finita");
				return false;
			}
			// verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer())) {
				logger.warn("Non è il turno del giocatore {}", giocatore.getNome());
				return false;
			}

			TipoMossa tipo = mossa.tipo;

			// Impedisce la doppia pescata
			if (haPescatoNelTurno && tipo == TipoMossa.PESCA) {
				logger.warn("Il giocatore {} ha già pescato in questo turno", giocatore.getNome());
				return false;
			}

			switch (tipo) {
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
				cartaGiocata.applicaEffetto(this);

				// Regola dell'UNO: se ha una sola carta, deve dichiarare UNO
				if (giocatore.getMano().getNumCarte() == 1) {
					avviaTimer(giocatore);
				} else {
					giocatore.setHaDichiaratoUNO(false);
					cancellaTimer();
				}

				checkWinCondition(giocatore);
				// prima controlla che la partita non sia finita, e se non lo è passa il turno
				// al prossimo giocatore
				if (!partitaFinita) {
					passaTurno(1);
				}
				logger.info("Il giocatore {} ha giocato la carta {}", giocatore.getNome(), cartaGiocata.toString());

				notifyObserverspartitaAggiornata();
				return true;
			}

			case PESCA: {
				Carta pescata = mazzo.pesca();
				if (pescata != null) {
					giocatore.getMano().aggiungiCarta(pescata);
					cartaPescataCorrente = pescata;
					haPescatoNelTurno = true;
					giocatore.setHaDichiaratoUNO(false);
					logger.info("Il giocatore {} ha pescato una carta", giocatore.getNome());
				}

				notifyObserverspartitaAggiornata();
				return true;
			}

			case PASSA: {
				// Non può passare se non ha pescato né giocato
				if (!haPescatoNelTurno) {
					logger.warn("Il giocatore {} non può passare senza aver pescato", giocatore.getNome());
					return false;
				}

				passaTurno(1);

				notifyObserverspartitaAggiornata();
				return true;
			}
			case DICHIARA_UNO: {
				if (giocatore.getMano().getNumCarte() == 1) {
					giocatore.setHaDichiaratoUNO(true);
					cancellaTimer();
					logger.info("Il giocatore {} ha dichiarato UNO!", giocatore.getNome());
					return true;
				}
				return false;
			}
			}
			return false;
		} catch (Exception e) {
			logger.error("Errore durante l'effettuazione della mossa: {}", e.getMessage());
			return false;
		}
	}

	private void cancellaTimer() { // Interrompe il timer quando non è più necessario
		if (timerAttivo != null) {
			timerAttivo.cancel(false);
			timerAttivo = null;
		}

	}

	private void avviaTimer(Giocatore giocatore) {
		cancellaTimer();
		timerAttivo = scheduler.schedule(() -> {
			if (!giocatore.haDichiaratoUNO()) {
				giocatore.getMano().aggiungiCarte(mazzo.pescaN(2));
				logger.info("Il giocatore {} non ha dichiarato UNO in tempo e pesca 2 carte", giocatore.getNome());
				notifyObserverspartitaAggiornata();
			}
		}, 8, TimeUnit.SECONDS);
	}

	private void passaTurno(int step) {
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

	private void notifyObserverspartitaAggiornata() {
		for (PartitaObserver ob : observers) {
			ob.partitaAggiornata();
		}
	}
}
