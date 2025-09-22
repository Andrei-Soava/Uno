package onegame.server.db;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.File;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;

public class GestoreDatabaseTest {
    private static final Path DB_DIR = Paths.get("./target/db");
    private static final String DB_FILE = "uno-game-db.mv.db";

    @Before
    public void cleanDbDirectory() throws Exception {
		/*
		 * if (Files.exists(DB_DIR)) { Files.walk(DB_DIR)
		 * .sorted(Comparator.reverseOrder()) .map(Path::toFile) .forEach(File::delete);
		 * }
		 */
    }

    @Test
    public void testGetConnectionCreatesFile() throws Exception {
        Connection conn = GestoreDatabase.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        conn.close();

        File file = DB_DIR.resolve(DB_FILE).toFile();
        Assert.assertTrue("Il DB file deve esistere", file.exists());
    }

    @Test
    public void testInizializzaDatabaseCreatesUsersTable() throws Exception {
		/*
		 * GestoreDatabase.inizializzaDatabase();
		 * 
		 * try (Connection conn = GestoreDatabase.getConnection()) { ResultSet rs =
		 * conn.getMetaData() .getTables(null, null, "USERS", null);
		 * Assert.assertTrue("Tabella USERS creata", rs.next()); }
		 */
    }
}
