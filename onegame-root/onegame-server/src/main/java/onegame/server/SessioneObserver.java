package onegame.server;

@FunctionalInterface
public interface SessioneObserver {
	void onSessioneInattiva(Sessione sessione);
}
