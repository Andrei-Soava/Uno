package onegame.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import onegame.modello.Mossa;
import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.Utente;

/**
 * ServerUno
 * - registra eventi di connessione/disconnessione
 * - instrada eventi di autenticazione e gestione stanze verso i gestori
 * - instrada eventi di gioco ("partita:mossa") verso la StanzaPartita corretta
 */
public class ServerUno {

    private final SocketIOServer server;
    private final GestoreConnessioni gestoreConnessioni;
    private final GestoreStanze gestoreStanze;
    // sessioni: token -> Utente
    private final Map<String, Utente> sessioni = new ConcurrentHashMap<>();

    public ServerUno(String host, int port) {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setPingInterval(10000);
        config.setPingTimeout(60000);

        this.server = new SocketIOServer(config);
        this.gestoreConnessioni = new GestoreConnessioni(this.server, sessioni);
        this.gestoreStanze = new GestoreStanze(this.server, gestoreConnessioni);

        registraEventi();
    }

    private void registraEventi() {
    	server.addConnectListener(client -> {
    	    HandshakeData hd = client.getHandshakeData();
    	    String addr = "unknown";
    	    try {
    	        if (client.getRemoteAddress() != null) addr = client.getRemoteAddress().toString();
    	        else if (hd != null && hd.getAddress() != null) addr = hd.getAddress().toString();
    	    } catch (Exception ex) {}

    	    System.out.println("[SERVER] Nuova connessione da " + addr + " sessionId=" + client.getSessionId());

    	    //  Recupera token dal parametro della connessione (se presente)
    	    String token = hd.getSingleUrlParam("token");
    	    if (token != null && !token.isEmpty()) {
    	        Utente u = gestoreConnessioni.getUtenteByToken(token);
    	        if (u != null) {
    	            u.setConnesso(true);
    	            client.set("token", token);
    	            System.out.println("[SERVER] Riconnesso utente: " + u.getUsername());
    	        }
    	    }

    	});

        // disconnessione
        server.addDisconnectListener(client -> {
            System.out.println("[SERVER] Disconnessione sessionId=" + client.getSessionId());
            // delego a gestore connessioni per marcare Utente offline
            gestoreConnessioni.handleDisconnessione(client);
        });

        // auth:login
        server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_LOGIN, Object.class,
                new DataListener<Object>() {
                    @Override
                    public void onData(SocketIOClient client, Object data, AckRequest ack) throws Exception {
                        try {
                            ProtocolloMessaggi.ReqAuth reqAuth;
                            
                            if (data instanceof ProtocolloMessaggi.ReqAuth) {
                                reqAuth = (ProtocolloMessaggi.ReqAuth) data;
                            } else if (data instanceof String) {
                                String jsonString = (String) data;
                                ObjectMapper mapper = new ObjectMapper();
                                reqAuth = mapper.readValue(jsonString, ProtocolloMessaggi.ReqAuth.class);
                            } else if (data instanceof Map) {
                                Map<?, ?> map = (Map<?, ?>) data;
                                reqAuth = new ProtocolloMessaggi.ReqAuth();
                                reqAuth.username = (String) map.get("username");
                                reqAuth.password = (String) map.get("password");
                            } else {
                            	if (ack != null && ack.isAckRequested()) {
                                    ack.sendAckData("Formato dati non supportato");
                                } else {
                                    client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Formato dati non supportato");
                                }
                                return;
                            }
                            
                            gestoreConnessioni.handleLogin(client, reqAuth, ack);
                            
                        } catch (Exception e) {
                            System.out.println("[SERVER] Errore processing auth:login: " + e.getMessage());
                            if (ack != null && ack.isAckRequested()) {
                                ack.sendAckData("Errore nel processare la richiesta");
                            } else {
                                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Errore nel processare la richiesta");
                            }
                        }
                    }
                });

        // auth:register
        server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_REGISTER, Object.class,
                (client, data, ack) -> {
                    try {
                        ProtocolloMessaggi.ReqAuth reqAuth;
                        
                        if (data instanceof ProtocolloMessaggi.ReqAuth) {
                            // Caso 1: Oggetto ricevuto direttamente
                            reqAuth = (ProtocolloMessaggi.ReqAuth) data;
                            System.out.println("[SERVER] Ricevuto ReqAuth come oggetto");
                        } else if (data instanceof String) {
                            // Caso 2: Stringa JSON ricevuta
                            String jsonString = (String) data;
                            System.out.println("[SERVER] Ricevuto JSON string: " + jsonString);
                            
                            // Prova a parsare la stringa JSON
                            ObjectMapper mapper = new ObjectMapper();
                            reqAuth = mapper.readValue(jsonString, ProtocolloMessaggi.ReqAuth.class);
                            System.out.println("[SERVER] JSON parsato correttamente");
                        } else if (data instanceof Map) {
                            // Caso 3: Mappa ricevuta (alternativa)
                            Map<?, ?> map = (Map<?, ?>) data;
                            reqAuth = new ProtocolloMessaggi.ReqAuth();
                            reqAuth.username = (String) map.get("username");
                            reqAuth.password = (String) map.get("password");
                            System.out.println("[SERVER] Ricevuto come Map per registrazione");
                        } else {
                            System.out.println("[SERVER] Tipo dati non supportato: " + data.getClass());
                            if (ack != null && ack.isAckRequested()) {
                                ack.sendAckData("Formato dati non supportato");
                            } else {
                                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Formato dati non supportato");
                            }
                            return;
                        }
                        
                        // Chiama il gestore
                        gestoreConnessioni.handleRegister(client, reqAuth, ack);
                        
                    } catch (Exception e) {
                        System.out.println("[SERVER] Errore processing auth:register: " + e.getMessage());
                        if (ack != null && ack.isAckRequested()) {
                            ack.sendAckData("Errore nel processare la richiesta");
                        } else {
                            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Errore nel processare la richiesta");
                        }
                    }
                });

        // auth:anonimo
        server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_ANONIMO, Void.class,
                (client, data, ack) -> gestoreConnessioni.handleAnonimo(client));

        // stanza:crea
        server.addEventListener(ProtocolloMessaggi.EVENT_STANZA_CREA, ProtocolloMessaggi.ReqCreaStanza.class,
                (client, data, ack) -> gestoreStanze.handleCreaStanza(client, data));

        // stanza:entra
        server.addEventListener(ProtocolloMessaggi.EVENT_STANZA_ENTRA, ProtocolloMessaggi.ReqEntraStanza.class,
                (client, data, ack) -> gestoreStanze.handleEntraStanza(client, data));

        // richiesta partite non concluse
        server.addEventListener(ProtocolloMessaggi.EVENT_RICHIESTA_PARTITE_NON_CONCLUSE, Void.class,
                (client, data, ack) -> gestoreConnessioni.handleRichiestaPartiteNonConcluse(client));

        // evento partita:mossa -> payload: Mossa
        server.addEventListener("partita:mossa", Mossa.class, (client, mossa, ack) -> {
            Object tokenObj = client.get("token");
            if (tokenObj == null) {
                client.sendEvent("partita:invalid", "Non autenticato");
                return;
            }
            String token = tokenObj.toString();
            // recupero stanza da gestoreStanze tramite mapping interno
            StanzaPartita stanza = gestoreStanze.getStanzaPerToken(token);
            if (stanza == null) {
                client.sendEvent("partita:invalid", "Non sei in alcuna stanza");
                return;
            }
            stanza.riceviMossa(token, mossa);
        });

        System.out.println("[SERVER] Eventi registrati.");
    }

    public void avvia() {
        server.start();
        System.out.println("[SERVER] Avviato su " + server.getConfiguration().getHostname() + ":"
                + server.getConfiguration().getPort());
    }

    public void stop() {
        server.stop();
        System.out.println("[SERVER] Arrestato");
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8080;
        ServerUno srv = new ServerUno(host, port);
        srv.avvia();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[SERVER] Arresto in corso...");
            srv.stop();
        }));
    }
}
