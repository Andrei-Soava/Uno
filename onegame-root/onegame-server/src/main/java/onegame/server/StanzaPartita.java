package onegame.server;

import java.util.*;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.DTOUtils;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.StatoPartitaDTO;
import onegame.modello.net.messaggi.Messaggi;
import onegame.modello.net.messaggi.Messaggi.*;
import onegame.server.gioco.PartitaNET;

public class StanzaPartita extends Stanza implements PartitaObserver {

	private final Map<Sessione, Giocatore> giocatori = new LinkedHashMap<>();

	private PartitaNET partita;

	public StanzaPartita(int codice, String nome, int maxUtenti) {
		super(codice, nome, maxUtenti);
	}

	public boolean avviaPartita() {
		lock.lock();
		try {
			if (isPartitaInCorso()) {
				return false;
			}

			giocatori.clear();
			// Crea i giocatori
			for (Sessione s : sessioni) {
				giocatori.put(s, new Giocatore(s.getNickname()));
			}

			// Inizializza la partita
			List<Giocatore> lista = new ArrayList<>(giocatori.values());
			Collections.shuffle(lista);
			this.partita = new PartitaNET(lista, this);
			this.partita.addObserver(this);

			inviaTurnoCorrente(Messaggi.EVENT_INIZIO_PARTITA);

			this.isAperta = false;
			return true;
		} finally {
			lock.unlock();
		}
	}

	public void riceviMossa(Sessione sessione, MossaDTO mossa) {
		if (partita == null || !giocatori.containsKey(sessione)) {
			return;
		}

		partita.effettuaMossa(mossa, giocatori.get(sessione));

	}

	@Override
	public void partitaAggiornata() {
		if (partita == null) {
			return;
		}

		if (partita.isFinished()) {
			inviaTurnoCorrente(Messaggi.EVENT_FINE_PARTITA);
			this.isAperta = true;
		} else {
			inviaTurnoCorrente(Messaggi.EVENT_AGGIORNAMENTO_PARTITA);
		}
	}

	private void inviaTurnoCorrente(String nomeEvento) {
		lock.lock();

		try {
			List<GiocatoreDTO> listaGiocatoriDTO = new ArrayList<>();

			// Prepara i DTO dei giocatori
			List<Giocatore> listaGiocatori = partita.getGiocatori();

			for (Giocatore g : listaGiocatori) {
				GiocatoreDTO gDTO = new GiocatoreDTO(g.getNome(), g.getMano().getNumCarte());
				listaGiocatoriDTO.add(gDTO);
			}

			CartaDTO cartaCorrente = DTOUtils.creaCartaDTO(partita.getCartaCorrente());

			for (Map.Entry<Sessione, Giocatore> entry : giocatori.entrySet()) {
				Sessione s = entry.getKey();
				Giocatore g = entry.getValue();

				List<CartaDTO> manoDTO = DTOUtils.creaListaCarteDTO(g.getMano().getCarte());
				int indiceGiocatoreLocale = listaGiocatori.indexOf(g);
				int indiceGiocatoreCorrente = partita.getIndiceGiocatoreCorrente();
				boolean direzione = partita.getDirezione();
				int indiceVincitore = partita.getIndiceVincitore();
				boolean finished = partita.isFinished();

				StatoPartitaDTO statoDTO = new StatoPartitaDTO(cartaCorrente, listaGiocatoriDTO, indiceGiocatoreLocale,
						indiceGiocatoreCorrente, manoDTO, direzione, finished, indiceVincitore);

				s.sendEvent(nomeEvento, new MessStatoPartita(statoDTO));
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean rimuoviSessione(Sessione sessione) {
		lock.lock();
		try {
			Giocatore g = giocatori.remove(sessione);
			g.setBot(true);
			return super.rimuoviSessione(sessione);
		} finally {
			lock.unlock();
		}
	}

	public boolean isPartitaInCorso() {
		return !partita.isFinished();
	}

	public PartitaNET getPartita() {
		return partita;
	}

	public Collection<Giocatore> getGiocatori() {
		lock.lock();
		try {
			return Collections.unmodifiableCollection(new ArrayList<>(giocatori.values()));
		} finally {
			lock.unlock();
		}
	}

	private Sessione trovaSessionePerGiocatore(Giocatore g) {
		for (Map.Entry<Sessione, Giocatore> entry : giocatori.entrySet()) {
			if (entry.getValue().equals(g)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
