package persistenza_temporanea;

import onegame.modello.Partita;

/**
 * interfaccia per gestione della persistenza
 */
public interface InterfacciaPersistenza {
	public void salvaPartita(Partita partita, String salvataggio);
	public Partita caricaPartita(String salvataggio);
	public boolean rinominaSalvataggio(String nomeVecchio, String nomeNuovo);
	public boolean eliminaSalvataggio(String nomeFile);
}
