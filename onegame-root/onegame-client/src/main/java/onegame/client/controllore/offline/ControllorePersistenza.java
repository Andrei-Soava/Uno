package onegame.client.controllore.offline;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.modello.Partita;
import onegame.modello.net.util.JsonHelper;

/**
 * classe DEFINITIVA per la gestione del salvataggio delle partite durante il gioco
 */
public class ControllorePersistenza {
	private ClientSocket cs;
	private ConnectionMonitor cm;
	
	String salvataggioCorrente;
	
	public ControllorePersistenza(ClientSocket cs, ConnectionMonitor cm) {
		this.cs=cs;
		this.cm=cm;
	}
	
	/**
	 * metodo per salvataggio di una nuova partita (da capire ancora come e quando farlo avvenire)
	 * @param nuovoSalvataggio
	 * @param partita
	 */
	void salvaPartita(String nuovoSalvataggio, Partita partita) {
		if(!cs.getUtente().isAnonimo() && cm.connectedProperty().get()) {
			cs.salvaPartita(nuovoSalvataggio, JsonHelper.toJson(partita), false, null);
		}
	}
	
	/**
	 * metodo per salvataggio di una automatico di una partita (in a salvataggio corrente)
	 * @param partita
	 */
	void salvaPartitaAutomatico(Partita partita) {
		if(salvataggioCorrente!=null && cm.connectedProperty().get()) {
			cs.salvaPartita(salvataggioCorrente, JsonHelper.toJson(partita), true, null);
		}
	}
	
	/**
	 * metodo che tenta di eliminare la partita corrispondente al salvataggio corrente
	 * (viene invocato alla fine di una partita SOLO se connesso E loggato)
	 */
	void eliminaPartita() {
		if(salvataggioCorrente!=null && cm.connectedProperty().get()) {
			cs.eliminaPartita(salvataggioCorrente, null);
		}
	}
	
	/**
	 * metodo per deserializzare una partita ed impostarla come partita del ControlloreGioco
	 * con adattamenti per ricollegare alcuni elementi
	 * 
	 * @param cg ControlloreGioco (serve solo per settargli la partita)
	 * @param nomeSalvataggio (passato da VistaSalvataggi e serve per fissare il focus attuale di quale partita verrà salvata man mano)
	 * @param partitaSerializzata (passato da VistaSalvataggi e contiente la serializzazione della partita presente nel db)
	 */
	void caricaPartita(ControlloreGioco cg, String nomeSalvataggio, String partitaSerializzata) {
		Partita partita = JsonHelper.fromJson(partitaSerializzata, Partita.class);
		cg.setPartita(partita);
		// Riaggancio il navigatore ai giocatori
		partita.getNavigatore().setItems(partita.getGiocatori());
		// Riaggancio mazzo alla sua pila
		partita.getMazzo().setPila(partita.getPilaScarti());
		salvataggioCorrente = nomeSalvataggio;
	}

}
