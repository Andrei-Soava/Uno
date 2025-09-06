package modello;

import java.util.List;

/**
 * classe accessoria utile per la ciclazione dei giocatori durante la partita
 * 
 * metodi importanti:
 * prossimo()--> cambia il giocatore corrente con quello successivo nella lista, e lo restituisce
 * precedente()--> cambia il giocatore corrente con quello precedente nella lista, e lo restituisce
 * corrente()--> restituisce il giocatore corrente
 * vediProssimo()--> restituisce il giocatore successivo nella lista, senza però aggiornare il il giocatore corrente
 * vediPrecedente()--> restituisce il giocatore precedente nella lista, senza però aggiornare il il giocatore corrente
 * 
 * @param <T>
 */
public class Navigatore<T> {
    private List<T> items;
    private int indiceCorrente;

    public Navigatore(List<T> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La lista non può essere vuota");
        }
        this.items = items;
        this.indiceCorrente = 0; // Partiamo dal primo elemento
    }

    public T corrente() {
        return items.get(indiceCorrente);
    }

    public T prossimo() {
        indiceCorrente = (indiceCorrente + 1) % items.size();
        return corrente();
    }
    
    public T vediProssimo() {
    	return items.get((indiceCorrente + 1) % items.size());
    }

    public T precedente() {
        indiceCorrente = (indiceCorrente - 1 + items.size()) % items.size();
        return corrente();
    }
    
    public T vediPrecedente() {
    	return items.get((indiceCorrente - 1 + items.size()) % items.size());
    }

    public void setCorrente(T item) {
        int index = items.indexOf(item);
        if (index == -1) throw new IllegalArgumentException("Elemento non trovato");
        indiceCorrente = index;
    }
}

