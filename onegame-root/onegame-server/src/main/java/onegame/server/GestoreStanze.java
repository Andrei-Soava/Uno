package onegame.server;

import java.util.HashMap;
import java.util.Map;

public class GestoreStanze {
	private static GestoreStanze instance;
	private final Map<String, ConnessioneGiocatore> connectedPlayer;
	//manca la parte della gameRoom
	
	private GestoreStanze() {
		connectedPlayer = new HashMap<>();
	}
	
	public static GestoreStanze getInstance() {
		if(instance == null) {
			instance = new GestoreStanze();
		}
		return instance;
	}
	
	//aggiuge giocatori alla mappa dei giocatori connessi
	public void registerPlayed(ConnessioneGiocatore giocatore) { 
		connectedPlayer.put(giocatore.getPlayerID(), giocatore);
	}
	
	//rimuove il giocatore disconnesso
	public void unRegisterPlayed(String playerID) {
		connectedPlayer.remove(playerID);
	}
	
	//recupera il giocatore connesso al quell'ID
	public ConnessioneGiocatore getPlayer(String playerID) {
		return connectedPlayer.get(playerID);
	}
	
	//manca la parte della gameRoom

}
