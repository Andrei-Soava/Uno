package onegame.client.persistenza_temporanea;

import com.fasterxml.jackson.databind.ObjectMapper;

import onegame.modello.Partita;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * classe TEMPORANEA per la gestione dei salvataggi a livello LOCALE
 * metodi principali (dovranno essere implementate le loro funzionalità nella versione client-server):
 * -salvaPartita--> data una partita ed un nome di salvataggio, serializza la partita come json e la salva con quel nome di file
 * -caricaPartita--> dato un nome di salvataggio, deserializza il file json nella partita corrispondente
 * -rinominaSalvataggio--> procede, se possibile, alla rinomina di un file
 * -eliminaSalvataggio-->procede, se possibile, alla eliminazione di un file
 * -listaSalvataggi-->fornisce la lista dei salvataggi presenti
 */
public class ManagerPersistenza {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String SAVE_DIR = "saves";

    static {
        // Crea la cartella se non esiste
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public static void salvaPartita(Partita partita, String nomeFile) throws IOException {
        File file = new File(SAVE_DIR, nomeFile + ".json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, partita);
    }

    public static Partita caricaPartita(String nomeFile) throws IOException {
        File file = new File(SAVE_DIR, nomeFile + ".json");
        return mapper.readValue(file, Partita.class);
    }
    
    public static boolean rinominaSalvataggio(String nomeVecchio, String nomeNuovo) {
        File dir = new File(SAVE_DIR);
        File fileVecchio = new File(dir, nomeVecchio + ".json");
        File fileNuovo = new File(dir, nomeNuovo + ".json");

        // Controlli di sicurezza
        if (!fileVecchio.exists()) return false;
        if (fileNuovo.exists()) return false; // evita di sovrascrivere

        return fileVecchio.renameTo(fileNuovo);
    }
    
    public static boolean verificaRinominaSalvataggio(String nomeVecchio, String nomeNuovo) {
    	File dir = new File(SAVE_DIR);
        File fileVecchio = new File(dir, nomeVecchio + ".json");
        File fileNuovo = new File(dir, nomeNuovo + ".json");

        // Controlli di sicurezza
        if (!fileVecchio.exists()) return false;
        if (fileNuovo.exists()) return false; // evita di sovrascrivere

        return true;
    }

    public static boolean eliminaSalvataggio(String nomeFile) {
    	File dir = new File(SAVE_DIR);
    	File file = new File(dir, nomeFile + ".json");
    	
    	if (!file.exists()) {
    		return false; // il file non esiste
    	}
    	return file.delete();
    }

    public static List<String> listaSalvataggi() {
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) return List.of();
        return Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".json")))
                .map(f -> f.getName().replace(".json", ""))
                .collect(Collectors.toList());
    }
    

}

