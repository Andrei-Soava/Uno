package onegame.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.net.Utente;

public class GestoreStanze {
	private static GestoreStanze instance;
	private final Map<String, Utente> connectedPlayer;
	private final Map<String, StanzaPartita> stanze;
	
	private GestoreStanze() {
		connectedPlayer = new HashMap<>();
		stanze = new HashMap<>();
	}
	
	public static GestoreStanze getInstance() {
		if(instance == null) {
			instance = new GestoreStanze();
		}
		return instance;
	}
	
	//aggiuge giocatori alla mappa dei giocatori connessi
	public void registerPlayed(Utente utente) { 
		connectedPlayer.put(utente.getIdGiocatore(), utente);
	}
	
	//rimuove il giocatore disconnesso
	public void unRegisterPlayed(String idGiocatore) {
		connectedPlayer.remove(idGiocatore);
	}
	
	//recupera il giocatore connesso al quell'ID
	public Utente getPlayer(String idGiocatore) {
		return connectedPlayer.get(idGiocatore);
	}
	
	//crea e registra una nuova stanza
	public StanzaPartita creaStanza(String id, String nome, int maxUtenti, SocketIOServer server, GestoreConnessioni gestoreConnessioni) {
		StanzaPartita stanza = new StanzaPartita(id, nome, maxUtenti, server, gestoreConnessioni);
		stanze.put(id, stanza);
		return stanza;
	}
	
	//recupera una stanza già esistente tramite id
	public StanzaPartita getStanza(String id) {
		return stanze.get(id);
	}
	
	//rimuove una stanza dalla mappa
	public void rimuoviStanza(String id) {
		stanze.remove(id);
	}
	
	//restituisce tutte le stanze attive
	public Collection<StanzaPartita> getStanzeAttive(){
		return stanze.values();
	}

}
