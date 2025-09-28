package onegame.modello.net;


public class GiocatoreDTO {
    public String idGiocatore;
    public String username;
    public boolean anonimo;
    public int numeroCarteInMano;

    public GiocatoreDTO() {}

	public GiocatoreDTO(String idGiocatore, String username, boolean anonimo, int numeroCarteInMano) {
		this.idGiocatore = idGiocatore;
		this.username = username;
		this.anonimo = anonimo;
		this.numeroCarteInMano = numeroCarteInMano;
	}
}
