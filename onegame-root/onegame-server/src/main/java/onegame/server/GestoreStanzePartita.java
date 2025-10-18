package onegame.server;

public class GestoreStanzePartita extends GestoreStanze<StanzaPartita> {

	public GestoreStanzePartita(GestoreSessioni gestoreSessioni) {
		super(gestoreSessioni);
	}

	@Override
	protected StanzaPartita creaStanza(int codice, long id, String nome, int maxUtenti,
			GestoreSessioni gestoreSessioni) {
		StanzaPartita stanza = new StanzaPartita(codice, id, nome, maxUtenti, gestoreSessioni);
		return stanza;
	}
}
