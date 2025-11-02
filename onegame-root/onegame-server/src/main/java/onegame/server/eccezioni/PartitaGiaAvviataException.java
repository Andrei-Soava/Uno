package onegame.server.eccezioni;

public class PartitaGiaAvviataException extends EccezionePartita {
	public PartitaGiaAvviataException() {
		super("La partita è già in corso.");
	}
}
