package onegame.server;

import java.util.*;

import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.GiocatoreDTO;
import onegame.modello.net.MossaDTO;
import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.ProtocolloMessaggi.*;
import onegame.server.gioco.PartitaNET;

public class StanzaPartita extends Stanza {

	private final Map<String, Giocatore> giocatorePerToken = new LinkedHashMap<>();
	private PartitaNET partita;
	private boolean partitaInCorso = false;

	public StanzaPartita(int codice, long id, String nome, int maxUtenti, GestoreSessioni gestoreSessioni) {
		super(codice, id, nome, maxUtenti, gestoreSessioni);
	}

	public boolean avviaPartita() {
		lock.lock();
		try {
			if (this.partitaInCorso)
				return false;

			Map<String, SocketIOClient> clientPerToken = new HashMap<>();

			for (String token : sessionePerToken.keySet()) {
				String username = gestoreSessioni.getSessione(token).getUsername();
				Giocatore g = new Giocatore(username);
				giocatorePerToken.put(token, g);
				clientPerToken.put(token, getClient(token));
			}

			List<Giocatore> giocatori = new ArrayList<>(giocatorePerToken.values());
			this.partita = new PartitaNET(giocatori);

			ArrayList<GiocatoreDTO> listaGiocatoriDTO = new ArrayList<>();

			for (Map.Entry<String, Sessione> entry : sessionePerToken.entrySet()) {
				String token = entry.getKey();
				Sessione sessione = entry.getValue();
				Giocatore g = giocatorePerToken.get(token);
				GiocatoreDTO gDTO = new GiocatoreDTO(sessione.getUsername(), sessione.getNickname(),
						sessione.isAnonimo(), g.getMano().getNumCarte());
				listaGiocatoriDTO.add(gDTO);
			}

			for (Map.Entry<String, Giocatore> entry : giocatorePerToken.entrySet()) {
				SocketIOClient client = getClient(entry.getKey());
				if (client != null) {
					MessIniziaPartita mess = new MessIniziaPartita();

					client.sendEvent(ProtocolloMessaggi.EVENT_INIZIA_PARTITA, mess);
				}
			}

			inviaTurnoCorrente();
			this.partitaInCorso = true;
			this.isAperta = false;
			return true;
		} finally {
			lock.unlock();
		}
	}

	public void riceviMossa(String token, MossaDTO mossa) {
		if (partita == null || !giocatorePerToken.containsKey(token))
			return;

		Giocatore g = giocatorePerToken.get(token);
		partita.effettuaMossa(mossa, g);

		if (partita.isFinished()) {
			broadcast("UNO_PARTITA_FINITA", g.getNome());
			partitaInCorso = false;
		} else {
			inviaTurnoCorrente();
		}
	}

	private void inviaTurnoCorrente() {
		if (partita == null)
			return;

		Giocatore g = partita.getGiocatoreCorrente();
//		SocketIOClient client = getClient();
//		if (client != null) {
//			client.sendEvent("UNO_TOCCA_A_TE", partita.topCard());
//		}
	}

	public boolean isPartitaInCorso() {
		return partitaInCorso;
	}

	public PartitaNET getPartita() {
		return partita;
	}

	public Collection<Giocatore> getGiocatori() {
		return Collections.unmodifiableCollection(giocatorePerToken.values());
	}
}
