package onegame.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;

public class GameServer {
	private SocketIOServer server;

	public GameServer(int port) {
		Configuration conf = new Configuration();
		conf.setHostname("0.0.0.0"); //accetta connessioni da tutti IP
		conf.setPort(port);
		
		server = new SocketIOServer(conf);//crea il server
	}
	
	private void registerEventHandlers() {
		//quando si connette un client
		server.addConnectListener(client -> {
			System.out.println("Nuovo giocatore connesso: " + client.getSessionId());
			client.sendEvent("Connected", "Benvenuto sul server di uno");
		});
		
		//quando un client si disconnette dal sever
		server.addDisconnectListener(client -> {
			System.out.println("Il giocatore " + client.getSessionId() + " si è disconnesso");
		});
	}
	
	public void start() {
		server.start();
		System.out.println("GameServer avviato");
	}
	
	public void stop() {
		server.stop();
		System.out.println("GameServer fermato");
	}

}
