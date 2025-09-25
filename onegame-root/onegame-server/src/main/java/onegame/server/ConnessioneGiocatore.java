package onegame.server;

import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.carte.Carta;
import onegame.modello.giocatori.Giocatore;

public class ConnessioneGiocatore {
	private final SocketIOClient client; //rappresenta la connessione attiva del giocatore
	private final String playerID;
	private final Giocatore giocatore;
	
	public ConnessioneGiocatore(SocketIOClient client, String playerID){
		this.client = client;
		this.playerID = playerID;
		this.giocatore = new Giocatore();
	}
	
	public SocketIOClient getClient() {
		return client;
	}
	
	public String getPlayerID() {
		return playerID;
	}
	
	public Giocatore getGiocatore() {
		return giocatore;
	}
	
	public void addCard(Carta carta) {
		giocatore.aggiungiCarta(carta);
	}
	
	public void removeCard(Carta carta) {
		giocatore.rimuoveCarta(carta);
	}
	
	public int getNumCarte() {
		return giocatore.getMano().getNumCarte();
	}
	
	public void sendEvent(String event, Object data) {
		client.sendEvent(event, data);
	}

	@Override
	public String toString() {
		return "PlayerConnection [ getPlayerID()=" + getPlayerID() + ", getNumCarte()="
				+ getNumCarte() + "]";
	}
	
	//manca la stanza in cui sta giocando il player
	
}
