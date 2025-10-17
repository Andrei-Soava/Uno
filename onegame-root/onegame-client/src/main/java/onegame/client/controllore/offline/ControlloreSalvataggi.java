package onegame.client.controllore.offline;

import java.util.List;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.persistenza_temporanea.ManagerPersistenza;
import onegame.client.vista.offline.VistaSalvataggi;
import onegame.modello.net.ProtocolloMessaggi.RespEliminaPartita;
import onegame.modello.net.ProtocolloMessaggi.RespListaPartite;
import onegame.modello.net.util.JsonHelper;

public class ControlloreSalvataggi {
	private VistaSalvataggi vs;
	private ClientSocket cs;
	
	public ControlloreSalvataggi(VistaSalvataggi vs, ClientSocket cs, ConnectionMonitor cm) {
		this.vs=vs;
		this.cs=cs;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vs.mostraMenuOffline();
	        }
	    });
	}
	
	public void eseguiScelta() {
		List<String> salvataggi = ManagerPersistenza.listaSalvataggi();
		vs.scegliAzioneSalvataggiAsync(salvataggi, event -> {
            switch (event.getTipo()) {
                case GIOCA: { 
                	vs.mostraGiocoCaricato(event.getNomeOriginale());
                	break;
                }
                case RINOMINA: {
                	if(ManagerPersistenza.verificaRinominaSalvataggio(event.getNomeOriginale(), event.getNuovoNome()))
                		ManagerPersistenza.rinominaSalvataggio(event.getNomeOriginale(), event.getNuovoNome());
                	else
                		System.out.println("Nome già presente");
                    eseguiScelta();
                    return;
                }
                case ELIMINA: {
                    ManagerPersistenza.eliminaSalvataggio(event.getNomeOriginale());
                    eseguiScelta();
                    return;
                }
            }
        });
	}
	
	public void eseguiSceltaWithDb() {
		cs.listaPartite(respListaPartite -> {
			if(respListaPartite.success) {
				vs.scegliAzioneSalvataggiAsync(respListaPartite.nomiSalvataggi, event->{
					switch (event.getTipo()) {
	                case GIOCA: { 
	                	//probabilmente partita da ottenere qui, deserializzarla e lo passo alla appwithmaven che la carica
	                	vs.mostraGiocoCaricato(event.getNomeOriginale());
	                	break;
	                }
	                case RINOMINA: {
	                	//serve un cs.rinomina visto che cs.salva di adesso prende solo nome e partita serializzata 
	                    eseguiSceltaWithDb();
	                    return;
	                }
	                case ELIMINA: {
	                    cs.eliminaPartita(event.getNomeOriginale(), respEliminaPartita -> {
	                    	if (respEliminaPartita.success) {
	                    		eseguiSceltaWithDb();
	                    		return;
	                    	}
	                    });
	                }
	            }
				});
			}
		});
	}
}
