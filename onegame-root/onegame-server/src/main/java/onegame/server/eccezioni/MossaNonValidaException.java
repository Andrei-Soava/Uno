package onegame.server.eccezioni;

public class MossaNonValidaException extends EccezionePartita {
	public MossaNonValidaException() {
		super("Mossa effettuata non valida.");
	}

//	public MossaNonValidaException(String message) {
//		super(message);
//	}
}
