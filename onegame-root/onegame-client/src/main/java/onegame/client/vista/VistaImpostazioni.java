package onegame.client.vista;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.vista.accessori.GestoreCallbackBottoni;

public class VistaImpostazioni {

    private Scene scene;
    private AppWithMaven app;
    private Label titolo;
    private Button modificaNomeBtn;
    private Button modificaPasswordBtn;
    private Button eliminaUtenteBtn;
    private Button logoutBtn;

    public VistaImpostazioni(AppWithMaven app) {
    	this.app=app;
        BorderPane root = new BorderPane();
        
        Button homeBtn = new Button("← Home");
    	homeBtn.setOnAction(e -> app.mostraVistaHome());

    	titolo = new Label();
    	titolo.getStyleClass().add("titolo");

    	Region leftSpacer = new Region();
    	Region rightSpacer = new Region();
    	rightSpacer.setPrefWidth(0);
    	HBox.setHgrow(leftSpacer, Priority.ALWAYS);
    	HBox.setHgrow(rightSpacer, Priority.ALWAYS);
    	
    	logoutBtn = new Button("Logout");
    	logoutBtn.getStyleClass().add("logout");

    	HBox topBar = new HBox(10);
    	topBar.setPadding(new Insets(10));
    	topBar.setAlignment(Pos.CENTER);
    	topBar.getChildren().addAll(homeBtn, leftSpacer, titolo, rightSpacer, logoutBtn);

    	root.setTop(topBar);
        
    	//centro con 4 bottoni
        VBox centro = new VBox(15);
        centro.setAlignment(Pos.CENTER);
        centro.setPadding(new Insets(30));

        modificaNomeBtn = new Button("Modifica nome utente");
        modificaNomeBtn.setPrefWidth(200);
        modificaPasswordBtn = new Button("Modifica password");
        modificaPasswordBtn.setPrefWidth(200);
        eliminaUtenteBtn = new Button("Elimina account");
        eliminaUtenteBtn.setPrefWidth(200);

        Button infoBtn = new Button("Informazioni");
        infoBtn.setPrefWidth(200);
        
        infoBtn.setOnAction(e -> {
            // per ora non fa nulla
        });

        centro.getChildren().addAll(modificaNomeBtn, modificaPasswordBtn, eliminaUtenteBtn, infoBtn);
        root.setCenter(centro);
    	
        scene = new Scene(root);
        scene.getStylesheets().add(
        	    getClass().getResource("/stile/base.css").toExternalForm()
        	);
    }

    public Scene getScene() {
        return scene;
    }
    
    public void mostraAccesso() {
    	app.mostraVistaAccesso();
    }
    
    public void compilaNomeTitolo(String nome) {
    	titolo.setText("Impostazioni di "+nome);
    }
    
    public CompletableFuture<Void> waitForLogoutBtnClick() {
		return GestoreCallbackBottoni.waitForClick(logoutBtn);
    }
    
    public CompletableFuture<Void> waitForModificaNomeClick(){
    	return GestoreCallbackBottoni.waitForClick(modificaNomeBtn);
    }
    
    public CompletableFuture<Void> waitForModificaPasswordClick(){
    	return GestoreCallbackBottoni.waitForClick(modificaPasswordBtn);
    }
    
    public CompletableFuture<Void> waitForEliminaUtenteClick(){
    	return GestoreCallbackBottoni.waitForClick(eliminaUtenteBtn);
    }
    
    /**
     * classe ausiliaria per gestione callback modifica nome dentro controllore
     * (interfaccia funzionale sarebbe stata comunque valida)
     */
    public static class ContestoModificaNome {
        private final Dialog<?> dialog;
        private final String nuovoNome;
        private final Label erroreLbl;

        private ContestoModificaNome(Dialog<?> dialog, String nuovoNome, Label erroreLbl) {
            this.dialog = dialog;
            this.nuovoNome = nuovoNome;
            this.erroreLbl = erroreLbl;
        }

        public Dialog<?> getDialog() { return dialog; }
        public String getNuovoNome() { return nuovoNome; }
        public Label getErroreLbl() { return erroreLbl; }
    }

    /**
     * metodo asincrono che mostra una dialog di modifica del nome 
     * SENZA però gestire gli effetti della sua chiusura
     * 
     * @param onConferma, callback contenente elementi per gestione modifica nome
     * @param onClose, runnable utilizzato da controllore durante la chiusura della dialog 
     */
    public void mostraDialogModificaNome(Consumer<ContestoModificaNome> onConferma, Runnable onClose) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifica nome utente");

        Label lbl = new Label("Nuovo nome utente:");
        Label erroreLbl = new Label();
        erroreLbl.setStyle("-fx-text-fill: red;");

        TextField nuovoNomeField = new TextField();

        VBox content = new VBox(10, lbl, nuovoNomeField, erroreLbl);
        dialog.getDialogPane().setContent(content);

        ButtonType confermaBtn = new ButtonType("Conferma", ButtonData.OK_DONE);
        ButtonType annullaBtn = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaBtn, annullaBtn);

        // intercetto il click su Conferma e passo i dati al callback
        Button conferma = (Button) dialog.getDialogPane().lookupButton(confermaBtn);
        conferma.addEventFilter(ActionEvent.ACTION, ev -> {
            String nuovoNome = nuovoNomeField.getText().trim();
            // creo un piccolo "contesto" con i riferimenti utili
            ContestoModificaNome ctx = new ContestoModificaNome(dialog, nuovoNome, erroreLbl);
            // passo il contesto al callback (sarà il controllore a gestire cosa fare)
            onConferma.accept(ctx);
            ev.consume(); // blocco la chiusura automatica, sarà il controllore a decidere
        });
        
        // notifica al controllore quando la dialog si chiude
        dialog.setOnHidden(e -> {
            if (onClose != null) onClose.run();
        });
        dialog.show();
    }

    /**
     * classe ausiliaria per gestione callback modifica password dentro controllore
     * (interfaccia funzionale NON sarebbe stata un'opzione saggia,
     * vista la quantità di campi)
     */
    public static class ContestoModificaPassword {
        private final Dialog<?> dialog;
        private final PasswordField oldPasswordField;
        private final PasswordField newPasswordField;
        private final PasswordField confirmPasswordField;
        private final String oldPassword;
        private final String newPassword;
        private final String confirmPassword;
        private final Label erroreLbl;

        private ContestoModificaPassword(Dialog<?> dialog, 
        		PasswordField oldPasswordField, 
        		PasswordField newPasswordField, 
        		PasswordField confirmPasswordField, 
        		Label erroreLbl) {
            this.dialog = dialog;
            this.oldPasswordField = oldPasswordField;
            this.newPasswordField = newPasswordField;
            this.confirmPasswordField = confirmPasswordField;
            this.oldPassword = oldPasswordField.getText();
            this.newPassword = newPasswordField.getText();
            this.confirmPassword = confirmPasswordField.getText();
            this.erroreLbl = erroreLbl;
        }

        public Dialog<?> getDialog() { return dialog; }
        public PasswordField getOldPasswordField() { return oldPasswordField; }
        public PasswordField getNewPasswordField() { return newPasswordField; }
        public PasswordField getConfirmPasswordField() { return confirmPasswordField; }
        public String getOldPassword() { return oldPassword; }
        public String getNewPassword() { return newPassword; }
        public String getConfirmPassword() { return confirmPassword; }
        public Label getErroreLbl() { return erroreLbl; }
    }

    /**
     * metodo asincrono che mostra una dialog di modifica della password 
     * SENZA però gestire gli effetti della sua chiusura
     * 
     * @param onConferma, callback contenente elementi per gestione modifica password
     * @param onClose, runnable utilizzato da controllore durante la chiusura della dialog 
     */
    public void mostraDialogModificaPassword(Consumer<ContestoModificaPassword> onConferma, Runnable onClose) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifica password");
        dialog.getDialogPane().setMinWidth(250);

        PasswordField vecchia = new PasswordField();
        vecchia.setPromptText("Vecchia password");
        PasswordField nuova = new PasswordField();
        nuova.setPromptText("Nuova password");
        PasswordField conferma = new PasswordField();
        conferma.setPromptText("Conferma nuova password");

        Label errorePasswordLbl = new Label();
        errorePasswordLbl.setStyle("-fx-text-fill: red;");

        VBox content = new VBox(10, vecchia, nuova, conferma, errorePasswordLbl);
        dialog.getDialogPane().setContent(content);

        ButtonType confermaBtn = new ButtonType("Conferma", ButtonData.OK_DONE);
        ButtonType annullaBtn = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaBtn, annullaBtn);

        // intercetto il click su Conferma
        Button confermaButton = (Button) dialog.getDialogPane().lookupButton(confermaBtn);
        confermaButton.addEventFilter(ActionEvent.ACTION, ev -> {
            ContestoModificaPassword ctx = new ContestoModificaPassword(
                    dialog,
                    vecchia,
                    nuova,
                    conferma,
                    errorePasswordLbl
            );
            onConferma.accept(ctx);
            ev.consume(); // blocco la chiusura automatica, decide il controllore
        });

        //notifica al controllore quando la dialog si chiude
        dialog.setOnHidden(e -> {
            if (onClose != null) onClose.run();
        });
        dialog.show();
    }

//    public static class ContestoEliminaUtente {
//        private final Dialog<?> dialog;
//
//        private ContestoEliminaUtente(Dialog<?> dialog) {
//            this.dialog = dialog;
//        }
//
//        public Dialog<?> getDialog() { return dialog; }
//    }
    
    /**
     * interfaccia funzionale con la scopo di passare nella callback una dialog
     * (soluzione valida rispetto al fare una classe dedicata visto che serve 
     * passare solo un oggetto)
     * 
     * soluzione con classe sopra
     */
    @FunctionalInterface
    public interface ContestoEliminaUtente {
    	public abstract void onConferma(Dialog<?> dialog);
    }

    /**
     * metodo asincrono che mostra una alert per confermare eliminazione utente
     * SENZA però gestire gli effetti della sua chiusura
     * 
     * @param handler, interfaccia funzionale che fornisce nel callback l'alert mostrato
     * --> utile SOLO per impostare il testo della alert qualora l'eliminazione fallisse
     * @param onClose, runnable utilizzato da controllore durante la chiusura della alert
     */
    public void mostraDialogEliminaUtente(ContestoEliminaUtente handler, Runnable onClose) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimina account");
        alert.setHeaderText("Vuoi davvero eliminare il tuo account?");
        alert.setContentText("Questa azione è irreversibile.");

        ButtonType confermaBtn = new ButtonType("Conferma", ButtonData.OK_DONE);
        ButtonType annullaBtn = new ButtonType("Annulla", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confermaBtn, annullaBtn);

        // intercetto il click su Conferma
        Button confermaButton = (Button) alert.getDialogPane().lookupButton(confermaBtn);
        confermaButton.addEventFilter(ActionEvent.ACTION, ev -> {
            handler.onConferma(alert);
            ev.consume(); // blocco la chiusura automatica, decide il controllore
        });

        // notifica al controllore quando la dialog si chiude (X, ESC, Annulla, chiusura manuale)
        alert.setOnHidden(e -> {
            if (onClose != null) onClose.run();
        });

        alert.show();
    }

}
