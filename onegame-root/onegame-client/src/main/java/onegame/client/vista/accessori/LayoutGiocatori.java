package onegame.client.vista.accessori;

import java.util.List;
import java.util.ArrayList;

/**
 * enumerato che contiene le varie posizioni dei giocatori in VistaPartita
 * in base al numero dei giocatori della partita
 * --> SERVE SOLO PER COSTRUIRE LA DISPOSZIONE DEI GIOCATORI AGEVOLMENTE
 * 
 * contiene una lista con le posizioni delle caselle per VistaPartita
 */
public enum LayoutGiocatori {
	DUE(1), TRE(2), QUATTRO(3), CINQUE(4), SEI(5), SETTE(6), OTTO(7), NOVE(8), DIECI(9);
	
	private ArrayList<Integer> posizioniCaselle = new ArrayList<>();
	
	/**
	 * @param i, indica il numero di AVVERSARI dalla prospettiva del giocatore
	 */
	LayoutGiocatori(int i) {
		switch(i) {
		case 1:
			posizioniCaselle.addAll(List.of(2));
			break;
			
		case 2:
			posizioniCaselle.addAll(List.of(5,8));
			break;
			
		case 3:
			posizioniCaselle.addAll(List.of(6,2,9));
			break;
			
		case 4:
			posizioniCaselle.addAll(List.of(6,1,3,9));
			break;
			
		case 5:
			posizioniCaselle.addAll(List.of(7,5,2,8,10));
			break;
			
		case 6:
			posizioniCaselle.addAll(List.of(7,5,1,3,8,10));
			break;
			
		case 7:
			posizioniCaselle.addAll(List.of(7,6,5,2,8,9,10));
			break;
			
		case 8:
			posizioniCaselle.addAll(List.of(7,6,5,1,3,8,9,10));
			break;
			
		case 9:
			posizioniCaselle.addAll(List.of(7,6,5,1,2,3,8,9,10));
			break;
		}
	}
	
	/**
	 * metodo che mi restituisce, a partire da un indice, la posizione della casella in VistaPartita
	 * (per quel numero di giocatori)
	 * @param index, elemento della lista a cui si vuole accedere
	 * @return posizione della casella
	 */
	public int getPosizioneCasella(int index) {
		if(index<0 || (index>posizioniCaselle.size()-1))
			return -1;
		else
			return posizioniCaselle.get(index);
	}

}
