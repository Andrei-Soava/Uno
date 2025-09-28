package onegame.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.Utente;
import onegame.modello.net.ProtocolloMessaggi.ReqAuth;
import onegame.modello.net.ProtocolloMessaggi.RespAuthFail;
import onegame.modello.net.ProtocolloMessaggi.RespAuthOk;

/**
 * Gestore delle connessioni e dell'autenticazione degli utenti
 */
public class GestoreConnessioni {

    private final SocketIOServer server;
    private final Map<String, Utente> sessioni; // token -> Utente
    private final Map<String, String> utentiRegistrati; // username -> passwordHash
    private final SecureRandom random = new SecureRandom();

    /**
	 * Costruttore del gestore connessioni
	 * 
	 * @param server   Il server Socket.IO
	 * @param sessioni La mappa delle sessioni attive (token -> Utente)
	 */
    public GestoreConnessioni(SocketIOServer server, Map<String, Utente> sessioni) {
        this.server = server;
        this.sessioni = sessioni;
        this.utentiRegistrati = new ConcurrentHashMap<>();
        
        utentiRegistrati.put("test25", hashPassword("test25"));
    }

    /**
	 * Gestisce la richiesta di login di un utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 */
    public void handleLogin(SocketIOClient client, ReqAuth req) {
        try {
            if (req == null || req.username == null || req.password == null) {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Dati mancanti"));
                return;
            }
            String stored = utentiRegistrati.get(req.username);
            if (stored == null) {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Utente non trovato"));
                return;
            }
            if (!verificaPassword(req.password, stored)) {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Password errata"));
                return;
            }
            Utente utente = new Utente(req.username, false);
            String token = generaToken();
            utente.setTokenSessione(token);
            utente.setUltimoHeartbeat(Instant.now().toEpochMilli());
            sessioni.put(token, utente);

            client.set("token", token);
            client.set("username", req.username);

            RespAuthOk ok = new RespAuthOk(utente.getIdGiocatore(), token, "Login riuscito");
            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_OK, ok);
            System.out.println("[AUTH] Login OK: " + req.username + " token=" + token);
        } catch (Exception e) {
            e.printStackTrace();
            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Errore server"));
        }
    }

    /**
	 * Gestisce la richiesta di registrazione di un nuovo utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 */
    public void handleRegister(SocketIOClient client, ReqAuth req) {
        try {
            if (req == null || req.username == null || req.password == null) {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Dati mancanti"));
                return;
            }
            if (utentiRegistrati.containsKey(req.username)) {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Username già esistente"));
                return;
            }
            String hash = hashPassword(req.password);
            utentiRegistrati.put(req.username, hash);

            Utente utente = new Utente(req.username, false);
            String token = generaToken();
            utente.setTokenSessione(token);
            utente.setUltimoHeartbeat(Instant.now().toEpochMilli());
            sessioni.put(token, utente);

            client.set("token", token);
            client.set("username", req.username);

            RespAuthOk ok = new RespAuthOk(utente.getIdGiocatore(), token, "Registrazione e login riusciti");
            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_OK, ok);
            System.out.println("[AUTH] Registrazione OK: " + req.username + " token=" + token);
        } catch (Exception e) {
            e.printStackTrace();
            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Errore server"));
        }
    }

    /**
	 * Gestisce la richiesta di accesso anonimo
	 * @param client Il client che effettua la richiesta
	 */
    public void handleAnonimo(SocketIOClient client) {
        try {
            String nomeAnonimo = "anonimo-" + randomString(6);
            Utente utente = new Utente(nomeAnonimo, true);
            String token = generaToken();
            utente.setTokenSessione(token);
            utente.setUltimoHeartbeat(Instant.now().toEpochMilli());
            sessioni.put(token, utente);

            client.set("token", token);
            client.set("username", nomeAnonimo);

            RespAuthOk ok = new RespAuthOk(utente.getIdGiocatore(), token, "Accesso anonimo riuscito");
            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_OK, ok);
            System.out.println("[AUTH] Anonimo creato: " + nomeAnonimo + " token=" + token);
        } catch (Exception e) {
            e.printStackTrace();
            client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, new RespAuthFail("Errore server"));
        }
    }

    /**
	 * Gestisce la disconnessione di un client
	 * @param client Il client che si disconnette
	 */
    public void handleDisconnessione(SocketIOClient client) {
        try {
            Object tokenObj = client.get("token");
            if (tokenObj != null) {
                String token = tokenObj.toString();
                Utente u = sessioni.get(token);
                if (u != null) {
                    u.setConnesso(false);
                    System.out.println("[CONNESSIONE] Utente disconnesso: " + u.getUsername() + " token=" + token);
                }
            } else {
                System.out.println("[CONNESSIONE] Client sconosciuto disconnesso: sessionId=" + client.getSessionId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRichiestaPartiteNonConcluse(SocketIOClient client) {
        ProtocolloMessaggi.RespStanza resp = new ProtocolloMessaggi.RespStanza("", "NESSUNA",
                "Funzionalità partite non concluse non ancora implementata");
        client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_OK, resp);
    }

    /** Restituisce l'utente associato al token di sessione
     * @param token Il token di sessione
     * @return L'utente corrispondente, o null se il token è invalido
     */
    public Utente getUtenteDaToken(String token) {
        if (token == null) return null;
        return sessioni.get(token);
    }

    public void rimuoviSessione(String token) {
        if (token != null) sessioni.remove(token);
    }

    /** Hash della password con SHA-256
	 * @param password La password in chiaro
	 * @return Hash della password
	 */
    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[salt.length + hashed.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashed, 0, combined, salt.length, hashed.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /** Verifica la password confrontando l'hash
     * @param password La password in chiaro
     * @param stored L'hash memorizzato
     * @return true se la password corrisponde, false altrimenti
     */
    private boolean verificaPassword(String password, String stored) {
        try {
            byte[] combined = Base64.getDecoder().decode(stored);
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            byte[] recombined = new byte[salt.length + hashed.length];
            System.arraycopy(salt, 0, recombined, 0, salt.length);
            System.arraycopy(hashed, 0, recombined, salt.length, hashed.length);
            return MessageDigest.isEqual(recombined, combined);
        } catch (Exception e) {
            return false;
        }
    }

    private String generaToken() {
        return UUID.randomUUID().toString();
    }

    private String randomString(int len) {
        byte[] buf = new byte[len];
        random.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf).substring(0, len);
    }
}
