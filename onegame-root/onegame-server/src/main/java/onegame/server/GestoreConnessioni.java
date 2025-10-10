package onegame.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import onegame.modello.net.ProtocolloMessaggi;
import onegame.modello.net.Utente;
import onegame.server.db.UtenteDb;
import onegame.modello.net.ProtocolloMessaggi.ReqAuth;
import onegame.modello.net.ProtocolloMessaggi.RespAuthFail;
import onegame.modello.net.ProtocolloMessaggi.RespAuthOk;

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
    public void handleLogin(SocketIOClient client, ProtocolloMessaggi.ReqAuth req, AckRequest ackRequest) {
        try {
            String username = req.getUsername();
            String password = req.getPassword();
            
            String storedHash = utenteDb.getPasswordHash(username);
            
            if(storedHash == null) {
            	if(ackRequest != null && ackRequest.isAckRequested()) {
            		ackRequest.sendAckData("Utente non trovato");
            	}else {
            		client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Utente non trovato");
            	}
            	return;
            }
            
            if(!verificaPassword(password, storedHash)) {
            	if(ackRequest != null && ackRequest.isAckRequested()) {
            		ackRequest.sendAckData("Password errata");
            	}else {
            		client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Password errata");
            	}
            	return;
            }
            
            long id = utenteDb.getIdByUsername(username);
            Utente utente = new Utente(username, false);
            utente.setTokenSessione(UUID.randomUUID().toString());
            utente.setConnesso(true);
            
            sessioni.put(utente.getTokenSessione(), utente);
            client.set("token", utente.getTokenSessione());
            
            if (ackRequest != null && ackRequest.isAckRequested()) {
                ackRequest.sendAckData("");
            } else {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_OK, "Login riuscito");
            }
        	
        	
            System.out.println("[Server] Utente loggato: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            if (ackRequest != null && ackRequest.isAckRequested()) {
                ackRequest.sendAckData("Errore nel database"); 
            } else {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Errore nel database");
            }
        }
    }

    /**
	 * Gestisce la richiesta di registrazione di un nuovo utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 */
    public void handleRegister(SocketIOClient client, ProtocolloMessaggi.ReqAuth req, AckRequest ackRequest) {
        try {
            String username = req.getUsername();
            String password = req.getPassword();
            
            if(utenteDb.esisteUtente(username)) {
            	if(ackRequest != null && ackRequest.isAckRequested()) {
            		ackRequest.sendAckData("Username già esistente");
            	}else {
            		client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Username già esistente");
            	}
            	System.out.println("[Server] Registrazione fallita - utente esistente: " + username);
            	return;
            }
            String passwordHash = hashPassword(password);
            utenteDb.registraUtente(username, passwordHash);
            
            if (ackRequest != null && ackRequest.isAckRequested()) {
                ackRequest.sendAckData(""); 
            } else {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_OK, "Registrazione completata");
            }
            System.out.println("[Server] Nuovo utente registrato: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            if (ackRequest != null && ackRequest.isAckRequested()) {
                ackRequest.sendAckData("Errore nel database");
            } else {
                client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_FAIL, "Errore nel database");
            }
        }
    }

    /**
	 * Gestisce la richiesta di accesso anonimo
	 * @param client Il client che effettua la richiesta
	 */
    public void handleAnonimo(SocketIOClient client) {
        Utente anonimo = new Utente(true);
        sessioni.put(anonimo.getTokenSessione(), anonimo);
        client.set("token",anonimo.getTokenSessione());
        client.sendEvent(ProtocolloMessaggi.EVENT_AUTH_OK, "Accesso anonimo riuscito");
        System.out.println("[Serrver] Utente anonimo connesso: " + anonimo.getIdGiocatore());
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
