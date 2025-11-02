package onegame.server.eccezioni;

public class ColoreNonValidoException extends EccezionePartita {
	public ColoreNonValidoException() {
		super("Il colore selezionato non è valido.");
	}
}
