package onegame.client.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
	private final Properties properties = new Properties();

	public ConfigLoader(String filename) {
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
			if (input == null) {
				throw new RuntimeException("File di configurazione non trovato: " + filename);
			}
			properties.load(input);
		} catch (IOException e) {
			throw new RuntimeException("Errore durante il caricamento del file di configurazione", e);
		}
	}

	public String getServerHost() {
		return properties.getProperty("server.host");
	}
}
