package onegame.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import onegame.modello.net.StatoStanzaDTO;
import onegame.server.Sessione;
import onegame.server.Stanza;

public class DTOServerUtils {
	public static StatoStanzaDTO creaStanzaDTO(Stanza stanza) {
		if (stanza == null) {
			return null;
		}
		// Ottieni nicknames e usernames delle sessioni nella stanza
		List<String> nicknames = new ArrayList<>();
		List<String> usernames = new ArrayList<>();
		Set<Sessione> sessioni = stanza.getSessioni();
		for (Sessione s : sessioni) {
			nicknames.add(s.getNickname());
			usernames.add(s.getUsername());
		}
		
		StatoStanzaDTO dto = new StatoStanzaDTO(stanza.getCodice(), stanza.getMaxUtenti(), nicknames, usernames);
		return dto;
	}
}
