package onegame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Gestisce le operazioni sul database relative alle partite incomplete salvate
 * dagli utenti registrati.
 */
public class PartitaIncompletaDb {

	public boolean esistePartita(long utenteId, String nomeSalvataggio) throws SQLException {
		String sql = "SELECT 1 FROM partita_incompleta WHERE utente_id = ? AND nome_salvataggio = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			ps.setString(2, nomeSalvataggio);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	/**
	 * Crea una nuova partita incompleta nel database
	 * @param utenteId ID dell'utente che ha salvato la partita
	 * @param nomeSalvataggio Nome del salvataggio
	 * @param partitaSerializzata La partita serializzata in formato stringa
	 * @throws SQLException
	 */
	public boolean createPartita(long utenteId, String nomeSalvataggio, String partitaSerializzata)
			throws SQLException {
		String sql = "INSERT INTO partita_incompleta (utente_id, nome_salvataggio, partita_serializzata) VALUES (?, ?, ?)";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			ps.setString(2, nomeSalvataggio);
			ps.setString(3, partitaSerializzata);
			ps.executeUpdate();
			return ps.getUpdateCount() > 0;
		}
	}

	/**
	 * Recupera la lista dei nomi dei salvataggi delle partite incomplete per un
	 * dato utente
	 * @param utenteId ID dell'utente
	 * @return Lista dei nomi dei salvataggi
	 * @throws SQLException
	 */
	public ArrayList<String> getPartiteByUtente(long utenteId) throws SQLException {
		String sql = "SELECT nome_salvataggio FROM partita_incompleta WHERE utente_id = ?";
		ArrayList<String> result = new ArrayList<>();
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					result.add(rs.getString("nome_salvataggio"));
				}
			}
		}
		return result;
	}

	/**
	 * Recupera la partita con il dato ID
	 * @param partitaId ID della partita
	 * @return La partita serializzata in formato stringa, o null se non trovata
	 * @throws SQLException
	 */
	public String getPartitaById(long partitaId) throws SQLException {
		String sql = "SELECT partita_serializzata FROM partita_incompleta WHERE id = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, partitaId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getString("partita_serializzata");
			}
		}
		return null;
	}

	/**
	 * Recupera la partita di un utente dato il nome del salvataggio
	 * @param utenteId ID dell'utente
	 * @param nomeSalvataggio Nome del salvataggio
	 * @return La partita serializzata in formato stringa, o null se non trovata
	 * @throws SQLException
	 */
	public String getPartitaByUtenteAndNome(long utenteId, String nomeSalvataggio) throws SQLException {
		String sql = "SELECT partita_serializzata FROM partita_incompleta WHERE utente_id = ? AND nome_salvataggio = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			ps.setString(2, nomeSalvataggio);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getString("partita_serializzata");
			}
		}
		return null;
	}

	/**
	 * Aggiorna il nome del salvataggio di una partita incompleta
	 * @param partitaId ID della partita
	 * @param nuovoNome Nuovo nome del salvataggio
	 * @throws SQLException
	 */
	public boolean updateNomeSalvataggio(long partitaId, String nuovoNome) throws SQLException {
		String sql = "UPDATE partita_incompleta SET nome_salvataggio = ? WHERE id = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nuovoNome);
			ps.setLong(2, partitaId);
			return ps.executeUpdate() > 0;
		}
	}

	public boolean rinominaSalvataggio(long utenteId, String vecchioNome, String nuovoNome) throws SQLException {
		String sql = "UPDATE partita_incompleta SET nome_salvataggio = ? WHERE utente_id = ? AND nome_salvataggio = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nuovoNome);
			ps.setLong(2, utenteId);
			ps.setString(3, vecchioNome);
			ps.executeUpdate();
			return ps.getUpdateCount() > 0;
		}
	}

	/**
	 * Elimina una partita incompleta dal database
	 * @param partitaId ID della partita da eliminare
	 * @throws SQLException
	 */
	@Deprecated
	public void deletePartita(long partitaId) throws SQLException {
		String sql = "DELETE FROM partita_incompleta WHERE id = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, partitaId);
			ps.executeUpdate();
		}
	}

	public boolean updatePartita(long utenteId, String nomeSalvataggio, String partitaSerializzata)
			throws SQLException {
		String sql = "UPDATE partita_incompleta SET partita_serializzata = ?, created_at = CURRENT_TIMESTAMP "
				+ "WHERE utente_id = ? AND nome_salvataggio = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, partitaSerializzata);
			ps.setLong(2, utenteId);
			ps.setString(3, nomeSalvataggio);
			return ps.executeUpdate() > 0;
		}
	}

//	public boolean deletePartitaByUtenteAndNome(String username, String nomeSalvataggio) throws SQLException {
//		long utenteId = utenteDb.getIdByUsername(username);
//		return deletePartitaByUtenteAndNome(utenteId, nomeSalvataggio);
//	}

	/**
	 * Elimina una partita incompleta di un utente dato il nome del salvataggio
	 * @param utenteId ID dell'utente
	 * @param nomeSalvataggio Nome del salvataggio da eliminare
	 * @throws SQLException
	 */
	public boolean deletePartitaByUtenteAndNome(long utenteId, String nomeSalvataggio) throws SQLException {
		String sql = "DELETE FROM partita_incompleta WHERE utente_id = ? AND nome_salvataggio = ?";
		try (Connection conn = GestoreDatabase.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, utenteId);
			ps.setString(2, nomeSalvataggio);
			ps.executeUpdate();
			return ps.getUpdateCount() > 0;
		}
	}

}
