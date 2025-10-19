package onegame.modello.net;

public class GiocatoreDTO {
	public String username;
	public String nickname;
	public boolean anonimo;
	public int numeroCarteInMano;

	public GiocatoreDTO() {
	}

	public GiocatoreDTO(String username, String nickname, boolean anonimo, int numeroCarteInMano) {
		this.username = username;
		this.nickname = nickname;
		this.anonimo = anonimo;
		this.numeroCarteInMano = numeroCarteInMano;
	}
}
