package onegame.server;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestoreDatabase {
	// private static final String URL = "jdbc:h2:/uno-game-db;AUTO_SERVER=TRUE";
	private static final String USER = "root";
	private static final String PWD = "root";
	private static final String DB_DIR = "./target/db";
	private static final String DB_NAME = "uno-game-db";

	private static String buildUrl() {
		File dir = new File(DB_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return "jdbc:h2:file:" + DB_DIR + "/" + DB_NAME + ";AUTO_SERVER=TRUE";
	}

	public static Connection getConnection() throws SQLException {
		String url = buildUrl();
		return DriverManager.getConnection(url, USER, PWD);
	}

	public static void main(String[] args) throws Exception {
		Connection conn = getConnection();
		Statement st = conn.createStatement();
		st.executeUpdate("CREATE TABLE users (" + " id IDENTITY PRIMARY KEY," + " username VARCHAR(50) UNIQUE NOT NULL,"
				+ " password VARCHAR(100) NOT NULL," + " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + ")");

	}
}
