package onegame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Gestisce le operazioni sul database relative agli utenti
 */
public class UtenteDb {

	/**
	 * Controlla se esiste un utente con lo username specificato
	 * @param username lo username da cercare
	 * @return true se l'utente esiste, false altrimenti
	 * @throws SQLException
	 */
	public boolean esisteUtente(String username) throws SQLException {
		String sql = "SELECT 1 FROM utente WHERE username =?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	/**
	 * Registra un nuovo utente con lo username e la password hash specificati
	 * @param username lo username del nuovo utente
	 * @param passwordHash la password hash del nuovo utente
	 * @throws SQLException
	 */
	public void registraUtente(String username, String passwordHash) throws SQLException {
		String sql = "INSERT INTO utente (username, password) VALUES (?,?)";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			ps.setString(2, passwordHash);
			ps.executeUpdate();
		}
	}

	/**
	 * Recupera la password hash dell'utente con lo username specificato
	 * @param username lo username dell'utente
	 * @return la password hash, o null se l'utente non esiste
	 * @throws SQLException
	 */
	public String getPasswordHash(String username) throws SQLException {
		String sql = "SELECT password FROM utente WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getString("password");
			}
		}
		return null;
	}

	/**
	 * Recupera l'ID dell'utente con lo username specificato
	 * @param username lo username dell'utente
	 * @return l'ID dell'utente, o -1 se l'utente non esiste
	 * @throws SQLException
	 */
	public long getIdByUsername(String username) throws SQLException {
		String sql = "SELECT id FROM utente WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getLong("id");
			}
		}
		return -1;
	}

}
