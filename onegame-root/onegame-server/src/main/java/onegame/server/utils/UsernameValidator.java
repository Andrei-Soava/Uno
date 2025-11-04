package onegame.server.utils;

/**
 * Classe di utilità per la validazione degli username.
 */
public class UsernameValidator {
	/**
	 * Controlla se uno username è valido.
	 * Uno username è valido se:
	 * - è lungo tra 3 e 50 caratteri
	 * - contiene solo lettere, numeri e underscore
	 * - non inizia con "anonimo", "guest" o "admin" (case insensitive)
	 * - non contiene doppio underscore "__"
	 * 
	 * @param username lo username da controllare
	 * @return true se lo username è valido, false altrimenti
	 */
	public static boolean isUsernameValido(String username) {
		String str;
		return username != null && username.matches("^[a-zA-Z0-9_]{3,50}$")
				&& !(str = username.toLowerCase()).startsWith("anonimo") && !str.startsWith("guest")
				&& !str.startsWith("admin") && !str.contains("__");
	}
}
