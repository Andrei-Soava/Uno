package onegame.server.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import onegame.modello.carte.Colore;
import onegame.modello.net.CartaDTO;
import onegame.modello.net.StatoStanzaDTO;
import onegame.server.Sessione;
import onegame.server.Stanza;
import onegame.server.gioco.CartaNET;

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

	public static CartaNET fromCartaDTOtoNET(CartaDTO dto) {
		if (dto == null || dto.colore == null)
			return null;

		if (dto.isCartaNumero) {
			return CartaNET.numero(dto.colore, dto.numero);
		} else if (dto.tipo != null) {
			return CartaNET.cartaSpeciale(dto.colore, dto.tipo);
		}
		return null;
	}

	public static CartaDTO fromCartaNETtoDTO(CartaNET cartaNET) {
		if (cartaNET == null)
			return null;

		if (cartaNET.isCartaNumero()) {
			return new CartaDTO(true, cartaNET.getNumero(), null, cartaNET.getColore());
		} else {
			return new CartaDTO(false, -1, cartaNET.getTipo(), cartaNET.getColore());
		}
	}

	public static List<CartaDTO> fromListaCarteNETtoDTO(List<CartaNET> carteNET) {
		List<CartaDTO> carteDTO = new ArrayList<>();
		for (CartaNET c : carteNET) {
			carteDTO.add(fromCartaNETtoDTO(c));
		}
		return carteDTO;
	}

}
