package onegame.server.utils;

public class UsernameValidator {
	public static boolean isUsernameValido(String username) {
		String str;
		return username != null && username.matches("^[a-zA-Z0-9_]{3,50}$")
				&& !(str = username.toLowerCase()).startsWith("anonimo") && !str.startsWith("guest")
				&& !str.startsWith("admin") && !str.contains("__");
	}
}
