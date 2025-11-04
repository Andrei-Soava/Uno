package onegame.net.messaggi;

public class MessaggiStatistiche {
	public static final String EVENT_CARICA_STATISTICHE = "statistiche:carica";

	public static class RespCaricaStatistiche {
		public boolean success;
		public int partiteGiocate;
		public int partiteVinte;
		public String messaggio;

		public RespCaricaStatistiche() {
		}

		public RespCaricaStatistiche(boolean success, int partiteGiocate, int partiteVinte, String messaggio) {
			this.success = success;
			this.partiteGiocate = partiteGiocate;
			this.partiteVinte = partiteVinte;
			this.messaggio = messaggio;
		}

	}
}
