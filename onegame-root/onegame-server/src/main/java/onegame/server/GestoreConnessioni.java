package onegame.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;

import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.Utente;
import onegame.modello.net.util.JsonHelper;
import onegame.server.db.UtenteDb;
import onegame.modello.net.ProtocolloMessaggi.ReqAuth;
import onegame.modello.net.ProtocolloMessaggi.RespAuth;

/**
 * Gestore delle connessioni e dell'autenticazione degli utenti
 */
public class GestoreConnessioni {

    private final SocketIOServer server;
    private final Map<String, Utente> sessioni; // token -> Utente
    //private final Map<String, String> utentiRegistrati; // username -> passwordHash
    private final SecureRandom random = new SecureRandom();
    private final UtenteDb utenteDb;

    /**
	 * Costruttore del gestore connessioni
	 * 
	 * @param server   Il server Socket.IO
	 * @param sessioni La mappa delle sessioni attive (token -> Utente)
	 */
    public GestoreConnessioni(SocketIOServer server, Map<String, Utente> sessioni) {
        this.server = server;
        this.sessioni = sessioni;
        this.utenteDb = new UtenteDb();
        //this.utentiRegistrati = new ConcurrentHashMap<>();
        
        //utentiRegistrati.put("test25", hashPassword("test25"));
    }

    /**
	 * Gestisce la richiesta di login di un utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 */
    public void handleLogin(SocketIOClient client, String str, AckRequest ackRequest) {
        try {
        	ReqAuth req = JsonHelper.fromJson(str, ReqAuth.class);
            String username = req.getUsername();
            String password = req.getPassword();
            String storedHash = utenteDb.getPasswordHash(username);
            
            if(storedHash == null) {
        		ackRequest.sendAckData(new RespAuth(false, null, null, "Utente non trovato"));
            	return;
            }
            
            if(!verificaPassword(password, storedHash)) {
        		ackRequest.sendAckData(new RespAuth(false, null, null, "Password non valida"));
            	return;
            }
            
            Utente utente = new Utente(username, false);
            String token = JWT.create()
            	    .withClaim("username", username)
            	    .withExpiresAt(new Date(System.currentTimeMillis() + 36000_000))
            	    .sign(Algorithm.HMAC256("u7$T9z!k@!#Lqa^mT2&b10pW"));
            utente.setConnesso(true);
            
            sessioni.put(token, utente);
            client.set("token", token);
            
            ackRequest.sendAckData(new RespAuth(true, null, token, "Login completato"));
        	
            System.out.println("[Server] Utente loggato: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            ackRequest.sendAckData(new RespAuth(false, null, null, "Errore"));
        }
    }

    /**
	 * Gestisce la richiesta di registrazione di un nuovo utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 */
    public void handleRegister(SocketIOClient client, String str, AckRequest ackRequest) {
        try {
        	ReqAuth req = JsonHelper.fromJson(str, ReqAuth.class);
            String username = req.getUsername();
            String password = req.getPassword();
            
            if(utenteDb.esisteUtente(username)) {
            	ackRequest.sendAckData(new RespAuth(false, null, null, "Utente già esistente"));
            	System.out.println("[Server] Registrazione fallita - utente esistente: " + username);
            	return;
            }
            String passwordHash = hashPassword(password);
            utenteDb.registraUtente(username, passwordHash);
            
            Utente utente = new Utente(username, false);
            String token = JWT.create()
            	    .withClaim("username", username)
            	    .withExpiresAt(new Date(System.currentTimeMillis() + 36000_000))
            	    .sign(Algorithm.HMAC256("u7$T9z!k@!#Lqa^mT2&b10pW"));
            utente.setConnesso(true);
            
            sessioni.put(token, utente);
            client.set("token", token);
            
            ackRequest.sendAckData(new RespAuth(true, null, token, "Registrazione completata"));
            System.out.println("[Server] Nuovo utente registrato: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            ackRequest.sendAckData(new RespAuth(false, null, null, "Errore"));
        }
    }

    /**
	 * Gestisce la richiesta di accesso anonimo
	 * @param client Il client che effettua la richiesta
	 */
    public void handleAnonimo(SocketIOClient client, String str, AckRequest ackRequest) {
    	try {
    		Utente utenteAnonimo = new Utente(true);
            String token = UUID.randomUUID().toString();
            
            sessioni.put(token, utenteAnonimo);
            client.set("token", token);
            ackRequest.sendAckData(new RespAuth(true, null, token, "Accesso anonimo riuscito"));
            System.out.println("[Server] Utente anonimo connesso: " + token);
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
	 * Gestisce la disconnessione di un client
	 * @param client Il client che si disconnette
	 */
    public void handleDisconnessione(SocketIOClient client) {
    	Object tokenObj = client.get("token");
        if (tokenObj == null) return;

        String token = tokenObj.toString();
        Utente u = sessioni.get(token);
        if (u != null) {
            u.setConnesso(false);
            System.out.println("[SERVER] Utente disconnesso: " + u.getUsername());
        }
    }

//    public void handleRichiestaPartiteNonConcluse(SocketIOClient client) {
//        ProtocolloMessaggi.RespStanza resp = new ProtocolloMessaggi.RespStanza("", "NESSUNA",
//                "Funzionalità partite non concluse non ancora implementata");
//        client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_OK, resp);
//    }

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
        	throw new RuntimeException("Errore nell'hash della password", e);
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
            byte[] hashFromDb = new byte[combined.length - 16];
            System.arraycopy(combined, 0, salt, 0, 16);
            System.arraycopy(combined, 16, hashFromDb, 0, hashFromDb.length);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(hashFromDb, hashedInput);
        } catch (Exception e) {
        	throw new RuntimeException("Errore nella verifica della password", e);
        }
    }
    
 // RESTITUISCE UTENTE DAL TOKEN
    public Utente getUtenteByToken(String token) {
        return sessioni.get(token);
    }

    

    private String randomString(int len) {
        byte[] buf = new byte[len];
        random.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf).substring(0, len);
    }
}
