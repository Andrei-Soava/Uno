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
			inviaTurnoCorrente(Messaggi.EVENT_GIOCO_MOSSA);
		}
	}

	private void inviaTurnoCorrente(String nomeEvento) {
		lock.lock();

		try {
			List<GiocatoreDTO> listaGiocatoriDTO = new ArrayList<>();
			
			throw new RuntimeException();
//			// Prepara i DTO dei giocatori
//			for (Sessione s : sessioni) {
//				Giocatore g = giocatori.get(s);
//				GiocatoreDTO gDTO = new GiocatoreDTO(s.getUsername(), s.getNickname(), s.isAnonimo(),
//						g.getMano().getNumCarte());
//				listaGiocatoriDTO.add(gDTO);
//			}
//
//			CartaDTO cartaIniziale = DTOUtils.creaCartaDTO(partita.getCartaCorrente());
//
//			for (Map.Entry<Sessione, Giocatore> entry : giocatori.entrySet()) {
//				Sessione s = entry.getKey();
//				Giocatore g = entry.getValue();
//
//				List<CartaDTO> manoDTO = DTOUtils.creaListaCarteDTO(g.getMano().getCarte());
//				StatoPartitaDTO mess = new StatoPartitaDTO(cartaIniziale, listaGiocatoriDTO,
//						partita.getIndiceGiocatoreCorrente(), manoDTO, partita.getDirezione(), partita.isFinished(),
//						partita.getIndiceVincitore());
//
//				s.sendEvent(nomeEvento, new MessStatoPartita(mess));
//			}
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

}
