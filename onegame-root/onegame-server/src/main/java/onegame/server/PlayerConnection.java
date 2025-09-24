package onegame.server;

import com.corundumstudio.socketio.SocketIOClient;


public class PlayerConnection {
	private final SocketIOClient client; //rappresenta la connessione attiva del giocatore
	private final String playerID;
	private String nickname;
	
	public PlayerConnection(SocketIOClient client, String nickname, String playerID){
		this.client = client;
		this.nickname = nickname;
		this.playerID = playerID;
	}
	
	public SocketIOClient getClient() {
		return client;
	}
	
	public String getPlayerID() {
		return playerID;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void sendEvent(String event, Object data) {
		client.sendEvent(event, data);
	}
	
	//manca la stanza in cui sta giocando il player
	//manca la mano del giocatore

}
