package onegame.server;

import java.util.ArrayList;
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
	private final Map<String, Sessione> sessioni = new ConcurrentHashMap<>();

	private static final long SESSION_EXPIRATION_MS = 1000_000;

	private static final Logger logger = LoggerFactory.getLogger(GestoreSessioni.class);

	private final ArrayList<SessioneObserver> observers = new ArrayList<>();

	public GestoreSessioni() {
		avviaControlloTimeout();
	}

	public void associaToken(String token, Sessione sessione, SocketIOClient client) {
		sessioni.put(token, sessione);
		sessione.setClient(client);
		client.set("token", token);
		sessione.aggiornaPing();
		sessione.setConnesso(true);
		logger.debug("Sessione associata: nickname={}, sessionId={}", sessione.getNickname(), client.getSessionId());
	}

	public Sessione getSessione(String token) {
		return sessioni.get(token);
	}

	public Sessione getSessioneByClient(SocketIOClient client) {
		String token = client.get("token");
		return getSessione(token);
	}

	/**
	 * Recupera l'utente autenticato associato al client
	 * @param client Il client
	 * @return L'utente autenticato o null se non valido
	 */
	public Sessione getSessioneAutenticato(SocketIOClient client) {
		Sessione sessione = getSessioneByClient(client);

		if (sessione == null || sessione.isAnonimo()) {
			logger.warn("Accesso negato: token non valido o utente anonimo");
			return null;
		}
		return sessione;
	}

	public void aggiornaPing(String token) {
		Sessione s = getSessione(token);
		if (s != null) {
			s.aggiornaPing();
		}
	}

	/**
	 * Gestisce la disconnessione di un client
	 * @param client Il client che si disconnette
	 */
	public void marcaDisconnesso(SocketIOClient client) {
		String token = client.get("token");
		if (token == null)
			return;
		Sessione s = sessioni.get(token);
		if (s != null) {
			s.setConnesso(false);
			s.setClient(null);
			logger.info("Disconnessione sessione: nickname={}, sessionId={}", s.getNickname(), client.getSessionId());
		}
	}

	private void avviaControlloTimeout() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			long now = System.currentTimeMillis();
			for (Sessione s : sessioni.values()) {
				rimuoviSeScaduta(s, now);
			}
		}, 0, 5, TimeUnit.MINUTES);
	}

	public boolean addObserver(SessioneObserver observer) {
		return observers.add(observer);
	}

	public boolean removeObserver(SessioneObserver observer) {
		return observers.remove(observer);
	}

	private void rimuoviSeScaduta(Sessione sessione, long now) {
		String token = sessione.getToken();
		if (!sessione.isConnesso() && now - sessione.getUltimoPing() > SESSION_EXPIRATION_MS) {
			sessioni.remove(token);
			logger.info("Sessione rimossa per inattività: nickname={}, token={}", sessione.getNickname(), token);

			for (SessioneObserver observer : observers) {
				observer.onSessioneInattiva(sessione);
			}
		}
	}

}
