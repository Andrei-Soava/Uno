package onegame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

	/**
	 * Aggiorna lo username dell'utente
	 * @param vecchioUsername lo username attuale
	 * @param nuovoUsername il nuovo username desiderato
	 * @return true se l'aggiornamento è riuscito, false altrimenti
	 * @throws SQLException
	 */
	public boolean aggiornaUsername(String vecchioUsername, String nuovoUsername) throws SQLException {
		// Verifica che il nuovo username non sia già in uso
		if (esisteUtente(nuovoUsername)) {
			return false;
		}

		String sql = "UPDATE utente SET username=? WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nuovoUsername);
			ps.setString(2, vecchioUsername);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}

	/**
	 * Aggiorna la password dell'utente
	 * @param username lo username dell'utente
	 * @param nuovaPasswordHash la nuova password hash
	 * @return true se l'aggiornamento è riuscito, false altrimenti
	 * @throws SQLException
	 */
	public boolean aggiornaPassword(String username, String nuovaPasswordHash) throws SQLException {
		String sql = "UPDATE utente SET password=? WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nuovaPasswordHash);
			ps.setString(2, username);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}

	/**
	 * Elimina l'utente con lo username specificato
	 * @param username lo username dell'utente da eliminare
	 * @return true se l'eliminazione è riuscita, false altrimenti
	 * @throws SQLException
	 */
	public boolean eliminaUtente(String username) throws SQLException {
		String sql = "DELETE FROM utente WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			int deleted = ps.executeUpdate();
			return deleted > 0;
		}
	}

	/**
	 * Incrementa il contatore delle partite giocate per l'utente specificato
	 * @param utenteId ID dell'utente
	 * @return true se l'aggiornamento è riuscito, false altrimenti
	 * @throws SQLException
	 */
	public boolean incrementaPartiteGiocate(long utenteId) throws SQLException {
		String sql = "UPDATE utente SET partite_giocate = partite_giocate + 1 WHERE id=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}

	/**
	 * Incrementa il contatore delle partite vinte per l'utente specificato
	 * @param utenteId ID dell'utente
	 * @return true se l'aggiornamento è riuscito, false altrimenti
	 * @throws SQLException
	 */
	public boolean incrementaPartiteVinte(long utenteId) throws SQLException {
		String sql = "UPDATE utente SET partite_vinte = partite_vinte + 1 WHERE id=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}

	/**
	 * Incrementa il contatore delle partite giocate per l'utente specificato
	 * @param username username dell'utente
	 * @return true se l'aggiornamento è riuscito, false altrimenti
	 * @throws SQLException
	 */
	public boolean incrementaPartiteGiocate(String username) throws SQLException {
		String sql = "UPDATE utente SET partite_giocate = partite_giocate + 1 WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}

	/**
	 * Incrementa il contatore delle partite vinte per l'utente specificato
	 * @param username username dell'utente
	 * @return true se l'aggiornamento è riuscito, false altrimenti
	 * @throws SQLException
	 */
	public boolean incrementaPartiteVinte(String username) throws SQLException {
		String sql = "UPDATE utente SET partite_vinte = partite_vinte + 1 WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			int updated = ps.executeUpdate();
			return updated > 0;
		}
	}

	/**
	 * Recupera le statistiche di gioco dell'utente specificato
	 * @param username lo username dell'utente
	 * @return una mappa con le statistiche (partite_giocate, partite_vinte), o null se l'utente non esiste
	 * @throws SQLException
	 */
	public Map<String, Integer> getStatisticheUtente(String username) throws SQLException {
		String sql = "SELECT partite_giocate, partite_vinte FROM utente WHERE username=?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					Map<String, Integer> stats = new HashMap<>();
					stats.put("partite_giocate", rs.getInt("partite_giocate"));
					stats.put("partite_vinte", rs.getInt("partite_vinte"));
					return stats;
				}
			}
		}
		return null;
	}

}
