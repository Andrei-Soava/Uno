package onegame.client.controllore.offline;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.offline.VistaSalvataggi;

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
		
		eseguiSceltaWithDb();
	}
	
	public void eseguiSceltaWithDb() {
		cs.listaPartite(respListaPartite -> {
			if(respListaPartite.success) {
				vs.scegliAzioneSalvataggiAsync(respListaPartite.nomiSalvataggi, event->{
					switch (event.getTipo()) {
	                case GIOCA: { 
	                	cs.caricaPartita(event.getNomeOriginale(), respCaricaPartita ->{
	                		Platform.runLater(()->{
	                			if(respCaricaPartita.success) {
	                				vs.mostraGiocoCaricato(event.getNomeOriginale(), respCaricaPartita.partitaSerializzata);
	                			}
	                			else {
	                				eseguiSceltaWithDb();
	                			}
	                		});
	                	});
	                	break;
	                }
	                case RINOMINA: {
	                	//serve un cs.rinomina visto che cs.salva di adesso prende solo nome e partita serializzata 
	                	cs.rinominaPartita(event.getNomeOriginale(), event.getNuovoNome(), respRinominaPartita ->{
	                		Platform.runLater(()->{
	                			if(respRinominaPartita.success) {
	                				eseguiSceltaWithDb();
	                			}
	                			else {
	                				System.out.println("Errore durante la rinomina del salvataggio");
	                			}
	                		});
	                	});
	                    eseguiSceltaWithDb();
	                    break;
	                }
	                case ELIMINA: {
	                    cs.eliminaPartita(event.getNomeOriginale(), respEliminaPartita -> {
	                    	Platform.runLater(()->{
	                    		if (respEliminaPartita.success) {
	                    			eseguiSceltaWithDb();
	                    		}
	                    		else {
	                    			System.out.println("Errore durante l'eliminazione");
	                    		}
	                    		
	                    	});
	                    });
	                    break;
	                }
	            }
				});
			}
		});
	}
}
