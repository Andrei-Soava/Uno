package onegame.client.controllore;

import javafx.application.Platform;
import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.VistaImpostazioni;

public class ControlloreImpostazioni {

	private VistaImpostazioni vi;
	private ClientSocket cs;
	
	public ControlloreImpostazioni(VistaImpostazioni vi, ClientSocket cs, ConnectionMonitor cm) {
		this.vi=vi;
		this.cs=cs;
		
		cm.connectedProperty().addListener((obs, oldVal, newVal) -> {
	        if (Boolean.FALSE.equals(newVal)) {
	            // siamo passati a disconnesso
	            vi.mostraAccesso();
	        }
	    });
		
		aspettaLogout();
		aspettaSelezione();
	}

	public void aspettaSelezione() {
		vi.compilaNomeTitolo(cs.getUtente().getUsername());
		vi.waitForModificaNomeClick().thenRun(()->{gestisciModificaNome();});
		vi.waitForModificaPasswordClick().thenRun(()->{gestisciModificaPassword();});
		vi.waitForEliminaUtenteClick().thenRun(()->{gestisciEliminaUtente();});
	}
	
	private void gestisciModificaNome() {
		vi.mostraDialogModificaNome(contesto->{
			
			if(contesto.getNuovoNome()==null || contesto.getNuovoNome().length()==0) {
				contesto.getErroreLbl().setText("Campo vuoto");
				return;
			}
			
			cs.cambioUsername(contesto.getNuovoNome(), risposta->{
				Platform.runLater(()->{
					if(risposta.success) {
						cs.getUtente().setUsername(contesto.getNuovoNome());
						contesto.getDialog().close();
					} else {
						contesto.getErroreLbl().setText(risposta.messaggio);
					}	
				});
			});
		}, () -> {
	        // questo viene chiamato SEMPRE quando la dialog si chiude
	        aspettaSelezione();
	    });
	}
	
	private void gestisciModificaPassword() {
		vi.mostraDialogModificaPassword(contesto->{
			
			if(contesto.getOldPassword() == null || contesto.getOldPassword().isEmpty()) {
				contesto.getErroreLbl().setText("La vecchia password non può essere vuota");
		        return;
			}
			if (contesto.getNewPassword() == null || contesto.getNewPassword().isEmpty()) {
		        contesto.getErroreLbl().setText("La nuova password non può essere vuota");
		        return;
		    }
			
		    if (!contesto.getNewPassword().equals(contesto.getConfirmPassword())) {
		        contesto.getErroreLbl().setText("Le password non coincidono");
		        contesto.getNewPasswordField().clear();
		        contesto.getConfirmPasswordField().clear();
		        return;
		    }
		    
		    cs.cambioPassword(contesto.getOldPassword(), contesto.getNewPassword(), risposta->{
		    	Platform.runLater(()->{
		    		if(risposta.success) {
		    			contesto.getDialog().close();
		    		} else {
		    			contesto.getErroreLbl().setText(risposta.messaggio);
		    		}
		    	});
		    });
		    
		}, ()->{
			aspettaSelezione();
		});
	}
	
	private void gestisciEliminaUtente() {
		vi.mostraDialogEliminaUtente((alert) -> {
			//sarà dentro una callback del client-socket
			cs.eliminaAccount(alert.getPassword(), risposta->{
				Platform.runLater(()->{
					if(risposta.success) {
						alert.getDialog().close();
						cs.setUtente(null);
						vi.mostraAccesso();
					} else {
						alert.getErroreLbl().setText(risposta.messaggio);
					}
				});
			});
		}, ()->{
			aspettaSelezione();
		});
	}
	
	public void aspettaLogout() {
		vi.waitForLogoutBtnClick().thenRun(()->{
			cs.setUtente(null);
			vi.mostraAccesso();
		});
	}
}
