package onegame.server.db;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onegame.server.GestoreConnessioni;

/**
 * Crea e gestisce la connessione a un database H2 in modalità embedded.
 */
public class GestoreDatabase {
	// private static final String URL = "jdbc:h2:/uno-game-db;AUTO_SERVER=TRUE";
	private static final String USER = "root";
	private static final String PWD = "root";
	private static final String DB_DIR = "./target/db";
	private static final String DB_NAME = "uno-game-db";
	
	private static final Logger logger = LoggerFactory.getLogger(GestoreDatabase.class);

	private static final String[] DDL = {

	};

	private static String buildUrl() {
		File dir = new File(DB_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return "jdbc:h2:file:" + DB_DIR + "/" + DB_NAME + ";AUTO_SERVER=TRUE";
	}

	/**
	 * Restituisce una connessione JDBC attiva verso il database H2. Se il database non
	 * esiste, lo crea automaticamente.
	 *
	 * @return {@link Connection} aperta verso il database
	 * @throws SQLException in caso di errori di connessione
	 */
	public static Connection getConnection() throws SQLException {
		String url = buildUrl();
		return DriverManager.getConnection(url, USER, PWD);
	}

	/**
     * Inizializza il database con le tabelle necessarie.
     *
     * @throws SQLException in caso di errori SQL durante l'esecuzione
     * @throws Exception in caso di errori generici
     */
	public static void inizializzaDatabase() {
		try (Connection conn = getConnection();
				Statement st = conn.createStatement()) {
			//Tabella utente
			st.executeUpdate("CREATE TABLE IF NOT EXISTS utente (" +
			        "id IDENTITY PRIMARY KEY," +
			        "username VARCHAR(50) UNIQUE NOT NULL," +
			        "password VARCHAR(200) NOT NULL," +
			        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
			        ")");
			
			//Tabella partita incompleta
			st.executeUpdate("CREATE TABLE IF NOT EXISTS partita_incompleta("+
					"id IDENTITY PRIMARY KEY,"+
					"utente_id BIGINT NOT NULL,"+
					"nome_salvataggio VARCHAR(100) NOT NULL,"+
					"partita_serializzata CLOB NOT NULL,"+
					"created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
					"FOREIGN KEY (utente_id) REFERENCES utente(id) ON DELETE CASCADE"+
					")");
		} catch (SQLException e) {
			logger.error("Errore durante l'inizializzazione del database: {}", e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		inizializzaDatabase();
	}
}
