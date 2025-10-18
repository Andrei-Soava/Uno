package onegame.server;

import java.util.*;

import com.corundumstudio.socketio.SocketIOClient;

/**
 * Classe astratta che rappresenta una stanza generica con gestione delle
 * sessioni.
 */
public abstract class Stanza {

	protected final int codice;
	protected final long id;
	protected final String nome;
	protected final int maxUtenti;
	protected final GestoreSessioni gestoreSessioni;

	protected final Set<String> tokenUtenti = Collections.synchronizedSet(new LinkedHashSet<>());

	public Stanza(int codice, long id, String nome, int maxUtenti, GestoreSessioni gestoreSessioni) {
		this.codice = codice;
		this.id = id;
		this.nome = nome;
		this.maxUtenti = maxUtenti;
		this.gestoreSessioni = gestoreSessioni;
	}

	public boolean aggiungiUtente(String token) {
		if (tokenUtenti.size() >= maxUtenti || tokenUtenti.contains(token))
			return false;
		tokenUtenti.add(token);
		return true;
	}

	public boolean rimuoviUtente(String token) {
		return tokenUtenti.remove(token);
	}

	public boolean isVuota() {
		return tokenUtenti.isEmpty();
	}

	public boolean isPiena() {
		return tokenUtenti.size() >= maxUtenti;
	}

	public void broadcast(String evento, Object payload) {
		for (String token : tokenUtenti) {
			SocketIOClient client = getClient(token);
			if (client != null) {
				client.sendEvent(evento, payload);
			}
		}
	}

	public int getCodice() {
		return codice;
	}

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public int getMaxUtenti() {
		return maxUtenti;
	}

	public Set<String> getTokenUtenti() {
		return Collections.unmodifiableSet(tokenUtenti);
	}

	public boolean hasUtente(String token) {
		return tokenUtenti.contains(token);
	}

	protected SocketIOClient getClient(String token) {
		return gestoreSessioni.getClient(token);
	}
}
