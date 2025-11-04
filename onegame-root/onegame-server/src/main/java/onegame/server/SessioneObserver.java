package onegame.server;

@FunctionalInterface
public interface SessioneObserver {
	/**
	 * Chiamato quando una sessione diventa inattiva.
	 * @param sessione la sessione inattiva
	 */
	void onSessioneInattiva(Sessione sessione);
}
