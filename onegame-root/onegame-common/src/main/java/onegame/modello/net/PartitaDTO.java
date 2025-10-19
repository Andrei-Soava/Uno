package onegame.modello.net;

import java.util.List;

public class PartitaDTO {
	public String idPartita;
	public CartaDTO cartaCorrente;
	public List<GiocatoreDTO> giocatori;
	public int indiceGiocatoreCorrente;
	public List<CartaDTO> carteInMano;
	public boolean partitaTerminata;
	public String vincitoreId; // idGiocatore se partitaTerminata == true

	public PartitaDTO() {
	}

	public PartitaDTO(String idPartita, CartaDTO cartaCorrente, List<GiocatoreDTO> giocatori,
			int indiceGiocatoreCorrente, List<CartaDTO> carteInMano, boolean partitaTerminata, String vincitoreId) {
		this.idPartita = idPartita;
		this.cartaCorrente = cartaCorrente;
		this.giocatori = giocatori;
		this.indiceGiocatoreCorrente = indiceGiocatoreCorrente;
		this.carteInMano = carteInMano;
		this.partitaTerminata = partitaTerminata;
		this.vincitoreId = vincitoreId;
	}
}
