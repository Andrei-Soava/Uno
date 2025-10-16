package onegame.modello.net;

import java.util.List;

public class ProtocolloMessaggi {

	// Eventi
	public static final String EVENT_AUTH_LOGIN = "auth:login";
	public static final String EVENT_AUTH_REGISTER = "auth:register";
	public static final String EVENT_AUTH_ANONIMO = "auth:anonimo";

	public static final String EVENT_STANZA_CREA = "stanza:crea";
	public static final String EVENT_STANZA_ENTRA = "stanza:entra";
	public static final String EVENT_STANZA_ESCI = "stanza:esci";

	// Eventi partite offline
	public static final String EVENT_SALVA_PARTITA = "partita:salva";
	public static final String EVENT_CARICA_PARTITA = "partita:carica";
	public static final String EVENT_ELIMINA_PARTITA = "partita:elimina";
	public static final String EVENT_LISTA_PARTITE = "partita:lista";

	public static final String EVENT_GIOCO_MOSSA = "gioco:mossa";

//    //Risposte partite offline
//    public static final String EVENT_PARTITA_OK = "partita:ok";
//    public static final String EVENT_PARTITA_FAIL = "partita:fail";
//
//    public static final String EVENT_STANZA_OK = "stanza:ok";
//    public static final String EVENT_STANZA_FAIL = "stanza:fail";

	// Richiesta di login / register
	public static class ReqAuth {
		public String username;
		public String password;

		public ReqAuth() {
		}

		public ReqAuth(String username, String password) {
			this.username = username;
			this.password = password;
		}
	}

	public static class RespAuth {
		public boolean success;
		public String idGiocatore;
		public String token;
		public String messaggio;

		public RespAuth() {
		}

		public RespAuth(boolean success, String idGiocatore, String token, String messaggio) {
			this.success = success;
			this.idGiocatore = idGiocatore;
			this.token = token;
			this.messaggio = messaggio;
		}
	}

//     Crea stanza
	public static class ReqCreaStanza {
		public String nomeStanza;
		public int maxGiocatori;

		public ReqCreaStanza() {
		}

		public ReqCreaStanza(String nomeStanza, int maxGiocatori) {
			this.nomeStanza = nomeStanza;
			this.maxGiocatori = maxGiocatori;
		}
	}

//     Entra stanza
	public static class ReqEntraStanza {
		public String idStanza;

		public ReqEntraStanza() {
		}

		public ReqEntraStanza(String idStanza) {
			this.idStanza = idStanza;
		}
	}

//     Stanza risposta semplice
	public static class RespStanza {
		public String idStanza;
		public String stato;
		public String messaggio;

		public RespStanza() {
		}

		public RespStanza(String idStanza, String stato, String messaggio) {
			this.idStanza = idStanza;
			this.stato = stato;
			this.messaggio = messaggio;
		}
	}

	// Richiesta di salvataggio
	public static class ReqSalvaPartita {
		public String nomeSalvataggio;
		public String partitaSerializzata;

		public ReqSalvaPartita() {
		}

		public ReqSalvaPartita(String nomeSalvataggio, String partitaSerializzata) {
			this.nomeSalvataggio = nomeSalvataggio;
			this.partitaSerializzata = partitaSerializzata;
		}
	}

	public static class RespSalvaPartita {
		public boolean success;
		public String messaggio;

		public RespSalvaPartita() {
		}

		public RespSalvaPartita(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	// Richiesta di caricamento
	public static class ReqCaricaPartita {
		public String idSalvataggio;

		public ReqCaricaPartita() {
		}

		public ReqCaricaPartita(String idSalvataggio) {
			this.idSalvataggio = idSalvataggio;
		}
	}

	public static class RespCaricaPartita {
		public boolean success;
		public String partitaSerializzata;
		public String messaggio;

		public RespCaricaPartita() {
		}

		public RespCaricaPartita(boolean success, String partitaSerializzata, String messaggio) {
			this.success = success;
			this.partitaSerializzata = partitaSerializzata;
			this.messaggio = messaggio;
		}
	}

	// richiesta di eliminazione
	public static class ReqEliminaPartita {
		public String idSalvataggio;

		public ReqEliminaPartita() {
		}

		public ReqEliminaPartita(String idSalvataggio) {
			this.idSalvataggio = idSalvataggio;
		}
	}
	
	public static class RespEliminaPartita {
		public boolean success;
		public String messaggio;

		public RespEliminaPartita() {
		}

		public RespEliminaPartita(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	// richiesta elenco partite
	public static class RespListaPartite {
		public boolean success;
		public java.util.List<String> nomiSalvataggi;
		public String messaggio;

		public RespListaPartite() {
		}

		public RespListaPartite(boolean success, List<String> nomiSalvataggi, String messaggio) {
			this.success = success;
			this.nomiSalvataggi = nomiSalvataggi;
			this.messaggio = messaggio;
		}
	}
}
