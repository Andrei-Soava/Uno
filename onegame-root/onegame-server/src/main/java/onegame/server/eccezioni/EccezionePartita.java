package onegame.server.eccezioni;

/**
 * Classe astratta per le eccezioni relative alla partita.
 */
public abstract class EccezionePartita extends Exception {

	public EccezionePartita(String message) {
		super(message);
	}
}
