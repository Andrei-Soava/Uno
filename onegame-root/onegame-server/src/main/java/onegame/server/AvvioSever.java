package onegame.server;

public class AvvioSever {
	//usato per l'avvio del server
	public static void main(String[] args) {
		int port = 9092;//porta di rete
		GameServer gameserver = new GameServer(port);
		gameserver.start();
		
		//mantiene sempre il server attivo
		try {
			Thread.sleep(Integer.MAX_VALUE);
		}catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
