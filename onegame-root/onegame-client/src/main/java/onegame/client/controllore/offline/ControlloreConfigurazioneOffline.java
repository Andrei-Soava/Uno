package onegame.client.controllore.offline;

import java.util.UUID;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.vista.offline.VistaConfigurazioneOffline;

public class ControlloreConfigurazioneOffline {
	private VistaConfigurazioneOffline vcoff;
	private ClientSocket cs;
	
	public ControlloreConfigurazioneOffline(VistaConfigurazioneOffline vcoff, ClientSocket cs) {
		this.vcoff=vcoff;
		this.cs=cs;
		
		aspettaCreazionePartita();
	}
	
	/**
	 * metodo per gestione creazione partita nel caso di utente anonimo e registrato
	 */
	public void aspettaCreazionePartita() {
		vcoff.configuraPartita(numeroGiocatori->{
			//entro dentro il menu speciale SOLO se sono connesso E sono registrato
			if(cs.getUtente()!=null && (!cs.getUtente().isAnonimo())){
				//apro il menù speciale
				vcoff.mostraDialogNomeSalvataggio(contesto->{
					if(contesto.getSalvataggio()==null) {
						contesto.getDialog().close();
						vcoff.mostraGiocoOffline(numeroGiocatori, null);
						return;
					} 
					else //qui ha premuto conferma, quindi devo applicare politiche sul nomeSalvataggio
					{
						//nome non nullo--> verifico se è già presente o meno
						if(!contesto.getSalvataggio().isBlank()) {
							cs.listaPartite(risposta->{
								Platform.runLater(()->{
									if(risposta.success) {
										for(String partita : risposta.nomiSalvataggi) {
											if(partita.equals(contesto.getSalvataggio())) {
												contesto.getErroreLbl().setText("Nome non dispobibile");
												return;
											}
										}
										contesto.getDialog().close();
										vcoff.mostraGiocoOffline(numeroGiocatori, contesto.getSalvataggio());
									} else {
										contesto.getErroreLbl().setText("Errore nella verifica");
									}
								});
							});
						}
						else //nome nullo--> procedo alla creazione di un nome random
						{
							String randomName = (UUID.randomUUID()).toString();
							randomName = "salvataggio" + randomName.substring(0, 8);
							contesto.getDialog().close();
							vcoff.mostraGiocoOffline(numeroGiocatori, randomName);
						}
					}
					
					
				}, ()->{
					aspettaCreazionePartita();
				});
				
			} else {
				vcoff.mostraGiocoOffline(numeroGiocatori, null);
			}
		});
	}
	
}
