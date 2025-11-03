package onegame.server;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.modello.carte.Colore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.StatoPartitaDTO;
import onegame.modello.net.messaggi.MessaggiGioco;
import onegame.modello.net.messaggi.MessaggiGioco.MessStatoPartita;
import onegame.server.db.UtenteDb;
import onegame.server.eccezioni.EccezionePartita;
import onegame.server.eccezioni.GiocatoriInsufficientiException;
import onegame.server.eccezioni.PartitaGiaAvviataException;
import onegame.server.gioco.CartaNET;
import onegame.server.gioco.GiocatoreNET;
import onegame.server.gioco.MazzoBlackFactory;
import onegame.server.gioco.MazzoONEFactory;
import onegame.server.gioco.MazzoWildFactory;
import onegame.server.gioco.PartitaNET;
import onegame.server.utils.DTOServerUtils;

public class StanzaPartita extends Stanza implements PartitaObserver {

	private final Map<Sessione, GiocatoreNET> giocatori = new LinkedHashMap<>();
	private boolean vincitoreAggiornato = false;

	private PartitaNET partita;

	private static final UtenteDb utenteDb = new UtenteDb();
	private static final Logger logger = LoggerFactory.getLogger(StanzaPartita.class);

	public StanzaPartita(int codice, String nome, int maxUtenti) {
		super(codice, nome, maxUtenti);
	}

	public void avviaPartita() throws EccezionePartita {
		lock.lock();
		try {
			if ((partita != null && isPartitaInCorso())) {
				throw new PartitaGiaAvviataException();
			}
			if (sessioni.size() < 2) {
				throw new GiocatoriInsufficientiException();
			}

			giocatori.clear();
			// Crea i giocatori
			for (Sessione s : sessioni) {
				try {
					utenteDb.incrementaPartiteGiocate(s.getUsername());
				} catch (Exception e) {
					logger.error("Errore nell'aggiornamento delle statistiche per l'utente {}: {}", s.getUsername(),
							e.getMessage());
				}
				giocatori.put(s, new GiocatoreNET(s.getNickname()));
			}
			vincitoreAggiornato = false;

			// Inizializza la partita
			List<GiocatoreNET> lista = new ArrayList<>(giocatori.values());
			Collections.shuffle(lista);
			this.partita = new PartitaNET(lista, this, MazzoONEFactory.getInstance());
			this.partita.addObserver(this);

			inviaTurnoCorrente(MessaggiGioco.EVENT_INIZIATA_PARTITA, Map.of());

			this.isAperta = false;
		} finally {
			lock.unlock();
		}
	}

	public void riceviMossa(Sessione sessione, MossaDTO mossa) throws EccezionePartita {
		if (partita == null || !giocatori.containsKey(sessione)) {
			return;
		}

		if (mossa.tipo == MossaDTO.TipoMossa.GIOCA_CARTA) {
			CartaNET cartaGiocata = DTOServerUtils.fromCartaDTOtoNET(mossa.carta);
			partita.giocaCarta(cartaGiocata, mossa.coloreScelto, giocatori.get(sessione));
		} else if (mossa.tipo == MossaDTO.TipoMossa.PESCA) {
			partita.pesca(giocatori.get(sessione));
		} else if (mossa.tipo == MossaDTO.TipoMossa.PASSA) {
			partita.passa(giocatori.get(sessione));
		} else if (mossa.tipo == MossaDTO.TipoMossa.DICHIARA_UNO) {
			partita.dichiaraUno(giocatori.get(sessione));
		} else if (mossa.tipo == MossaDTO.TipoMossa.PESCA_E_PASSA) {
			partita.pescaEPassa(giocatori.get(sessione));
		}
	}

	@Override
	public void partitaAggiornata(Map<GiocatoreNET, CartaNET> cartePescate) {
		if (partita == null) {
			return;
		}

		if (partita.isFinished()) {
			inviaTurnoCorrente(MessaggiGioco.EVENT_FINITA_PARTITA, cartePescate);
			this.isAperta = true;
			if (!vincitoreAggiornato) {
				GiocatoreNET vincitore = partita.getVincitore();
				try {
					if (vincitore != null) {
						utenteDb.incrementaPartiteVinte(vincitore.getNickname());
						logger.debug("Statistiche aggiornate per l'utente vincitore {}", vincitore.getNickname());
					}
				} catch (Exception e) {
					logger.error("Errore nell'aggiornamento delle statistiche per l'utente {}: {}",
							vincitore.getNickname(), e.getMessage());
				}
				vincitoreAggiornato = true;
			}
		} else {
			inviaTurnoCorrente(MessaggiGioco.EVENT_AGGIORNATA_PARTITA, cartePescate);
		}
	}

	/**
	 * Invia lo stato corrente della partita a tutti i giocatori nella stanza
	 * @param nomeEvento nome dell'evento da inviare
	 * @param cartePescateMap mappa delle carte pescate dai giocatori in questo turno
	 */
	private void inviaTurnoCorrente(String nomeEvento, Map<GiocatoreNET, CartaNET> cartePescateMap) {
		lock.lock();

		try {
			List<GiocatoreDTO> listaGiocatoriDTO = new ArrayList<>();

			// Prepara i DTO dei giocatori
			List<GiocatoreNET> listaGiocatori = partita.getGiocatori();

			for (GiocatoreNET g : listaGiocatori) {
				GiocatoreDTO gDTO = new GiocatoreDTO(g.getNickname(), g.getNumeroCarte());
				listaGiocatoriDTO.add(gDTO);
			}

			CartaDTO cartaCorrente = DTOServerUtils.fromCartaNETtoDTO(partita.getCartaCorrente());
			Colore coloreCorrente = partita.getColoreCorrente();
			int indiceGiocatoreCorrente = partita.getIndiceGiocatoreCorrente();
			boolean direzione = partita.getDirezione();
			int indiceVincitore = partita.getIndiceVincitore();
			boolean finished = partita.isFinished();

			logger.debug("Carta corrente: {}, colore corrente: {}", cartaCorrente, coloreCorrente);

			StatoPartitaDTO statoDTO = new StatoPartitaDTO(cartaCorrente, coloreCorrente, listaGiocatoriDTO,
					indiceGiocatoreCorrente, direzione, finished, indiceVincitore);

			for (Map.Entry<Sessione, GiocatoreNET> entry : giocatori.entrySet()) {
				Sessione s = entry.getKey();
				GiocatoreNET g = entry.getValue();

				List<CartaDTO> manoDTO = DTOServerUtils.fromListaCarteNETtoDTO(g.getMano());
				int indiceGiocatoreLocale = listaGiocatori.indexOf(g);

				CartaDTO cartaPescata = null;
				if (cartePescateMap != null && cartePescateMap.containsKey(g)) {
					cartaPescata = DTOServerUtils.fromCartaNETtoDTO(cartePescateMap.get(g));
				}

				MessStatoPartita mess = new MessStatoPartita(statoDTO, indiceGiocatoreLocale, manoDTO, cartaPescata);
				s.sendEvent(nomeEvento, mess);
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
			GiocatoreNET g = giocatori.remove(sessione);
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

	public Collection<GiocatoreNET> getGiocatori() {
		lock.lock();
		try {
			return Collections.unmodifiableCollection(new ArrayList<>(giocatori.values()));
		} finally {
			lock.unlock();
		}
	}

	private Sessione trovaSessionePerGiocatore(GiocatoreNET g) {
		for (Map.Entry<Sessione, GiocatoreNET> entry : giocatori.entrySet()) {
			if (entry.getValue().equals(g)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
