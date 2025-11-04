package onegame.server.gioco;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Rappresenta un mazzo di carte nel gioco.
 */
public class MazzoNET {
	private final Stack<CartaNET> carte = new Stack<>();
	private final MazzoFactory factory;

	public MazzoNET(MazzoFactory factory) {
		this.factory = factory;
		ricostruisciMazzo();
	}

	public void ricostruisciMazzo() {
		carte.clear();
		carte.addAll(factory.creaCarte());
	}

	public CartaNET pesca() {
		if (this.isVuoto())
			ricostruisciMazzo();
		if (this.isVuoto()) {
			return null;
		}
		return carte.pop();
	}

	public List<CartaNET> pescaN(int n) {
		List<CartaNET> temp = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			temp.add(pesca());
		}
		return temp;
	}

	@JsonIgnore
	public boolean isVuoto() {
		return carte.size() == 0;
	}
}
