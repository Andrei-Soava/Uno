package onegame.modello.net;

public class ProtocolloMessaggi {

    // Eventi
    public static final String EVENT_AUTH_LOGIN = "auth:login";
    public static final String EVENT_AUTH_REGISTER = "auth:register";
    public static final String EVENT_AUTH_ANONIMO = "auth:anonimo";

    public static final String EVENT_STANZA_CREA = "stanza:crea";
    public static final String EVENT_STANZA_ENTRA = "stanza:entra";
    public static final String EVENT_STANZA_ESCI = "stanza:esci";

    public static final String EVENT_RICHIESTA_PARTITE_NON_CONCLUSE = "richiesta:partiteNonConcluse";

    // Risposte
    public static final String EVENT_AUTH_OK = "auth:ok";
    public static final String EVENT_AUTH_FAIL = "auth:fail";

    public static final String EVENT_STANZA_OK = "stanza:ok";
    public static final String EVENT_STANZA_FAIL = "stanza:fail";

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

//     Risposta auth ok
    public static class RespAuthOk {
        public String idGiocatore;
        public String token;
        public String messaggio;

        public RespAuthOk() {
        }

        public RespAuthOk(String idGiocatore, String token, String messaggio) {
            this.idGiocatore = idGiocatore;
            this.token = token;
            this.messaggio = messaggio;
        }
    }

//     Risposta auth fail
    public static class RespAuthFail {
        public String motivo;

        public RespAuthFail() {
        }

        public RespAuthFail(String motivo) {
            this.motivo = motivo;
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
}
