package onegame.modello.net;

import java.util.List;

public class StatoStanzaDTO {
	public int codiceStanza;
	public int maxUtenti;
	public List<String> nicknames;

	public StatoStanzaDTO() {
	}

	public StatoStanzaDTO(int codiceStanza, int maxUtenti, List<String> nicknames) {
		this.codiceStanza = codiceStanza;
		this.maxUtenti = maxUtenti;
		this.nicknames = nicknames;
	}

}
