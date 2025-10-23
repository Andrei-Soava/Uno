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
		// Ottieni nicknames e usernames delle sessioni nella stanza e l'indice del proprietario
		List<String> nicknames = new ArrayList<>();
		List<String> usernames = new ArrayList<>();
		Set<Sessione> sessioni = stanza.getSessioni();
		int i = 0;
		int indiceProprietario = -1;
		for (Sessione s : sessioni) {
			nicknames.add(s.getNickname());
			usernames.add(s.getUsername());
			if (s.equals(stanza.getProprietario())) {
				indiceProprietario = i;
			}
			i++;
		}

		StatoStanzaDTO dto = new StatoStanzaDTO(stanza.getCodice(), stanza.getMaxSessioni(), nicknames, usernames,
				indiceProprietario);
		return dto;
	}
}
