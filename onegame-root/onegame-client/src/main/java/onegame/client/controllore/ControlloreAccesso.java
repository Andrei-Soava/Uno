package onegame.client.controllore;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import onegame.client.net.ClientSocket;
import onegame.client.vista.VistaAccesso;

public class ControlloreAccesso {
	private VistaAccesso va;
	private ClientSocket cs;
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	
	public ControlloreAccesso(VistaAccesso va, ClientSocket cs) {
		this.va=va;
		this.cs=cs;
		verificaConnessione();
	}
	
	private void verificaConnessione() {
        scheduler.scheduleAtFixedRate(() -> {
            boolean connected = cs.isConnected();
            //aggiornamento bottoni funzionanti SOLO con connessione
            if(connected)
            	va.enableOnlineBtns();
            else
            	va.disableOnlineBtns();
            //aggiornamento label con stato connessione
            va.compilaStatoConnessione(connected);
        }, 0, 2, TimeUnit.SECONDS); // ogni 2 secondi
    }

    public void interrompiVerificaConnessione() {
        scheduler.shutdownNow();
    }
	
	public void eseguiAccesso() {
		va.ottieniDati((username,password)->{
			System.out.println(username);
			System.out.println(password);
			if(!cs.isConnected()) {
				va.mostraHome();
				return;
			}
			if(username==null && password==null) {
				cs.getUtente().setAnonimo(true);
				cs.getUtente().setUsername("anonimo");
				va.mostraHome();
				return;
			}
			if(username.length()==0 || password.length()==0) {
				va.compilaMessaggioErrore("Uno o più campi vuoti");
				eseguiAccesso();
				return;
			}
			
			//condizionale (sarà dentro una send asincrona al server e se la response == true, si va alla vista successiva)
			if(true) {
				cs.getUtente().setAnonimo(false);
				cs.getUtente().setUsername(username);
				va.mostraHome();	
			}
			else {
				va.compilaMessaggioErrore("Credenziali errate");
				va.svuotaCampi();
				eseguiAccesso();
			}
		});
	}
	
	

}
