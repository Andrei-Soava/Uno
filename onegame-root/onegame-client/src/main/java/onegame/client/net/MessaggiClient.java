package onegame.client.net;

import java.io.Serializable;
/**
 * Contiene le classi dei messaggi che il client invia al server.
 */
public class MessaggiClient {

    public static class ReqAuth {
        public String username;
        public String password;
        public ReqAuth() {}
        public ReqAuth(String username, String password) { this.username = username; this.password = password; }
    }

    public static class ReqCreaStanza {
        public String nomeStanza;
        public int maxGiocatori;
        public ReqCreaStanza() {}
        public ReqCreaStanza(String nomeStanza, int maxGiocatori) { this.nomeStanza = nomeStanza; this.maxGiocatori = maxGiocatori; }
    }

    public static class ReqEntraStanza {
        public String idStanza;
        public ReqEntraStanza() {}
        public ReqEntraStanza(String idStanza) { this.idStanza = idStanza; }
    }
}
