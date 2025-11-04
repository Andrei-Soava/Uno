package onegame.server;

/**
 * Interfaccia per osservare lo stato delle sessioni.
 */
@FunctionalInterface
public interface SessioneObserver {
	/**
	 * Chiamato quando una sessione diventa inattiva.
	 * @param sessione la sessione inattiva
	 */
	void onSessioneInattiva(Sessione sessione);
}
