package onegame.client.controllore.online.utils;

import java.util.ArrayList;
import java.util.List;

import onegame.modello.carte.*;
import onegame.modello.net.CartaDTO;abstract
public class CartaUtils {

	public static List<Carta> convertiCartaDTOinCarta(List<CartaDTO> carteDTO){
		List<Carta> carte=new ArrayList<>();
		for(CartaDTO c:carteDTO) {
			if(c.isCartaNumero)
				carte.add(Carta.numero(c.colore, c.numero));
			else {
				switch(c.tipo) {
				case PIU_DUE:
					carte.add(Carta.pescaDue(c.colore));
					break;
				case PIU_QUATTRO:
					carte.add(Carta.pescaQuattro());
					break;
				case INVERTI:
					carte.add(Carta.cambioGiro(c.colore));
					break;
				case BLOCCA:
					carte.add(Carta.skip(c.colore));
					break;
				case JOLLY:
					carte.add(Carta.cambioColore());
					break;
				}
			}
		}
		return carte;
	}
	
	public static Carta convertiCartaDTOinCarta(CartaDTO cartaDTO) {
		return convertiCartaDTOinCarta(List.of(cartaDTO)).get(0);
	}

}
