package onegame.server.eccezioni;

public class GiocatoriInsufficientiException extends EccezionePartita {
	public GiocatoriInsufficientiException() {
		super("Numero minimo di giocatori non raggiunto.");
	}
}
