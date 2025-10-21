package onegame.modello.net;

import java.util.List;

public class StatoStanzaDTO {
	public int codiceStanza;
	public int maxUtenti;
	public List<String> nicknames;
	public List<String> usernames;

	public StatoStanzaDTO() {
	}

	public StatoStanzaDTO(int codiceStanza, int maxUtenti, List<String> nicknames, List<String> usernames) {
		this.codiceStanza = codiceStanza;
		this.maxUtenti = maxUtenti;
		this.nicknames = nicknames;
		this.usernames = usernames;
	}

}
