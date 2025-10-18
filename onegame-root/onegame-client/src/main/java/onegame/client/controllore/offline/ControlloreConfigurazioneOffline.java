package onegame.client.controllore.offline;

import onegame.client.vista.offline.VistaConfigurazioneOffline;

public class ControlloreConfigurazioneOffline {
	private VistaConfigurazioneOffline vcoff;
	
	public ControlloreConfigurazioneOffline(VistaConfigurazioneOffline vcoff) {
		this.vcoff=vcoff;
		
		aspettaCreazionePartita();
	}
	
	public void aspettaCreazionePartita() {
		vcoff.configuraPartita(numeroGiocatori->{
			vcoff.mostraGiocoOffline(numeroGiocatori);
		});
	}
}
