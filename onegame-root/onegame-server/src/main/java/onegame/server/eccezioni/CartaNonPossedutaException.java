package onegame.server.eccezioni;

public class CartaNonPossedutaException extends EccezionePartita {

	public CartaNonPossedutaException() {
		super("Carta non posseduta dal giocatore.");
	}
}
