package onegame.server.gioco;

import java.util.ArrayList;
import java.util.List;

public class GiocatoreNET {
	private List<CartaNET> mano = new ArrayList<>();
	private String nickname;
	private boolean haDichiaratoUNO = false;
	private boolean isBot = false;

	public GiocatoreNET(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public boolean haDichiaratoUNO() {
		return haDichiaratoUNO;
	}

	public void setHaDichiaratoUNO(boolean haDichiaratoUNO) {
		this.haDichiaratoUNO = haDichiaratoUNO;
	}

	public List<CartaNET> getMano() {
		return mano;
	}

	public void aggiungiCarta(CartaNET carta) {
		mano.add(carta);
	}

	public void rimuoviCarta(CartaNET carta) {
		mano.remove(carta);
	}

	public boolean hasCarta(CartaNET carta) {
		return mano.contains(carta);
	}

	public void aggiungiCarte(List<CartaNET> carte) {
		mano.addAll(carte);
	}

	public int getNumeroCarte() {
		return mano.size();
	}

	public boolean isBot() {
		return isBot;
	}

	public void setBot(boolean isBot) {
		this.isBot = isBot;
	}

}
