package onegame.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

import onegame.modello.net.Utente;
import onegame.modello.net.util.JsonHelper;
import onegame.server.db.UtenteDb;
import onegame.modello.net.ProtocolloMessaggi.ReqAuth;
import onegame.modello.net.ProtocolloMessaggi.RespAuth;

/**
 * Gestore delle connessioni e dell'autenticazione degli utenti
 */
public class GestoreConnessioni {
    private final Map<String, Utente> sessioni; // token -> Utente
    //private final Map<String, String> utentiRegistrati; // username -> passwordHash
    private final SecureRandom random = new SecureRandom();
    private final UtenteDb utenteDb;
    
    private static final String JWT_SECRET = "u7$T9z!k@!#Lqa^mT2&b10pW";
    private static final long TOKEN_EXPIRATION_MS = 36000_000;
    
    private static final Logger logger = LoggerFactory.getLogger(GestoreConnessioni.class);


    /**
	 * Costruttore del gestore connessioni
	 * @param sessioni La mappa delle sessioni attive (token -> Utente)
	 */
    public GestoreConnessioni(Map<String, Utente> sessioni) {
        this.sessioni = sessioni;
        this.utenteDb = new UtenteDb();
        //this.utentiRegistrati = new ConcurrentHashMap<>();
        
        //utentiRegistrati.put("test25", hashPassword("test25"));
    }
    
    /** Genera un token JWT per l'utente */
    private String generaToken(String username) {
        return JWT.create()
            .withClaim("username", username)
            .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
            .sign(Algorithm.HMAC256(JWT_SECRET));
    }

    /**
	 * Gestisce la richiesta di login di un utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
    public void handleLogin(SocketIOClient client, String str, AckRequest ackRequest) {
        try {
        	ReqAuth req = JsonHelper.fromJson(str, ReqAuth.class);
            String username = req.username;
            String password = req.password;
            
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            	ackRequest.sendAckData(new RespAuth(false, null, null, "Credenziali mancanti"));
            	return;
            }
            
            String storedHash = utenteDb.getPasswordHash(username);
            if(storedHash == null || !verificaPassword(password, storedHash)) {
        		ackRequest.sendAckData(new RespAuth(false, null, null, "Credenziali non valide"));
        		logger.warn("[Server] Tentativo di login fallito per username: {}", username);
            	return;
            }
            
            Utente utente = new Utente(username, false);
            utente.setConnesso(true);
            String token = generaToken(username);
            
            sessioni.put(token, utente);
            client.set("token", token);
            
            ackRequest.sendAckData(new RespAuth(true, null, token, "Login completato"));
        	
            logger.info("[Server] Utente connesso: {}", username);
        } catch (Exception e) {
        	logger.error("[Server] Errore durante il login: {}", e.getMessage());
            ackRequest.sendAckData(new RespAuth(false, null, null, "Errore interno"));
        }
    }

    /**
	 * Gestisce la richiesta di registrazione di un nuovo utente
	 * @param client Il client che effettua la richiesta
	 * @param req La richiesta di autenticazione (username e password)
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
    public void handleRegister(SocketIOClient client, String str, AckRequest ackRequest) {
        try {
        	ReqAuth req = JsonHelper.fromJson(str, ReqAuth.class);
            String username = req.username;
            String password = req.password;
            
            if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            	ackRequest.sendAckData(new RespAuth(false, null, null, "Credenziali mancanti"));
            	return;
            }
            
            if(utenteDb.esisteUtente(username)) {
            	ackRequest.sendAckData(new RespAuth(false, null, null, "Utente già esistente"));
            	logger.warn("[Server] Tentativo di registrazione con username già esistente: {}", username);
            	return;
            }
            
            String passwordHash = hashPassword(password);
            utenteDb.registraUtente(username, passwordHash);
            
            Utente utente = new Utente(username, false);
            utente.setConnesso(true);
            String token = generaToken(username);
            
            sessioni.put(token, utente);
            client.set("token", token);
            
            ackRequest.sendAckData(new RespAuth(true, null, token, "Registrazione completata"));
            logger.info("[Server] Nuovo utente registrato: {}", username);
        } catch (Exception e) {
        	logger.error("[Server] Errore durante la registrazione: {}", e.getMessage());
            ackRequest.sendAckData(new RespAuth(false, null, null, "Errore interno"));
        }
    }

    /**
	 * Gestisce la richiesta di accesso anonimo
	 * @param client Il client che effettua la richiesta
	 * @param ackRequest L'oggetto per inviare la risposta di ack
	 */
    public void handleAnonimo(SocketIOClient client, AckRequest ackRequest) {
    	try {
    		Utente utenteAnonimo = new Utente(true);
            String token = UUID.randomUUID().toString();
            
            sessioni.put(token, utenteAnonimo);
            client.set("token", token);
            ackRequest.sendAckData(new RespAuth(true, null, token, "Accesso anonimo riuscito"));
            logger.info("[Server] Utente anonimo connesso");
    	} catch (Exception e) {
    		logger.error("[Server] Errore durante l'accesso anonimo: {}", e.getMessage());
			ackRequest.sendAckData(new RespAuth(false, null, null, "Errore interno"));
		}
    }

    /**
	 * Gestisce la disconnessione di un client
	 * @param client Il client che si disconnette
	 */
    public void handleDisconnessione(SocketIOClient client) {
//    	logger.error("Token " + client.get("token"));
//    	logger.error("0");
//    	String token = client.get("token");
//        if (token == null) return;
//        logger.error("1");
//        Utente u = sessioni.get(token);
//        if (u != null) {
//            u.setConnesso(false);
//            logger.info("[Server] Utente disconnesso: {}", u.isAnonimo() ? "Anonimo" : u.getUsername());
//        }
    }

//    public void handleRichiestaPartiteNonConcluse(SocketIOClient client) {
//        ProtocolloMessaggi.RespStanza resp = new ProtocolloMessaggi.RespStanza("", "NESSUNA",
//                "Funzionalità partite non concluse non ancora implementata");
//        client.sendEvent(ProtocolloMessaggi.EVENT_STANZA_OK, resp);
//    }

    public void rimuoviSessione(String token) {
        if (token != null) sessioni.remove(token);
    }

	/**
	 * Genera un hash sicuro della password usando PBKDF2 con HMAC SHA-256.
	 * @param password La password in chiaro
	 * @return Stringa Base64 contenente salt + hash
	 */
	private String hashPassword(String password) {
		try {
			// Salt casuale di 16 byte
			byte[] salt = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(salt);

			// Parametri PBKDF2
			int iterations = 65536;
			int keyLength = 256;

			// Derivazione della chiave
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hash = skf.generateSecret(spec).getEncoded();

			// Combina salt + hash
			byte[] combined = new byte[salt.length + hash.length];
			System.arraycopy(salt, 0, combined, 0, salt.length);
			System.arraycopy(hash, 0, combined, salt.length, hash.length);

			// Codifica in Base64 per memorizzazione
			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new RuntimeException("Errore nell'hash della password", e);
		}
	}

	/**
	 * Verifica se la password in chiaro corrisponde all'hash memorizzato.
	 * @param password La password in chiaro
	 * @param stored L'hash memorizzato (Base64 salt + hash)
	 * @return true se la password è corretta, false altrimenti
	 */
	private boolean verificaPassword(String password, String stored) {
		try {
			byte[] combined = Base64.getDecoder().decode(stored);

			// Estrai salt e hash
			byte[] salt = Arrays.copyOfRange(combined, 0, 16);
			byte[] hashFromDb = Arrays.copyOfRange(combined, 16, combined.length);

			// Parametri PBKDF2 (devono essere identici)
			int iterations = 65536;
			int keyLength = 256;

			// Deriva hash dalla password fornita
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			byte[] hashedInput = skf.generateSecret(spec).getEncoded();

			// Confronto sicuro
			return MessageDigest.isEqual(hashFromDb, hashedInput);
		} catch (Exception e) {
			throw new RuntimeException("Errore nella verifica della password", e);
		}
	}

    
    /** Recupera l'utente associato a un token
	 * @param token Il token di sessione
	 * @return L'utente associato, o null se non trovato
	 */
    public Utente getUtenteByToken(String token) {
        return sessioni.get(token);
    }

    

//    private String randomString(int len) {
//        byte[] buf = new byte[len];
//        random.nextBytes(buf);
//        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf).substring(0, len);
//    }
}
