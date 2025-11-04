package onegame.net.util;

@FunctionalInterface
public interface Callback<T> {
	public void call(T response);
}
