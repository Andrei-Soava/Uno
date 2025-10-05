package onegame.client.vista.partita;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import onegame.client.esecuzione.AppWithMaven;
import onegame.modello.carte.Carta;

public class VistaSpettatore extends VistaPartita {
    private Label numeroCarteLbl;
	
	public VistaSpettatore(AppWithMaven app) {
		super(app);
		//pescaBtn non serve nella VistaSpettatore
    	pescaBtn.setVisible(false);
    	//contenitore inferiore
    	HBox contenitoreInferiore = new HBox(10);
    	contenitoreInferiore.setAlignment(Pos.CENTER);
    	numeroCarteLbl=new Label();
    	numeroCarteLbl.setTextAlignment(TextAlignment.CENTER);
    	numeroCarteLbl.setAlignment(Pos.CENTER);
    	numeroCarteLbl.getStyleClass().add("titolo");
    	contenitoreInferiore.getChildren().add(numeroCarteLbl);
    	//contenitoreInferiore.setMinHeight(200);
        //imposto il contenitore inferiore sotto
        root.setBottom(contenitoreInferiore);
        
        scene = new Scene(root, app.getPrimaryStage().getWidth(), app.getPrimaryStage().getHeight());
    	scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
	}
	
	public void impostaTurnoSpettatore(String giocatore, int numeroCarte, Carta cartaCorrente) {
		this.stampaTurnoCorrente(giocatore);
		this.impostaCartaCorrente(cartaCorrente);
		numeroCarteLbl.setText("Numero carte in mano: "+numeroCarte);
	}


	//TODO stampaMessaggi e stampaFinePartita 
}
