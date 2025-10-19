package onegame.modello.net;

import java.util.List;

public class StatoPartitaDTO {
	public CartaDTO cartaCorrente;
	public List<GiocatoreDTO> giocatori;
	public int indiceGiocatoreCorrente;
	public List<CartaDTO> carteInMano;
	public boolean direzioneCrescente;
	public boolean partitaTerminata;
	public int indiceVincitore; // se partitaTerminata == true

	public StatoPartitaDTO() {
	}

	public StatoPartitaDTO(CartaDTO cartaCorrente, List<GiocatoreDTO> giocatori, int indiceGiocatoreCorrente,
			List<CartaDTO> carteInMano, boolean direzioneCrescente, boolean partitaTerminata, int indiceVincitore) {
		this.cartaCorrente = cartaCorrente;
		this.giocatori = giocatori;
		this.indiceGiocatoreCorrente = indiceGiocatoreCorrente;
		this.carteInMano = carteInMano;
		this.direzioneCrescente = direzioneCrescente;
		this.partitaTerminata = partitaTerminata;
		this.indiceVincitore = indiceVincitore;
	}
}
