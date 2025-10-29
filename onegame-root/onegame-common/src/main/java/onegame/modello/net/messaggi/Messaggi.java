package onegame.modello.net.messaggi;

import onegame.modello.net.StatoStanzaDTO;

public class Messaggi {

	// Eventi
	public static final String EVENT_AUTH_LOGIN = "auth:login";
	public static final String EVENT_AUTH_REGISTER = "auth:register";
	public static final String EVENT_AUTH_ANONIMO = "auth:anonimo";
	public static final String EVENT_LOGOUT_FORZATO = "auth:logout_forzato";

	public static final String EVENT_STANZA_CREA = "stanza:crea";
	public static final String EVENT_STANZA_ENTRA = "stanza:entra";
	public static final String EVENT_STANZA_ESCI = "stanza:esci";
	public static final String EVENT_STANZA_DETTAGLI = "stanza:dettagli";
	public static final String EVENT_STANZA_AGGIORNAMENTO = "stanza:aggiornamento";

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

	public static class ReqAuthAnonimo {
		public String nickname;

		public ReqAuthAnonimo() {
		}

		public ReqAuthAnonimo(String nickname) {
			this.nickname = nickname;
		}

	}

	public static class RespAuth {
		public boolean success;
		public String token;
		public String messaggio;
		public String username;
		public String nickname;

		public RespAuth() {
		}

		public RespAuth(boolean success, String token, String messaggio, String username, String nickname) {
			this.success = success;
			this.token = token;
			this.messaggio = messaggio;
			this.username = username;
			this.nickname = nickname;
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

	public static class RespCreaStanza {
		public boolean success;
		public String messaggio;
		public int codiceStanza; // -1 se fallita

		public RespCreaStanza() {
		}

		public RespCreaStanza(boolean success, String messaggio, int codiceStanza) {
			this.success = success;
			this.messaggio = messaggio;
			this.codiceStanza = codiceStanza;
		}
	}

//     Entra stanza
	public static class ReqEntraStanza {
		public int codiceStanza;

		public ReqEntraStanza() {
		}

		public ReqEntraStanza(int codiceStanza) {
			this.codiceStanza = codiceStanza;
		}
	}

	public static class RespEntraStanza {
		public boolean success;
		public String messaggio;

		public RespEntraStanza() {
		}

		public RespEntraStanza(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	public static class RespAbbandonaStanza {
		public boolean success;
		public String messaggio;

		public RespAbbandonaStanza() {
		}

		public RespAbbandonaStanza(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	public static class RespDettagliStanza {
		public boolean success;
		public String messaggio;
		public StatoStanzaDTO statoStanza;

		public RespDettagliStanza() {
		}

		public RespDettagliStanza(boolean success, String messaggio, StatoStanzaDTO statoStanza) {
			this.success = success;
			this.messaggio = messaggio;
			this.statoStanza = statoStanza;
		}
	}
}
