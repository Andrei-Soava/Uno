package onegame.modello.net;

import java.util.ArrayList;
import java.util.List;

import onegame.modello.Partita;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.giocatori.Giocatore;

public class DTOUtils {
	public static CartaDTO convertiCartaInDTO(Carta carta) {
		if (carta == null) {
			return null;
		}
		if (carta instanceof CartaNumero cartaNumero) {
			return new CartaDTO(true, cartaNumero.getNumero(), null, cartaNumero.getColore());
		}
		if (carta instanceof CartaSpeciale cartaSpeciale) {
			return new CartaDTO(false, -1, cartaSpeciale.getTipo(), cartaSpeciale.getColore());
		}

		return null;
	}

	public static List<CartaDTO> convertiListaCarteInDTO(List<Carta> carte) {
		List<CartaDTO> carteDTO = new ArrayList<>();
		for (Carta carta : carte) {
			carteDTO.add(convertiCartaInDTO(carta));
		}
		return carteDTO;
	}

	public static Carta convertiDTOinCarta(CartaDTO dto) {
		if (dto == null)
			return null;
		if (dto.tipo != null) {
			return new CartaSpeciale(dto.colore, dto.tipo);
		} else {
			return new CartaNumero(dto.colore, dto.numero);
		}
	}
	
	public static List<Carta> convertiListaDTOinCarte(List<CartaDTO> carteDTO){
		List<Carta> carte = new ArrayList<>();
		for(CartaDTO c : carteDTO) {
			carte.add(convertiDTOinCarta(c));
		}
		return carte;
	}

	@Deprecated
	public static PartitaOfflineDTO creaPartitaOfflineDTO(Partita partita) {
		boolean direzione = partita.getDirezione();
		int indiceGiocatoreCorrente = partita.getNavigatore().getIndiceCorrente();
		CartaDTO topCarta = convertiCartaInDTO(partita.getCartaCorrente());

		List<String> usernames = new ArrayList<String>();
		List<ArrayList<CartaDTO>> mani = new ArrayList<ArrayList<CartaDTO>>();
		for (Giocatore g : partita.getGiocatori()) {
			usernames.add(g.getNome());
			ArrayList<CartaDTO> mano = new ArrayList<>();
			for (Carta c : g.getMano().getCarte()) {
				mano.add(convertiCartaInDTO(c));
			}
			mani.add(mano);
		}

		List<CartaDTO> scarti = new ArrayList<CartaDTO>();
		for (Carta c : partita.getPilaScarti().getScarti()) {
			scarti.add(convertiCartaInDTO(c));
		}

		return new PartitaOfflineDTO(usernames, mani, scarti, topCarta, direzione, indiceGiocatoreCorrente);

	}

// @formatter:off
	public static StatoStanzaDTO clone(StatoStanzaDTO dto) {
	    return new StatoStanzaDTO(
	        dto.codiceStanza,
	        dto.maxUtenti,
	        dto.nicknames != null ? List.copyOf(dto.nicknames) : null,
	        dto.usernames != null ? List.copyOf(dto.usernames) : null,
	        dto.indiceProprietario
	    );
	}
// @formatter:on

}
