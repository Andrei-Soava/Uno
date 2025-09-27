package onegame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtenteDb {
	
	//verifica se un utente esiste di già tramite l'username
	public boolean esisteUtente(String username) throws SQLException{
		String sql = "SELECT 1 FROM utente WHERE username =?";
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		}
	}
	
	//registra un nuovo utente nel database
	public void registraUtente(String username, String passwordHash) throws SQLException{
		String sql = "INSERT INTO utente (username, password) VALUES (?,?)";
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, username);
			ps.setString(2, passwordHash);
			ps.executeUpdate();
		}
	}
	
	//recupera la password hash associata a un username
	public String getPasswordHash(String username) throws SQLException{
		String sql = "SELECT password FROM utente WHERE username=?";
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return rs.getString("password");
		}
		return null;
	}
	
	//recupera l'id dell'utente con quel username
	public long getIdByUsername(String username) throws SQLException{
		String sql = "SELECT id FROM utente WHERE username=?";
		try(Connection conn = GestoreDatabase.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if(rs.next())
				return rs.getLong("id");
		}
		return -1;
	}

}
