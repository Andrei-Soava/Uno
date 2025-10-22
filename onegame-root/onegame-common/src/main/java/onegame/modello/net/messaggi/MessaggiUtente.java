package onegame.modello.net.messaggi;

public class MessaggiUtente {
	public static final String EVENT_CAMBIO_USERNAME = "utente:cambio_username";
	public static final String EVENT_CAMBIO_PASSWORD = "utente:cambio_password";
	public static final String EVENT_ELIMINA_ACCOUNT = "utente:elimina_account";

	// Richiesta cambio username
	public static class ReqCambioUsername {
		public String nuovoUsername;

		public ReqCambioUsername() {
		}

		public ReqCambioUsername(String nuovoUsername) {
			this.nuovoUsername = nuovoUsername;
		}
	}

	public static class RespCambioUsername {
		public boolean success;
		public String username;
		public String messaggio;

		public RespCambioUsername() {
		}

		public RespCambioUsername(boolean success, String username, String messaggio) {
			this.success = success;
			this.username = username;
			this.messaggio = messaggio;
		}

	}

	// Richiesta cambio password
	public static class ReqCambioPassword {
		public String passwordAttuale;
		public String nuovaPassword;

		public ReqCambioPassword() {
		}

		public ReqCambioPassword(String passwordAttuale, String nuovaPassword) {
			this.passwordAttuale = passwordAttuale;
			this.nuovaPassword = nuovaPassword;
		}
	}

	public static class RespCambioPassword {
		public boolean success;
		public String messaggio;

		public RespCambioPassword() {
		}

		public RespCambioPassword(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}

	// Richiesta eliminazione account
	public static class ReqEliminaAccount {
		public String password;

		public ReqEliminaAccount() {
		}

		public ReqEliminaAccount(String password) {
			this.password = password;
		}
	}

	public static class RespEliminaAccount {
		public boolean success;
		public String messaggio;

		public RespEliminaAccount() {
		}

		public RespEliminaAccount(boolean success, String messaggio) {
			this.success = success;
			this.messaggio = messaggio;
		}
	}
}
