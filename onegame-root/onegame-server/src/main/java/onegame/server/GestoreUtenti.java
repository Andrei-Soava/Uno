package onegame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;

import onegame.net.messaggi.MessaggiUtente.*;
import onegame.net.util.JsonHelper;
import onegame.server.db.UtenteDb;
import onegame.server.utils.PasswordUtils;
import onegame.server.utils.UsernameValidator;

/**
 * Gestore delle operazioni sugli utenti come cambio username, cambio password ed eliminazione account
 */
public class GestoreUtenti {

	private final GestoreSessioni gestoreSessioni;

	private final UtenteDb utenteDb;
	private static final Logger logger = LoggerFactory.getLogger(GestoreUtenti.class);

	public GestoreUtenti(GestoreSessioni gestoreSessioni) {
		this.utenteDb = new UtenteDb();
		this.gestoreSessioni = gestoreSessioni;
	}

	public void handleCambioUsername(Sessione sessione, String str, AckRequest ack) {
		if (sessione == null || sessione.isAnonimo()) {
			ack.sendAckData(new RespCambioUsername(false, null, "Utente non autenticato"));
			logger.warn("Tentativo di cambio username da sessione non valida");
			return;
		}

		try {
			ReqCambioUsername req = JsonHelper.fromJson(str, ReqCambioUsername.class);
			if (!UsernameValidator.isUsernameValido(req.nuovoUsername)) {
				ack.sendAckData(new RespCambioUsername(false, null, "Username non valido"));
				return;
			}

			boolean success = utenteDb.aggiornaUsername(sessione.getUsername(), req.nuovoUsername);
			if (success) {
				sessione.setUsername(req.nuovoUsername);
				ack.sendAckData(new RespCambioUsername(true, req.nuovoUsername, "Username aggiornato con successo"));
				logger.info("Username aggiornato per utente {}", req.nuovoUsername);
			} else {
				ack.sendAckData(new RespCambioUsername(false, null, "Username già in uso o errore"));
			}
		} catch (Exception e) {
			logger.error("Errore cambio username: {}", e.getMessage());
			ack.sendAckData(new RespCambioUsername(false, null, "Errore durante il cambio username"));
		}
	}

	public void handleCambioPassword(Sessione sessione, String str, AckRequest ack) {
		if (sessione == null || sessione.isAnonimo()) {
			ack.sendAckData(new RespCambioPassword(false, "Utente non autenticato"));
			logger.warn("Tentativo di cambio password da sessione non valida");
			return;
		}

		try {
			ReqCambioPassword req = JsonHelper.fromJson(str, ReqCambioPassword.class);
			if (req.passwordAttuale == null || req.nuovaPassword == null || req.passwordAttuale.isBlank()
					|| req.nuovaPassword.isBlank()) {
				ack.sendAckData(new RespCambioPassword(false, "Dati mancanti"));
				return;
			}

			String storedHash = utenteDb.getPasswordHash(sessione.getUsername());
			if (storedHash == null || !PasswordUtils.verificaPassword(req.passwordAttuale, storedHash)) {
				ack.sendAckData(new RespCambioPassword(false, "Password attuale errata"));
				return;
			}

			String nuovoHash = PasswordUtils.hashPassword(req.nuovaPassword);
			boolean success = utenteDb.aggiornaPassword(sessione.getUsername(), nuovoHash);
			if (success) {
				ack.sendAckData(new RespCambioPassword(true, "Password aggiornata con successo"));
				logger.info("Password aggiornata per utente {}", sessione.getUsername());
			} else {
				ack.sendAckData(new RespCambioPassword(false, "Errore durante l'aggiornamento della password"));
			}
		} catch (Exception e) {
			logger.error("Errore cambio password: {}", e.getMessage());
			ack.sendAckData(new RespCambioPassword(false, "Errore durante il cambio password"));
		}
	}

	public void handleEliminaAccount(Sessione sessione, String str, AckRequest ack) {
		if (sessione == null || sessione.isAnonimo()) {
			ack.sendAckData(new RespEliminaAccount(false, "Utente non autenticato"));
			logger.warn("Tentativo di eliminazione account da sessione non valida");
			return;
		}

		try {
			ReqEliminaAccount req = JsonHelper.fromJson(str, ReqEliminaAccount.class);
			if (req.password == null || req.password.isBlank()) {
				ack.sendAckData(new RespEliminaAccount(false, "Password mancante"));
				return;
			}

			String storedHash = utenteDb.getPasswordHash(sessione.getUsername());
			if (storedHash == null || !PasswordUtils.verificaPassword(req.password, storedHash)) {
				ack.sendAckData(new RespEliminaAccount(false, "Password errata"));
				return;
			}

			boolean success = utenteDb.eliminaUtente(sessione.getUsername());
			if (success) {
				sessione.setConnesso(false);
				sessione.setClient(null);
				gestoreSessioni.rimuoviSessione(sessione);
				ack.sendAckData(new RespEliminaAccount(true, "Account eliminato con successo"));
				logger.info("Account eliminato: {}", sessione.getUsername());
			} else {
				ack.sendAckData(new RespEliminaAccount(false, "Errore durante l'eliminazione dell'account"));
			}
		} catch (Exception e) {
			logger.error("Errore eliminazione account: {}", e.getMessage());
			ack.sendAckData(new RespEliminaAccount(false, "Errore durante l'eliminazione dell'account"));
		}
	}

}
