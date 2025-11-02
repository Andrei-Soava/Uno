package onegame.server.eccezioni;

public class PartitaGiaFinitaException extends EccezionePartita {
	public PartitaGiaFinitaException() {
		super("La partita è già finita.");
	}
}
