package onegame.client.vista.offline;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.Vista;
import onegame.client.vista.accessori.GestoreCallbackBottoni;

public class VistaConfigurazioneOffline extends Vista {

    private ComboBox<Integer> numGiocatori;
    private Button avviaBtn;

    public VistaConfigurazioneOffline(AppWithMaven app) {
    	super(app);
    	BorderPane root = new BorderPane();

    	Button indietroBtn = new Button("Indietro");
    	indietroBtn.setOnAction(e -> app.mostraVistaMenuOffline());

    	Label titolo = new Label("CONFIGURA NUOVA PARTITA");
    	titolo.getStyleClass().add("titolo");

    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(50);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);

    	HBox topBar = new HBox(10);
    	topBar.setPadding(new Insets(10));
    	topBar.setAlignment(Pos.CENTER_LEFT);
    	topBar.getChildren().addAll(indietroBtn, leftSpacer, titolo, rightSpacer);

    	root.setTop(topBar);

    	Label lblNumGiocatori = new Label("Numero Giocatori");
    	numGiocatori = new ComboBox<>();
    	numGiocatori.getItems().addAll(2, 3, 4, 5, 6, 7, 8, 9, 10);
    	numGiocatori.setValue(2);

    	//possibile scelta modalità alternative QUI
    	
    	Button annullaBtn = new Button("Annulla");
    	annullaBtn.setOnAction(e -> app.mostraVistaMenuOffline());

    	avviaBtn = new Button("Avvia Partita");

    	HBox pulsanti = new HBox(10, annullaBtn, avviaBtn);
    	pulsanti.setAlignment(Pos.CENTER);

    	VBox centro = new VBox(20, lblNumGiocatori, numGiocatori, pulsanti);
    	centro.setAlignment(Pos.CENTER);

    	root.setCenter(centro);

        scene = new Scene(root);
        
        scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    }
    
    public CompletableFuture<Void> waitForAvviaBtnClick(){
    	return GestoreCallbackBottoni.waitForClick(avviaBtn);
    }
    

//	@FunctionalInterface
//	public interface NumeroGiocatoriCallback {
//		void mandaNumero(int numero);
//	}
//
//	public void ottieniNumero(NumeroGiocatoriCallback callback) {
//		avviaBtn.setOnAction(e -> {
//			int numero = numGiocatori.getValue();
//			callback.mandaNumero(numero);
//		});
//	}
    
    public void mostraGiocoOffline(int numero, String salvataggio) {
    	app.mostraVistaPartitaNuova(numero, salvataggio);
    }
    
    public void configuraPartita(Consumer<Integer> callback) {
    	Platform.runLater(()->{
    		avviaBtn.setOnAction(e->{
    			callback.accept(numGiocatori.getValue());
    		});
    	});
    }
    
    
    
    /**
     * classe ausiliaria per gestione delle scelte dell'utente registrato
     */
    public static class ContestoNomeSalvataggio {
        private final Dialog<?> dialog;
        private final String salvataggio;
        private final Label erroreLbl;

        private ContestoNomeSalvataggio(Dialog<?> dialog, String salvataggio, Label erroreLbl) {
            this.dialog = dialog;
            this.salvataggio = salvataggio;
            this.erroreLbl = erroreLbl;
        }

        public Dialog<?> getDialog() { return dialog; }
        public String getSalvataggio() { return salvataggio; }
        public Label getErroreLbl() { return erroreLbl; }
    }

    /**
     * metodo che mostra una dialog per gli utenti registrati dove che nome dare ad un nuovo salvataggio
     * (qualora lo volessero dare)
     * 
     * @param onConferma
     * @param onClose
     */
    public void mostraDialogNomeSalvataggio(Consumer<ContestoNomeSalvataggio> onConferma, Runnable onClose) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Nome salvataggio");

        Label lbl = new Label("Scegli nome salvataggio");
        lbl.getStyleClass().add("titolo");
        Label erroreLbl = new Label();
        erroreLbl.setStyle("-fx-text-fill: red;");

        TextField salvataggioField = new TextField();
        salvataggioField.setPromptText("Inserisci un nome valido (oppure lascia vuoto per generarne uno)");

        VBox content = new VBox(10, lbl, salvataggioField, erroreLbl);
        dialog.getDialogPane().setContent(content);

        ButtonType confermaBtn = new ButtonType("Conferma", ButtonData.OK_DONE);
        ButtonType nonSalvareBtn = new ButtonType("Continua senza salvare", ButtonData.OK_DONE);
        ButtonType annullaBtn = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaBtn,nonSalvareBtn, annullaBtn);

        Button conferma = (Button) dialog.getDialogPane().lookupButton(confermaBtn);
        conferma.addEventFilter(ActionEvent.ACTION, ev -> {
            String salvataggio = salvataggioField.getText().trim();
            ContestoNomeSalvataggio ctx = new ContestoNomeSalvataggio(dialog, salvataggio, erroreLbl);
            onConferma.accept(ctx);
            ev.consume();
        });
        
        Button continua = (Button) dialog.getDialogPane().lookupButton(nonSalvareBtn);
        continua.addEventFilter(ActionEvent.ACTION, ev -> {
            ContestoNomeSalvataggio ctx = new ContestoNomeSalvataggio(dialog, null, erroreLbl);
            onConferma.accept(ctx);
            ev.consume();
        });
        
        dialog.setOnHidden(e -> {
            if (onClose != null) onClose.run();
        });
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
        dialog.show();
    }
    
}
