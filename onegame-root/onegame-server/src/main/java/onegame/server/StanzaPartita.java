package onegame.server;

import java.util.*;

import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.giocatori.Giocatore;
import onegame.modello.net.MossaDTO;
import onegame.server.gioco.PartitaNET;

public class StanzaPartita extends Stanza {

	private final Map<String, Giocatore> giocatori = new LinkedHashMap<>();
	private PartitaNET partita;
	private boolean partitaInCorso = false;

	public StanzaPartita(int codice, long id, String nome, int maxUtenti, GestoreSessioni gestoreSessioni) {
		super(codice, id, nome, maxUtenti, gestoreSessioni);
	}

	@Override
	public boolean aggiungiUtente(String token) {
		if (partitaInCorso || isPiena() || hasUtente(token))
			return false;

		Sessione sessione = gestoreSessioni.getSessione(token);
		String username = sessione != null ? sessione.getUsername() : "anonimo";

		Giocatore g = new Giocatore(username);
		giocatori.put(token, g);
		return super.aggiungiUtente(token);
	}

	@Override
	public boolean rimuoviUtente(String token) {
		giocatori.remove(token);
		return super.rimuoviUtente(token);
	}

	public void avviaPartita() {
		if (partitaInCorso || !isPiena())
			return;

		List<Giocatore> lista = new ArrayList<>(giocatori.values());
		this.partita = new PartitaNET(lista);
		this.partitaInCorso = true;

		for (Map.Entry<String, Giocatore> entry : giocatori.entrySet()) {
			SocketIOClient client = getClient(entry.getKey());
			if (client != null) {
			}
		}

		inviaTurnoCorrente();
	}

	public void riceviMossa(String token, MossaDTO mossa) {
		if (partita == null || !giocatori.containsKey(token))
			return;

		Giocatore g = giocatori.get(token);
		partita.effettuaMossa(mossa, g);

		if (partita.isFinished()) {
			partitaInCorso = false;
		} else {
			inviaTurnoCorrente();
		}
	}

	private void inviaTurnoCorrente() {
		if (partita == null)
			return;

		Giocatore g = partita.getGiocatoreCorrente();
	}

	public boolean isPartitaInCorso() {
		return partitaInCorso;
	}

	public PartitaNET getPartita() {
		return partita;
	}

	public Collection<Giocatore> getGiocatori() {
		return Collections.unmodifiableCollection(giocatori.values());
	}
}
