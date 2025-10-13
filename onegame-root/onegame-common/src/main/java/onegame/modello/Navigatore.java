package onegame.modello;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	@JsonIgnore
    private List<T> items;
    private int indiceCorrente;
    
    @JsonIgnore
    public List<T> getItems() {
		return items;
	}

    @JsonIgnore
	public void setItems(List<T> items) {
		this.items = items;
	}

	public int getIndiceCorrente() {
		return indiceCorrente;
	}

	public void setIndiceCorrente(int indiceCorrente) {
		this.indiceCorrente = indiceCorrente;
	}


    //costruttore vuoto per Jackson
    public Navigatore() {items=null; indiceCorrente=0;}
    
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
    
    @Deprecated //per adesso
    public List<T> altriInOrdine() {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("La lista è vuota o non inizializzata");
        }

        List<T> result = new java.util.ArrayList<>();
        int size = items.size();

        if (size == 1) {
            return result; // se c'è un solo elemento, non ci sono "altri"
        }

        int index = indiceCorrente;

        for (int i = 1; i < size; i++) {
            //if (direzione) {
            index = (index + 1) % size; // scansiona in avanti
            //} else {
            //    index = (index - 1 + size) % size; // scansiona all’indietro
            //}
            result.add(items.get(index));
        }

        return result;
    }
    
    public List<T> altriInSuccessione(T item) {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("La lista è vuota o non inizializzata");
        }

        int size = items.size();
        List<T> result = new ArrayList<>();

        if (size == 1) {
            return result; // se c'è un solo elemento, non ci sono "altri"
        }

        int startIndex = items.indexOf(item);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Elemento non trovato nella lista");
        }

        int index = startIndex;
        for (int i = 1; i < size; i++) {
            index = (index + 1) % size; // scansiona in avanti
            result.add(items.get(index));
        }

        return result;
    }


}

