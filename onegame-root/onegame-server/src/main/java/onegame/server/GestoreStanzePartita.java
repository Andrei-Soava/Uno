package onegame.server;

public class GestoreStanzePartita extends GestoreStanze<StanzaPartita> {

	public GestoreStanzePartita() {
		super();
	}

	@Override
	protected StanzaPartita creaStanza(int codice, long id, String nome, int maxUtenti) {
		return new StanzaPartita(codice, id, nome, maxUtenti);
	}
}
