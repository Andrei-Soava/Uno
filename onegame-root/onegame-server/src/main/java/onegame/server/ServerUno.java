package onegame.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

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
        // connessione
        server.addConnectListener(client -> {
            HandshakeData hd = client.getHandshakeData();
            String addr = "unknown";
            try {
                if (client.getRemoteAddress() != null) addr = client.getRemoteAddress().toString();
                else if (hd != null && hd.getAddress() != null) addr = hd.getAddress().toString();
            } catch (Exception ex) {
                // fallback a "unknown"
            }
            System.out.println("[SERVER] Nuova connessione da " + addr + " sessionId=" + client.getSessionId());
        });

        // disconnessione
        server.addDisconnectListener(client -> {
            System.out.println("[SERVER] Disconnessione sessionId=" + client.getSessionId());
            // delego a gestore connessioni per marcare Utente offline
            gestoreConnessioni.handleDisconnessione(client);
        });

        // auth:login
        server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_LOGIN, ProtocolloMessaggi.ReqAuth.class,
                new DataListener<ProtocolloMessaggi.ReqAuth>() {
                    @Override
                    public void onData(SocketIOClient client, ProtocolloMessaggi.ReqAuth data, AckRequest ack)
                            throws Exception {
                        gestoreConnessioni.handleLogin(client, data);
                    }
                });

        // auth:register
        server.addEventListener(ProtocolloMessaggi.EVENT_AUTH_REGISTER, ProtocolloMessaggi.ReqAuth.class,
                (client, data, ack) -> gestoreConnessioni.handleRegister(client, data));

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
