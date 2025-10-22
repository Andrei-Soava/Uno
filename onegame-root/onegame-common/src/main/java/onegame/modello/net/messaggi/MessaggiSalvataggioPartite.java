package onegame.modello.net.messaggi;

import java.util.List;

public class MessaggiSalvataggioPartite {

	// Eventi partite offline
	public static final String EVENT_SALVA_SALVATAGGIO = "salvataggiopartita:salva";
	public static final String EVENT_CARICA_SALVATAGGIO = "salvataggiopartita:carica";
	public static final String EVENT_ELIMINA_SALVATAGGIO = "salvataggiopartita:elimina";
	public static final String EVENT_RINOMINA_SALVATAGGIO = "salvataggiopartita:rinomina";
	public static final String EVENT_LISTA_SALVATAGGI = "salvataggiopartita:lista";

	// Richiesta di salvataggio
	public static class ReqCreaSalvataggio {
		public String nomeSalvataggio;
		public String partitaSerializzata;
		public boolean sovrascriviSeEsiste; // true = sovrascrive se esiste, false = errore se duplicato

		public ReqCreaSalvataggio() {
		}

		public ReqCreaSalvataggio(String nomeSalvataggio, String partitaSerializzata, boolean sovrascriviSeEsiste) {
			this.nomeSalvataggio = nomeSalvataggio;
			this.partitaSerializzata = partitaSerializzata;
			this.sovrascriviSeEsiste = sovrascriviSeEsiste;
		}

	}

	public static class RespCreaSalvataggio {
		public boolean success;
		public int sovrascritto; // 0 = nuovo, 1 = sovrascritto, -1 = errore
		public String messaggio;

		public RespCreaSalvataggio() {
		}

		public RespCreaSalvataggio(boolean success, int sovrascritto, String messaggio) {
			this.success = success;
			this.sovrascritto = sovrascritto;
			this.messaggio = messaggio;
		}

	}

	// Richiesta di caricamento
	public static class ReqCaricaSalvataggio {
		public String nomeSalvataggio;

		public ReqCaricaSalvataggio() {
		}

		public ReqCaricaSalvataggio(String nomeSalvataggio) {
			this.nomeSalvataggio = nomeSalvataggio;
		}
	}

	public static class RespCaricaSalvataggio {
		public boolean success;
		public String partitaSerializzata;
		public String messaggio;

		public RespCaricaSalvataggio() {
		}

		public RespCaricaSalvataggio(boolean success, String partitaSerializzata, String messaggio) {
			this.success = success;
			this.partitaSerializzata = partitaSerializzata;
			this.messaggio = messaggio;
		}
	}

	// richiesta di eliminazione
	public static class ReqEliminaSalvataggio {
		public String nomeSalvataggio;

		public ReqEliminaSalvataggio() {
		}

		public ReqEliminaSalvataggio(String nomeSalvataggio) {
			this.nomeSalvataggio = nomeSalvataggio;
		}
	}

	public static class RespEliminaSalvataggio {
		public boolean success;
		public String messaggio;

		public RespEliminaSalvataggio() {
		}

		public RespEliminaSalvataggio(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	// richiesta elenco partite
	public static class RespListaSalvataggi {
		public boolean success;
		public List<String> nomiSalvataggi;
		public String messaggio;

		public RespListaSalvataggi() {
		}

		public RespListaSalvataggi(boolean success, List<String> nomiSalvataggi, String messaggio) {
			this.success = success;
			this.nomiSalvataggi = nomiSalvataggi;
			this.messaggio = messaggio;
		}
	}

	public static class ReqRinominaSalvataggio {
		public String nomeVecchio;
		public String nomeNuovo;

		public ReqRinominaSalvataggio() {
		}

		public ReqRinominaSalvataggio(String nomeVecchio, String nomeNuovo) {
			this.nomeVecchio = nomeVecchio;
			this.nomeNuovo = nomeNuovo;
		}
	}

	public static class RespRinominaSalvataggio {
		public boolean success;
		public String messaggio;

		public RespRinominaSalvataggio() {
		}

		public RespRinominaSalvataggio(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

}
