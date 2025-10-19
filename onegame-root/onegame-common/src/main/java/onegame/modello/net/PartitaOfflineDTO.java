package onegame.modello.net;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PartitaOfflineDTO {
	public List<String> usernames;
	public List<ArrayList<CartaDTO>> mani;
	public List<CartaDTO> scarti;
	public CartaDTO topCarta;
	public boolean direzione;
	public int indiceGiocatoreCorrente;

	public PartitaOfflineDTO() {
	}

	public PartitaOfflineDTO(List<String> usernames, List<ArrayList<CartaDTO>> mani, List<CartaDTO> scarti,
			CartaDTO topCarta, boolean direzione, int indiceGiocatoreCorrente) {
		this.usernames = usernames;
		this.mani = mani;
		this.scarti = scarti;
		this.topCarta = topCarta;
		this.direzione = direzione;
		this.indiceGiocatoreCorrente = indiceGiocatoreCorrente;
	}

	
}
