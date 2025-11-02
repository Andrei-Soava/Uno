package onegame.server.eccezioni;

public class TurnoNonValidoException extends EccezionePartita {
	public TurnoNonValidoException() {
		super("Non è il tuo turno di giocare.");
	}
}
