package onegame.modello.net;

import java.util.List;

import onegame.modello.carte.Colore;

public class StatoPartitaDTO {
	public CartaDTO cartaCorrente; // carta scoperta sul mazzo degli scarti
	public Colore coloreCorrente; // colore corrente
	public List<GiocatoreDTO> giocatori; // lista di tutti i giocatori nella partita
	public int indiceGiocatoreCorrente; // indice del giocatore che deve giocare
	public boolean direzioneCrescente;
	public boolean partitaTerminata;
	public int indiceVincitore; // se partitaTerminata == true

	public StatoPartitaDTO() {
	}

	public StatoPartitaDTO(CartaDTO cartaCorrente, Colore coloreCorrente, List<GiocatoreDTO> giocatori,
			int indiceGiocatoreCorrente, boolean direzioneCrescente, boolean partitaTerminata, int indiceVincitore) {
		this.cartaCorrente = cartaCorrente;
		this.coloreCorrente = coloreCorrente;
		this.giocatori = giocatori;
		this.indiceGiocatoreCorrente = indiceGiocatoreCorrente;
		this.direzioneCrescente = direzioneCrescente;
		this.partitaTerminata = partitaTerminata;
		this.indiceVincitore = indiceVincitore;
	}

}
