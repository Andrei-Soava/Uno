package onegame.modello.net.util;

@FunctionalInterface
public interface Callback<T> {
	void call(T response);
}
