package onegame.server;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.net.ProtocolloMessaggi;


public class GestoreStanze {

    private final SocketIOServer server;
    private final GestoreConnessioni gestoreConnessioni;
    // Mappa idStanza -> StanzaPartita
    private final Map<String, StanzaPartita> stanze = new ConcurrentHashMap<>();
    // Mappa tokenUtente -> idStanza
    private final Map<String, String> mappaTokenAStanza = new ConcurrentHashMap<>();

    public GestoreStanze(SocketIOServer server, GestoreConnessioni gestoreConnessioni) {
        this.server = server;
        this.gestoreConnessioni = gestoreConnessioni;
    }
    
    /**
     * Crea una nuova stanza e aggiunge l'utente che ha fatto la richiesta.
     * Invia al client un evento di conferma o errore.
     * @param client client che ha fatto la richiesta
     * @param req richiesta di creazione stanza
     */
    public void handleCreaStanza(SocketIOClient client, ProtocolloMessaggi.ReqCreaStanza req) {
        try {
            Object tokenObj = client.get("token");
            if (tokenObj == null) {
                client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_FAIL,
                        new ProtocolloMessaggi.RespStanza("", "FAIL", "Non autenticato"));
                return;
            }
            String token = tokenObj.toString();
            String idStanza = UUID.randomUUID().toString();
            StanzaPartita stanza = new StanzaPartita(idStanza, req.nomeStanza, Math.max(2, req.maxGiocatori), server,
                    gestoreConnessioni);
            stanze.put(idStanza, stanza);
            mappaTokenAStanza.put(token, idStanza);
            stanza.aggiungiUtente(token, client);
            ProtocolloMessaggi.RespStanza resp = new ProtocolloMessaggi.RespStanza(idStanza, "CREATA",
                    "Stanza creata con successo");
            client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_OK, resp);
            System.out.println("[STANZA] Creata stanza " + idStanza + " da token=" + token);
        } catch (Exception e) {
            e.printStackTrace();
            client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_FAIL,
                    new ProtocolloMessaggi.RespStanza("", "ERROR", "Errore creazione stanza"));
        }
    }
    
    /**
	 * Aggiunge l'utente autenticato alla stanza richiesta.
	 * Invia al client un evento di conferma o errore.
	 * @param client client che ha fatto la richiesta
	 * @param req richiesta di ingresso in stanza
	 */
    public void handleEntraStanza(SocketIOClient client, ProtocolloMessaggi.ReqEntraStanza req) {
        try {
            Object tokenObj = client.get("token");
            if (tokenObj == null) {
                client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_FAIL,
                        new ProtocolloMessaggi.RespStanza("", "FAIL", "Non autenticato"));
                return;
            }
            String token = tokenObj.toString();
            StanzaPartita stanza = stanze.get(req.idStanza);
            if (stanza == null) {
                client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_FAIL,
                        new ProtocolloMessaggi.RespStanza("", "NO_STANZA", "Stanza non trovata"));
                return;
            }
            boolean ok = stanza.aggiungiUtente(token, client);
            if (!ok) {
                client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_FAIL,
                        new ProtocolloMessaggi.RespStanza(req.idStanza, "FULL", "Stanza piena o partita iniziata"));
                return;
            }
            mappaTokenAStanza.put(token, req.idStanza);
            client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_OK,
                    new ProtocolloMessaggi.RespStanza(req.idStanza, "ENTRATO", "Ingresso nella stanza riuscito"));
            System.out.println("[STANZA] Token " + token + " entrato in stanza " + req.idStanza);
        } catch (Exception e) {
            e.printStackTrace();
            client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_FAIL,
                    new ProtocolloMessaggi.RespStanza(req.idStanza, "ERROR", "Errore ingresso stanza"));
        }
    }
    
    /**
     * Rimuove l'utente dalla stanza in cui si trova, se presente.
     * @param token token dell'utente da rimuovere
     */
    public void rimuoviTokenDaStanza(String token) {
        String id = mappaTokenAStanza.remove(token);
        if (id != null) {
            StanzaPartita s = stanze.get(id);
            if (s != null) s.rimuoviUtente(token);
        }
    }

    public StanzaPartita getStanza(String idStanza) {
        return stanze.get(idStanza);
    }

    /**
     * Restituisce la stanza in cui si trova l'utente con il token specificato.
     * @param token token dell'utente
     * @return stanza in cui si trova l'utente, o null se non trovato
     */
    public StanzaPartita getStanzaPerToken(String token) {
        if (token == null) return null;
        String id = mappaTokenAStanza.get(token);
        if (id == null) return null;
        return stanze.get(id);
    }
}
