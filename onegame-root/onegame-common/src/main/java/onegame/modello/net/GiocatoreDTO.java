package onegame.modello.net;

public class GiocatoreDTO {
	public String nickname;
	public int numeroCarteInMano;

	public GiocatoreDTO() {
	}

	public GiocatoreDTO(String nickname, int numeroCarteInMano) {
		this.nickname = nickname;
		this.numeroCarteInMano = numeroCarteInMano;
	}
}
