package onegame.client.controllore.utils;

/**
 * classe che contiene wrappers utili in metodi della Partita
 * ma che centrano poco con essa
 */
public class Wrappers {

	/**
	 * classe ausiliaria per eseguire mappe<String,Integer> quando String può contenere duplicati
	 */
	public static class StringWrapper {
	    private final String value;
	    private final int id;

	    public StringWrapper(String value, int id) {
	        this.value = value;
	        this.id = id;
	    }
	    
	    public String getValue() {
	    	return this.value;
	    }
	}
	
	/**
	 * classe ausiliaria per immagazzinare informazioni utili di un giocatore
	 * (numero carte + se è il suo turno, dal punto di vista dello spettatore)
	 */
	public static class IntegerAndBooleanWrapper {
	    private final int numero;
	    private final boolean flag;

	    public IntegerAndBooleanWrapper(int numero, boolean flag) {
	        this.numero = numero;
	        this.flag = flag;
	    }

	    public int getNumero() {
	        return numero;
	    }

	    public boolean isFlag() {
	        return flag;
	    }
	}

}
