package onegame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartitaIncompletaDb {
	
	//salva una nuova partita incompleta nel db
	public void createPartita(String utenteId, String nome, String partitaSerializzata) throws SQLException{
		String sql = "INSERT INTO partita_incompleta (utente_id, nome_salvataggio, partita_serializzata) VALUES (?, ?, ?)";
		try(Connection conn = GestoreDatabase.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, utenteId);
			ps.setString(2, nome);
			ps.setString(3, partitaSerializzata);
			ps.executeUpdate();
		}
	}
	
	//Recupera tutti i nomi dei salvataggi associati a un utente
	public List<String> getPartiteByUtente(String string) throws SQLException{
		String sql = "SELECT nome_salvataggio FROM partita_incompleta WHERE utente_id=?";
		List<String> result = new ArrayList<>();
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, string);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString("nome_salvataggio"));
			}
		}
		return result;
	}
	
	//Recupera la partita serializzata usando l’ID del salvataggio
	public String getPartitaById(String string) throws SQLException{
		String sql = "SELECT partita_serializzata FROM partita_incompleta WHERE id=?";
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, string);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) 
				return rs.getString("partita_serializzata");
		}
		return null;
	}
	
	//Aggiorna il nome del salvataggio per una partita incompleta
	public void updateNomeSalvataggio(String id,String nuovoNome) throws SQLException{
		String sql = "UPDATE partita_incompleta SET nome_salvataggio=? WHERE id=?";
		try (Connection conn = GestoreDatabase.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	          ps.setString(1, nuovoNome);
	          ps.setString(2, id);
	          ps.executeUpdate();
	     }
	}
	
	//Elimina una partita incompleta dal database
	public void deletePartita(String id) throws SQLException{
		String sql = "DELETE FROM partita_incompleta WHERE id=?";
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, id);
			ps.executeUpdate();
		}
	}

}
 