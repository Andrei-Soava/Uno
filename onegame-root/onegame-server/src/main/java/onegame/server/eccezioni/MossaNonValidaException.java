package onegame.server.eccezioni;

public class MossaNonValidaException extends EccezionePartita {
	// @formatter:off
	public enum TipoMossaNonValida {
		CARTA_NON_GIOCABILE,
		CARTA_NON_POSSEDUTA,
		CARTA_GIOCATA_NON_VALIDA_DOPO_PESCA,
		GIOCATORE_NON_TURNO,
		GIOCATORE_DEVE_PESCARE,
		GIOCATORE_HA_GIA_GIOCATO,
		GIOCATORE_HA_GIA_PESCATO,
		DICHIARAZIONE_UNO_NON_VALIDA,
		COLORE_SCELTO_NON_VALIDO
	}
	// @formatter:on

	public final TipoMossaNonValida tipo;

	public MossaNonValidaException(TipoMossaNonValida tipo) {
		super("Mossa effettuata non valida: " + tipo.toString());
		this.tipo = tipo;
	}
}
