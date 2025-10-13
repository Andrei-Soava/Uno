package onegame.server.gioco;

import java.time.Instant;
import java.util.*;

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

	public Carta topCard() {
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
			if (giocatore != giocatori.get(currentPlayerIndex))
				return;

			TipoMossa tipo = mossa.tipo;

			switch (tipo) {
			case GIOCA_CARTA: {
				Carta cartaGiocata = convertiDTOinCarta(mossa.carta);
				if (!giocatore.getMano().contieneCarta(cartaGiocata))
					return;
				Carta cartaTop = topCard();

				if (!cartaGiocata.giocabileSu(cartaTop))
					return;

				// Rimuovi dalla mano e metti sulla pila
				giocatore.getMano().rimuoviCarta(cartaGiocata);
				pilaScarti.mettiCarta(cartaGiocata);

				// Se è jolly, imposta colore scelto
				if (cartaGiocata.getColore() == Colore.NERO && mossa.coloreScelto != null) {
					this.coloreCorrente = mossa.coloreScelto;
				} else {
					this.coloreCorrente = cartaGiocata.getColore();
				}

				// Applica effetto
				cartaGiocata.applicaEffetto(this);

				// Controlla vittoria
				checkWinCondition(giocatore);

				// Passa turno
				passaTurno(1);
				break;
			}

			case PESCA: {
				Carta pescata = mazzo.pesca();
				giocatore.getMano().aggiungiCarta(pescata);
				// Regola base: dopo pesca il turno finisce
				passaTurno(1);
				break;
			}

			case PASSA: {
				// Nessuna azione, solo cambio turno
				passaTurno(1);
				break;
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
	}

}
