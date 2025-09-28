package onegame.client.controllore;

import onegame.modello.net.Utente;

public class ControlloreHome {
	private Utente utente;
	
	public ControlloreHome(Utente utente) {
		this.utente=utente;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}
	
}
