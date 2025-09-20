package controllore;

import java.io.IOException;
import java.util.List;

import modello.Partita;
import persistenza_temporanea.InterfacciaPersistenza;
import persistenza_temporanea.ManagerPersistenza;
import vista.InterfacciaVistaTemporanea;
import vista.VistaGioco;
import vista.VistaTemporanea;

/**
 * classe che si occupa di salvare/caricare partite (invocando l'opportuno gestore della persistenza--> db o file manager)
 * 
 * attributi importanti: 
 * -salvataggioCorrente--> nome del file appena salvato/caricato 
 * -persistenzaIF--> interfaccia per gestore persistenza
 * 
 * metodi importanti: 
 * -salvaPartita--> permette di salvare partita scegliendo il nome del salvataggio (per ora non utilizzato)
 * -salvaPartitaAutomatico--> invocato da ControlloreGioco durante esecuzione del gioco
 * -caricaPartita--> permette di scegliere tra le partite salvate una da caricare e quindi avviare
 * -rinominaSalvataggio--> permette di scegliere tra i salvataggi uno e rinominarlo
 */
public class ControllorePersistenza {
	private String salvataggioCorrente;
	private InterfacciaPersistenza persistenzaIF;

	public ControllorePersistenza(InterfacciaPersistenza persitenzaIF) {
		// da utilizzare quando ci sarà un interfaccia con database per gestione delle partite--> al momento si usa ManagerPersistenza
		this.persistenzaIF = persistenzaIF;
	}

	public void setSalvataggioCorrente(String s) {
		this.salvataggioCorrente = s;
	}
	
	public void setSalvataggioCorrente() {
		this.salvataggioCorrente= getNomeDisponibile();
	}

	public String getSalvataggioCorrente() {
		return this.salvataggioCorrente;
	}

	//in teoria da utilizzare solo quando si abbandona la partita--> per ora non serve considerarlo
	public void salvaPartita(ControlloreGioco cg) {
		VistaGioco tv = cg.getTv();
		Partita partita = cg.getPartita();
		String nome;
		do {
			nome = tv.inserisciStringa("Inserisci un nome per il salvataggio:");
			if (isNomeDisponibile(nome))
				break;
			else
				tv.stampaMessaggio("Nome salvataggio già presente. Riprova.");
		} while (true);
		try {
			ManagerPersistenza.salvaPartita(partita, nome);
			tv.stampaMessaggio("Partita salvata come '" + nome + "'");
			salvataggioCorrente = nome;
		} catch (IOException e) {
			tv.stampaMessaggio("Errore nel salvataggio: " + e.getMessage());
		}
	}

	public void salvaPartitaAutomatico(ControlloreGioco cg) {
		VistaGioco tv = cg.getTv();
		Partita partita = cg.getPartita();
		//sarà da fare solo se giocatore è loggato
		try {
			ManagerPersistenza.salvaPartita(partita, salvataggioCorrente);
			tv.stampaMessaggio("Partita salvata con successo!");
		} catch (IOException e) {
			tv.stampaMessaggio("Errore nel salvataggio: " + e.getMessage());
		}
	}

	public void caricaPartita(ControlloreGioco cg) {
		VistaGioco tv = cg.getTv();
		Partita partita = cg.getPartita();
		List<String> salvataggi = ManagerPersistenza.listaSalvataggi();
		if (salvataggi.isEmpty()) {
			tv.stampaMessaggio("Nessun salvataggio trovato.");
			return;
		}

		//mostra elenco salvataggi
		StringBuilder sb = new StringBuilder("Salvataggi disponibili:\n");
		for (int i = 0; i < salvataggi.size(); i++) {
			sb.append(i).append(") ").append(salvataggi.get(i)).append("\n");
		}
		int scelta = tv.scegliTraN(sb.toString(), 0, salvataggi.size() - 1);
		String nomeFile = salvataggi.get(scelta);

		try {
			partita = ManagerPersistenza.caricaPartita(nomeFile);
			cg.setPartita(partita);
			// Riaggancio il navigatore ai giocatori
			partita.getNavigatore().setItems(partita.getGiocatori());
			// Riaggancio l'interfaccia partita ai giocatori
			ControlloreGioco.setPartitaIF(partita.getGiocatori(), partita);
			// Riaggancio mazzo alla sua pila
			partita.getMazzo().setPila(partita.getPilaScarti());
			tv.stampaMessaggio("Partita '" + nomeFile + "' caricata con successo!");
			salvataggioCorrente = nomeFile;
		} catch (IOException e) {
			tv.stampaMessaggio("Errore nel caricamento: " + e.getMessage());
		}
	}

	private String trovaSalvataggio(String nome) {
		List<String> salvataggi = ManagerPersistenza.listaSalvataggi();
		if (salvataggi.isEmpty()) {
			return null;
		}

		for (String s : salvataggi) {
			if (s.equals(nome))
				return s;
		}
		return null;
	}

	public void rinominaSalvataggio(ControlloreGioco cg) {
		VistaGioco tv = cg.getTv();
		List<String> salvataggi = ManagerPersistenza.listaSalvataggi();
		if (salvataggi.isEmpty()) {
			tv.stampaMessaggio("Nessun salvataggio trovato.");
			return;
		}

		// Mostra elenco salvataggi
		StringBuilder sb = new StringBuilder("Scegli salvataggio da rinominare. Salvataggi disponibili:\n");
		for (int i = 0; i < salvataggi.size(); i++) {
			sb.append(i).append(") ").append(salvataggi.get(i)).append("\n");
		}
		int scelta = tv.scegliTraN(sb.toString(), 0, salvataggi.size() - 1);
		String nomeFile = salvataggi.get(scelta);

		String nomeNuovo;
		do {
			nomeNuovo = tv.inserisciStringa("Inserisci un nome nuovo per il salvataggio:");
			if (isNomeDisponibile(nomeNuovo)) {
				if (ManagerPersistenza.rinominaSalvataggio(nomeFile, nomeNuovo))
					break;
				else
					tv.stampaMessaggio("Errore");
			} else
				tv.stampaMessaggio("Nome salvataggio già presente. Riprova.");
		} while (true);
	}

	private int numeroSalvataggi() {
		return ManagerPersistenza.listaSalvataggi().size();
	}

	private boolean isNomeDisponibile(String nome) {
		if (trovaSalvataggio(nome) == null)
			return true;
		else
			return false;
	}

	public String getNomeDisponibile() {
		int prossimoNumero = numeroSalvataggi() + 1;
		while (true) {
			if (trovaSalvataggio("salvataggio" + prossimoNumero) == null)
				return "salvataggio" + prossimoNumero;
			prossimoNumero++;
		}
	}

	//versioni modificate per gui
	public void caricaPartita(ControlloreGioco cg, String salvataggio) {
		Partita partita = cg.getPartita();

		try {
			partita = ManagerPersistenza.caricaPartita(salvataggio);
			cg.setPartita(partita);
			// Riaggancio il navigatore ai giocatori
			partita.getNavigatore().setItems(partita.getGiocatori());
			// Riaggancio l'interfaccia partita ai giocatori
			ControlloreGioco.setPartitaIF(partita.getGiocatori(), partita);
			// Riaggancio mazzo alla sua pila
			partita.getMazzo().setPila(partita.getPilaScarti());
			salvataggioCorrente = salvataggio;
		} catch (IOException e) {
			e.getMessage();
		}

	}
	//fine
}
