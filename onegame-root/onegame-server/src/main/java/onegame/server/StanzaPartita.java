package onegame.server;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.carte.Carta;
import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.DTOUtils;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.StatoPartitaDTO;
import onegame.modello.net.messaggi.Messaggi.*;
import onegame.modello.net.messaggi.MessaggiGioco;
import onegame.modello.net.messaggi.MessaggiGioco.MessStatoPartita;
import onegame.server.gioco.PartitaNET;

public class StanzaPartita extends Stanza implements PartitaObserver {

	private final Map<Sessione, Giocatore> giocatori = new LinkedHashMap<>();

	private PartitaNET partita;

	private static final Logger logger = LoggerFactory.getLogger(StanzaPartita.class);

	public StanzaPartita(int codice, String nome, int maxUtenti) {
		super(codice, nome, maxUtenti);
	}

	public boolean avviaPartita() {
		lock.lock();
		try {
			if ((partita != null && isPartitaInCorso()) || sessioni.size() < 2) {
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

			inviaTurnoCorrente(MessaggiGioco.EVENT_INIZIATA_PARTITA, Map.of());

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
	public void partitaAggiornata(Map<Giocatore, List<Carta>> cartePescate) {
		if (partita == null) {
			return;
		}

		if (partita.isFinished()) {
			inviaTurnoCorrente(MessaggiGioco.EVENT_FINITA_PARTITA, cartePescate);
			this.isAperta = true;
		} else {
			inviaTurnoCorrente(MessaggiGioco.EVENT_AGGIORNATA_PARTITA, cartePescate);
		}
	}

	private void inviaTurnoCorrente(String nomeEvento, Map<Giocatore, List<Carta>> cartePescateMap) {
		lock.lock();

		try {
			List<GiocatoreDTO> listaGiocatoriDTO = new ArrayList<>();

			// Prepara i DTO dei giocatori
			List<Giocatore> listaGiocatori = partita.getGiocatori();

			for (Giocatore g : listaGiocatori) {
				GiocatoreDTO gDTO = new GiocatoreDTO(g.getNome(), g.getMano().getNumCarte());
				listaGiocatoriDTO.add(gDTO);
			}

			CartaDTO cartaCorrente = DTOUtils.convertiCartaInDTO(partita.getCartaCorrente());
			int indiceGiocatoreCorrente = partita.getIndiceGiocatoreCorrente();
			boolean direzione = partita.getDirezione();
			int indiceVincitore = partita.getIndiceVincitore();
			boolean finished = partita.isFinished();

			StatoPartitaDTO statoDTO = new StatoPartitaDTO(cartaCorrente, listaGiocatoriDTO, indiceGiocatoreCorrente,
					direzione, finished, indiceVincitore);

			for (Map.Entry<Sessione, Giocatore> entry : giocatori.entrySet()) {
				Sessione s = entry.getKey();
				Giocatore g = entry.getValue();

				List<CartaDTO> manoDTO = DTOUtils.convertiListaCarteInDTO(g.getMano().getCarte());
				int indiceGiocatoreLocale = listaGiocatori.indexOf(g);

				List<CartaDTO> cartePescate;
				if (cartePescateMap != null && cartePescateMap.containsKey(g)) {
					cartePescate = DTOUtils.convertiListaCarteInDTO(cartePescateMap.get(g));
				} else {
					cartePescate = new ArrayList<>();
				}

				MessStatoPartita mess = new MessStatoPartita(statoDTO, indiceGiocatoreLocale, manoDTO, cartePescate);
				s.sendEvent(nomeEvento, mess);
				logger.debug("Stato inviato a utente {}: {}", s.getNickname(), statoDTO);
			}
			logger.debug("Stato partita inviato a tutti i giocatori nella stanza {}", codice);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean rimuoviSessione(Sessione sessione) {
		boolean removed = super.rimuoviSessione(sessione);
		lock.lock();
		try {
			Giocatore g = giocatori.remove(sessione);
			if (g != null) {
				g.setBot(true);
			}
			return removed;
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
