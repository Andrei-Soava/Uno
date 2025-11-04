package onegame.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;

import onegame.net.messaggi.Messaggi;

/**
 * Gestore delle sessioni utente.
 */
public class GestoreSessioni {
	// token -> Utente
	private final Map<String, Sessione> sessioni = new ConcurrentHashMap<>();

	private static final long SESSION_EXPIRATION_MS = 1000_000;

	private static final Logger logger = LoggerFactory.getLogger(GestoreSessioni.class);

	private final ArrayList<SessioneObserver> observers = new ArrayList<>();

	public GestoreSessioni() {
		avviaControlloTimeout();
	}

	/**
	 * Associa un token a una sessione e a un client SocketIO.
	 * @param token il token di autenticazione
	 * @param sessione la sessione utente
	 * @param client il client SocketIO
	 */
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

	public void rimuoviSessione(Sessione sessione) {
		if (sessione == null)
			return;

		sessioni.remove(sessione.getToken());
		sessione.sendEvent(Messaggi.EVENT_LOGOUT_FORZATO, "Il tuo account è stato eliminato");

		logger.info("Sessione rimossa manualmente: nickname={}, token={}", sessione.getNickname(), sessione.getToken());

		for (SessioneObserver observer : observers) {
			observer.onSessioneInattiva(sessione);
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

	public Collection<Sessione> getSessioniAttive() {
		return Collections.unmodifiableCollection(sessioni.values());
	}

	/**
	 * Avvia un task periodico per controllare e rimuovere le sessioni scadute.
	 */
	private void avviaControlloTimeout() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(() -> {
			long now = System.currentTimeMillis();
			for (Sessione s : sessioni.values()) {
				rimuoviSeScaduta(s, now);
			}
		}, 0, 5, TimeUnit.MINUTES);
	}

	/**
	 * Rimuove la sessione se è scaduta per inattività.
	 * @param sessione La sessione da controllare
	 * @param now Il timestamp corrente
	 */
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

	public boolean addObserver(SessioneObserver observer) {
		return observers.add(observer);
	}

	public boolean removeObserver(SessioneObserver observer) {
		return observers.remove(observer);
	}
}
