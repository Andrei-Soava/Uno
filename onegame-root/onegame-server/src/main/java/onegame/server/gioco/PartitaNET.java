package onegame.server.gioco;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import onegame.modello.Mazzo;
import onegame.modello.PartitaIF;
import onegame.modello.PilaScarti;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.Colore;
import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.MossaDTO.TipoMossa;

public final class PartitaNET implements PartitaIF {
	private final UUID partitaId;
	private final List<Giocatore> giocatori = new ArrayList<>();
	private final Mazzo mazzo;
	private final PilaScarti pilaScarti = new PilaScarti();
	private Colore coloreCorrente;
	private boolean sensoOrario = true;
	private int currentPlayerIndex = 0;
	private boolean partitaFinita = false;
	private Instant startedAt;
	
	// stato del turno corrente
    private boolean haPescatoNelTurno = false;
    private Carta cartaPescataCorrente = null;
    
    //gestione timer
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    // Timer attivo
    private ScheduledFuture<?> timerAttivo = null;


	public PartitaNET(List<Giocatore> giocatori) {
		this.giocatori.addAll(giocatori);
		this.partitaId = UUID.randomUUID();
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
		this.startedAt = Instant.now();
	}

	public UUID getId() {
		return partitaId;
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

	@Override
	public int getNumeroGiocatori() {
		return this.giocatori.size();
	}

	public void effettuaMossa(MossaDTO mossa, Giocatore giocatore) {
		try {
			if(mossa == null || giocatore == null || partitaFinita)
				return;
			//verifica il turno
			if (!Objects.equals(giocatore, getCurrentPlayer()))
				return;

			TipoMossa tipo = mossa.tipo;
			
			// Impedisce la doppia pescata
			if(haPescatoNelTurno && tipo == TipoMossa.PESCA)
				return;

			switch (tipo) {
			case GIOCA_CARTA: {
				Carta cartaGiocata = convertiDTOinCarta(mossa.carta);
				if(cartaGiocata == null)
					return;
				
				//verifica che il giocatore possiede la carta
				if (!giocatore.getMano().contieneCarta(cartaGiocata))
					return;
				
				// Se ha appena pescato, può giocare al più la carta pescata
				if (haPescatoNelTurno && cartaPescataCorrente != null
                        && !cartaGiocata.equals(cartaPescataCorrente))
                    return;
				
				Carta cartaTop = getCartaCorrente();

				// Regola del +4: può essere giocato solo se non si hanno carte valide
				if (cartaGiocata instanceof CartaSpeciale cs
						&& cs.getTipo() == CartaSpeciale.TipoSpeciale.PIU_QUATTRO) {
					boolean haAlternative = giocatore.getMano().getCarte().stream()
							.anyMatch(c -> c.giocabileSu(cartaTop) && !(c instanceof CartaSpeciale
									&& ((CartaSpeciale) c).getTipo() == CartaSpeciale.TipoSpeciale.PIU_QUATTRO));
					if (haAlternative)
						return;
				}
				
				// Se è una carta nera, deve essere scelto il colore della carta
				if(cartaGiocata.getColore() == Colore.NERO) {
					if(mossa.coloreScelto == null || mossa.coloreScelto == Colore.NERO) {
						return;
					}else {
						this.coloreCorrente = mossa.coloreScelto;
					}
				}else {
					this.coloreCorrente = cartaGiocata.getColore();
				}

				// Verifica se la carta sia giocabile
				if (!cartaGiocata.giocabileSu(cartaTop))
					return;
				
				// rimuove la carta dalla mano e la mette negli scarti
				giocatore.getMano().rimuoviCarta(cartaGiocata);
				pilaScarti.mettiCarta(cartaGiocata);
				
				cartaPescataCorrente = null;
				haPescatoNelTurno = false;

				cartaGiocata.applicaEffetto(this);

				// Regola dell'UNO: se ha una sola carta, deve dichiarare UNO
				if(giocatore.getMano().getNumCarte() == 1) {
					avviaTimer(giocatore);
				}else {
					giocatore.setHaDichiaratoUNO(false);
					cancellaTimer();
				}

				checkWinCondition(giocatore);
				// prima controlla che la partita non sia finita, e se non lo è passa il turno al prossimo giocatore
                if (!partitaFinita) {
                    passaTurno(1);
                }
                break;
			}

			case PESCA: {
				Carta pescata = mazzo.pesca();
				giocatore.getMano().aggiungiCarta(pescata);
				cartaPescataCorrente = pescata;
				haPescatoNelTurno = true;
				
				// Se il mazzo è vuoto rimescola gli scarti
				if(mazzo.isVuoto()) {
					mazzo.ricostruisciMazzo();
				}
				
				break;
			}

			case PASSA: {
				// Non può passare se non ha pescato né giocato
                if (!haPescatoNelTurno)
                    return;

                passaTurno(1);
                haPescatoNelTurno = false;
                cartaPescataCorrente = null;
                break;
			}
			case DICHIARA_UNO: {
	              if (giocatore.getMano().getNumCarte() == 1) {
	                    giocatore.setHaDichiaratoUNO(true);
	                    cancellaTimer();
	                    System.out.println("Il giocatore " + giocatore.getNome() + " ha detto UNO!");
	                    
	                }
	                break;
	            }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cancellaTimer() { // Interrompe il timer quando non è più necessario
		if(timerAttivo != null) {
			timerAttivo.cancel(false);
			timerAttivo = null;
		}
		
	}

	private void avviaTimer(Giocatore giocatore) { 
		cancellaTimer();
		timerAttivo = scheduler.schedule(()->{
			if(!giocatore.haDichiaratoUNO()) {
				giocatore.getMano().aggiungiCarte(mazzo.pescaN(2));
				System.out.println("Il giocatore " + giocatore.getNome() + " non ha detto UNO! Penalità: aggiungi due carte!");
			}
		}, 5, TimeUnit.SECONDS);
	}

	private void passaTurno(int step) {
		int dir = sensoOrario ? 1 : -1;
		currentPlayerIndex = Math.floorMod(currentPlayerIndex + dir * step, giocatori.size());
	}

	@Override
	public void prossimoGiocatore() {
		passaTurno(1);
	}

	private void checkWinCondition(Giocatore giocatore) {
		if (giocatore.getMano().getNumCarte() == 0) {
			partitaFinita = true;
		}
	}

	public boolean isFinished() {
		return partitaFinita;
	}

	private Carta convertiDTOinCarta(CartaDTO dto) {
		if (dto == null)
			return null;
		if (dto.tipo != null) {
			return new CartaSpeciale(dto.colore, dto.tipo);
		} else {
			return new CartaNumero(dto.colore, dto.numero);
		}
	}

	@Override
	public Giocatore getGiocatoreCorrente() {
		return getCurrentPlayer();
	}

	@Override
	public Mazzo getMazzo() {
		return mazzo;
	}

	@Override
	public void cambiaDirezione() {
		sensoOrario = !sensoOrario;
	}

	@Override
	public boolean getDirezione() {
		return sensoOrario;
	}
}
