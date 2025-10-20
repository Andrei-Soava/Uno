package onegame.server;

import java.util.*;

import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.DTOUtils;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.StatoPartitaDTO;
import onegame.modello.net.messaggi.ProtocolloMessaggi;
import onegame.modello.net.messaggi.ProtocolloMessaggi.*;
import onegame.server.gioco.PartitaNET;

public class StanzaPartita extends Stanza implements PartitaObserver {

	private final Map<String, Giocatore> giocatorePerToken = new LinkedHashMap<>();
	private PartitaNET partita;

	public StanzaPartita(int codice, long id, String nome, int maxUtenti, GestoreSessioni gestoreSessioni) {
		super(codice, id, nome, maxUtenti, gestoreSessioni);
	}

	public boolean avviaPartita() {
		lock.lock();
		try {
			if (isPartitaInCorso()) {
				return false;
			}

			// Crea i giocatori
			for (String token : sessionePerToken.keySet()) {
				String username = gestoreSessioni.getSessione(token).getUsername();
				Giocatore g = new Giocatore(username);
				giocatorePerToken.put(token, g);
			}

			// Inizializza la partita
			List<Giocatore> giocatori = new ArrayList<>(giocatorePerToken.values());
			this.partita = new PartitaNET(giocatori, this);
			this.partita.addObserver(this);

			inviaTurnoCorrente(ProtocolloMessaggi.EVENT_INIZIO_PARTITA);

			this.isAperta = false;
			return true;
		} finally {
			lock.unlock();
		}
	}

	public void riceviMossa(String token, MossaDTO mossa) {
		if (partita == null || !giocatorePerToken.containsKey(token)) {
			return;
		}

		Giocatore g = giocatorePerToken.get(token);
		partita.effettuaMossa(mossa, g);

	}

	@Override
	public void partitaAggiornata() {
		if (partita == null) {
			return;
		}

		if (partita.isFinished()) {
			inviaTurnoCorrente(ProtocolloMessaggi.EVENT_FINE_PARTITA);
			this.isAperta = true;
		} else {
			inviaTurnoCorrente(ProtocolloMessaggi.EVENT_GIOCO_MOSSA);
		}
	}

	private void inviaTurnoCorrente(String nomeEvento) {
		lock.lock();

		try {
			List<GiocatoreDTO> listaGiocatoriDTO = new ArrayList<>();

			// Prepara i DTO dei giocatori
			for (Map.Entry<String, Sessione> entry : sessionePerToken.entrySet()) {
				String token = entry.getKey();
				Sessione sessione = entry.getValue();

				Giocatore g = giocatorePerToken.get(token);
				GiocatoreDTO gDTO = new GiocatoreDTO(sessione.getUsername(), sessione.getNickname(),
						sessione.isAnonimo(), g.getMano().getNumCarte());
				listaGiocatoriDTO.add(gDTO);
			}

			CartaDTO cartaIniziale = DTOUtils.creaCartaDTO(partita.getCartaCorrente());

			for (Map.Entry<String, Giocatore> entry : giocatorePerToken.entrySet()) {
				String token = entry.getKey();
				Giocatore g = entry.getValue();

				List<CartaDTO> manoDTO = DTOUtils.creaListaCarteDTO(g.getMano().getCarte());
				StatoPartitaDTO mess = new StatoPartitaDTO(cartaIniziale, listaGiocatoriDTO,
						partita.getIndiceGiocatoreCorrente(), manoDTO, partita.getDirezione(), partita.isFinished(),
						partita.getIndiceVincitore());

				SocketIOClient client = getClient(token);
				if (client != null) {
					client.sendEvent(nomeEvento, new MessStatoPartita(mess));
				}
			}
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
		return Collections.unmodifiableCollection(giocatorePerToken.values());
	}

}
