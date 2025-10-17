package onegame.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;

public class GestoreSessioni {
	// token -> Utente
	private final Map<String, Utente> sessioni = new ConcurrentHashMap<>();
	private final Map<String, SocketIOClient> clientPerToken = new ConcurrentHashMap<>();

	private static final long TIMEOUT_MS = 300_000;
	private static final long SESSION_EXPIRATION_MS = 1000_000;

	private static final Logger logger = LoggerFactory.getLogger(GestoreSessioni.class);

	public GestoreSessioni() {
		avviaControlloTimeout();
	}

	public void associaToken(String token, Utente utente, SocketIOClient client) {
		sessioni.put(token, utente);
		clientPerToken.put(token, client);
		client.set("token", token);
		utente.aggiornaPing();
		utente.setConnesso(true);
	}

	public Utente getUtente(String token) {
		return sessioni.get(token);
	}

	public SocketIOClient getClient(String token) {
		return clientPerToken.get(token);
	}

	public void aggiornaPing(String token) {
		Utente u = getUtente(token);
		if (u != null) {
			u.aggiornaPing();
		}
	}

	/**
	 * Gestisce la disconnessione di un client
	 * @param client Il client che si disconnette
	 */
	public void impostaUtenteDisconnesso(SocketIOClient client) {
		String token = client.get("token");
		if (token == null)
			return;
		Utente u = sessioni.get(token);
		if (u != null) {
			u.setConnesso(false);
			logger.info("Disconnessione utente: {} sessionId={}", u.getNickname(), client.getSessionId());
		}
	}

	public void rimuoviSessione(String token) {
		if (token != null)
			sessioni.remove(token);
	}

	private void avviaControlloTimeout() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			long now = System.currentTimeMillis();
			for (Map.Entry<String, Utente> entry : sessioni.entrySet()) {
				String token = entry.getKey();
				Utente u = entry.getValue();

				// Utente inattivo → non connesso
				if (u.isConnesso() && now - u.getUltimoPing() > TIMEOUT_MS) {
					u.setConnesso(false);
					logger.info("Timeout utente: {}", u.getUsername());
				}

//				// Utente disconnesso → rimozione sessione
//				if (!u.isConnesso() && now - u.getUltimoPing() > SESSION_EXPIRATION_MS) {
//					sessioni.remove(token);
//					logger.info("Rimozione sessione utente inattivo: {}", u.getUsername());
//				}
			}
		}, 0, 5, TimeUnit.MINUTES);
	}
}
