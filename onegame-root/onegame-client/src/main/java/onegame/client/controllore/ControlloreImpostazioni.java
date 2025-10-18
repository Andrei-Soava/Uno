package onegame.client.controllore;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.VistaImpostazioni;
import onegame.modello.net.Utente;

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
			
			if(contesto.getNuovoNome().equals("Marco")) {
				contesto.getErroreLbl().setText("Nome non disponibile");
				return;
			}
			
			if(contesto.getNuovoNome().equals("Fabio")) {
				contesto.getDialog().close();
			}
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
		    
		    //validazione password qui (se password vecchia è giusta, se password nuova rispetta criteri)
		    if(contesto.getOldPassword().equals("a") && contesto.getNewPassword().equals("b")) {
		    	contesto.getDialog().close();
		    }
		    
		}, ()->{
			aspettaSelezione();
		});
	}
	
	private void gestisciEliminaUtente() {
		vi.mostraDialogEliminaUtente((alert) -> {
			//sarà dentro una callback del client-socket
			if(true) { //ramo in cui è avvenuta eliminazione
				//possibili azioni sul clientsocket? in teoria se entro qui ho utente nullo, quindi mi serve un nuovo utente
				cs.setUtente(new Utente(true));
				vi.mostraAccesso();
				alert.close();				
			}
			else { //non è stato possibile eliminare
				alert.setContentText("Ciao");
				return;
			}
		}, ()->{
			aspettaSelezione();
		});
	}
	
	public void aspettaLogout() {
		vi.waitForLogoutBtnClick().thenRun(()->{
			if(cs.getUtente()!=null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
			}
			vi.mostraAccesso();
		});
	}
}
