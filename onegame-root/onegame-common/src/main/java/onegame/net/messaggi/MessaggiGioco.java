package onegame.net.messaggi;

import java.util.List;

import onegame.net.CartaDTO;
import onegame.net.MossaDTO;
import onegame.net.StatoPartitaDTO;

public class MessaggiGioco {

	public static final String EVENT_INIZIA_PARTITA = "partita:inizia";
	public static final String EVENT_INIZIATA_PARTITA = "partita:iniziata";
	public static final String EVENT_AGGIORNATA_PARTITA = "partita:aggiornata";
	public static final String EVENT_FINITA_PARTITA = "partita:finita";
	public static final String EVENT_EFFETTUA_MOSSA_PARTITA = "partita:mossa";

	public static class ReqEffettuaMossa {
		public MossaDTO mossa;

		public ReqEffettuaMossa() {
		}

		public ReqEffettuaMossa(MossaDTO mossa) {
			this.mossa = mossa;
		}
	}

	public static class RespEffettuaMossa {
		public boolean success;
		public String messaggio;

		public RespEffettuaMossa() {
		}

		public RespEffettuaMossa(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	public static class RespIniziaPartita {
		public boolean success;
		public String messaggio;

		public RespIniziaPartita() {
		}

		public RespIniziaPartita(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	public static class MessStatoPartita {
		public StatoPartitaDTO statoPartita;
		/** Indice del giocatore locale che riceve questo stato in statoPartita.giocatori */
		public int indiceGiocatoreLocale;
		/** Carte in mano al giocatore locale */
		public List<CartaDTO> carteInMano;
		/** Carta pescata dal giocatore locale a seguito di una mossa di pesca, null altrimenti */
		public CartaDTO cartaPescata;

		public MessStatoPartita() {
		}

		public MessStatoPartita(StatoPartitaDTO statoPartita, int indiceGiocatoreLocale, List<CartaDTO> carteInMano,
				CartaDTO cartaPescata) {
			this.statoPartita = statoPartita;
			this.indiceGiocatoreLocale = indiceGiocatoreLocale;
			this.carteInMano = carteInMano;
			this.cartaPescata = cartaPescata;
		}

	}

}
