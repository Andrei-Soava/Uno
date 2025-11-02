package onegame.server.eccezioni;

public class MossaNonValidaException extends EccezionePartita {
	public MossaNonValidaException() {
		super("La mossa effettuata non è valida.");
	}

//	public MossaNonValidaException(String message) {
//		super(message);
//	}
}
