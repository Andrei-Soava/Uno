package modello;

import modello.carte.Carta;

public interface PartitaIF {
	public abstract Carta pescaCarta(); 
	public abstract void giocaCarta(Carta c);
}
