package onegame.client.controllore.online.stanza;

import onegame.client.net.ClientSocket;
import onegame.client.net.ConnectionMonitor;
import onegame.client.vista.online.VistaStanza;
import onegame.modello.net.StatoStanzaDTO;

public class ControlloreStanzaHost extends ControlloreStanza {

	public ControlloreStanzaHost(VistaStanza vs, ClientSocket cs, ConnectionMonitor cm) {
		super(vs, cs, cm);
	}

	@Override
	public void aspetta() {
		vs.mostraAvviaBtn();
		vs.disattivaAvviaBtn();
		aspettaAvviaPartita();
	}
	
	public void aspettaAvviaPartita() {
		vs.waitForAvviaBtnClick().thenRun(()->{
			cs.iniziaPartita(null);
		});
	}

	@Override
	public void aggiornaStanza(StatoStanzaDTO stato) {
		if(stato.nicknames.size()>1) 
			vs.attivaAvviaBtn();
		else
			vs.disattivaAvviaBtn();
		super.aggiornaStanza(stato);
	}
	
	

}
