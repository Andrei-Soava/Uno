package modello;

import java.util.List;

/**
 * classe accessoria utile per la ciclazione dei giocatori durante la partita
 * 
 * metodi importanti:
 * next()--> cambia il giocatore corrente con quello successivo nella lista, e lo restituisce
 * back()--> cambia il giocatore corrente con quello precedente nella lista, e lo restituisce
 * current()--> restituisce il giocatore corrente
 * peekNext()--> restituisce il giocatore successivo nella lista, senza però aggiornare il il giocatore corrente
 * peekBack()--> restituisce il giocatore precedente nella lista, senza però aggiornare il il giocatore corrente
 * 
 * @param <T>
 */
public class Navigatore<T> {
    private List<T> items;
    private int currentIndex;

    public Navigatore(List<T> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La lista non può essere vuota");
        }
        this.items = items;
        this.currentIndex = 0; // Partiamo dal primo elemento
    }

    public T current() {
        return items.get(currentIndex);
    }

    public T next() {
        currentIndex = (currentIndex + 1) % items.size();
        return current();
    }
    
    public T peekNext() {
    	return items.get((currentIndex + 1) % items.size());
    }

    public T back() {
        currentIndex = (currentIndex - 1 + items.size()) % items.size();
        return current();
    }
    
    public T peekBack() {
    	return items.get((currentIndex - 1 + items.size()) % items.size());
    }

    public void setCurrent(T item) {
        int index = items.indexOf(item);
        if (index == -1) throw new IllegalArgumentException("Elemento non trovato");
        currentIndex = index;
    }
}

