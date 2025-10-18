package onegame.client.vista.offline;

import java.util.List;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import onegame.client.esecuzione.AppWithMaven;
import onegame.client.persistenza_temporanea.ManagerPersistenza;
import onegame.client.controllore.offline.EventoSalvataggio;

public class VistaSalvataggi {

	private Scene scene;
	private AppWithMaven app;
	private VBox lista;

	public VistaSalvataggi(AppWithMaven app) {
		this.app = app;
		BorderPane root = new BorderPane();

		Button indietroBtn = new Button("Indietro");
		indietroBtn.setOnAction(e -> mostraMenuOffline());

		Label titolo = new Label("SALVATAGGI");
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

		lista = new VBox(10);
		lista.setAlignment(Pos.TOP_CENTER);
		lista.setMaxWidth(Region.USE_PREF_SIZE);


		//contenitore verticale per topBar e lista
		VBox contenitore = new VBox(20);
		contenitore.setAlignment(Pos.TOP_CENTER);
		contenitore.getChildren().addAll(topBar, lista);

		root.setCenter(contenitore);

		scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
	}

	public Scene getScene() {
		return scene;
	}

	public void mostraMenuOffline() {
		app.mostraVistaMenuOffline();
	}

	public void mostraGiocoCaricato(String salvataggio) {
		app.mostraVistaPartitaCaricata(salvataggio);
	}
	
	public void mostraGiocoCaricatoWithDb(String nomeSalvataggio, String partitaSerializzata) {
		app.mostraVistaPartitaCaricataWithDb(nomeSalvataggio, partitaSerializzata);
	}

	/**
	 * Costruisce la lista e notifica al callback l'azione scelta dall'utente.
	 */
	public void scegliAzioneSalvataggiAsync(List<String> salvataggi, Consumer<EventoSalvataggio> callback) {
		Platform.runLater(() -> {
			lista.getChildren().clear();

			for (String s : salvataggi) {
				Label nome = new Label(s);
				nome.setMinWidth(200);
				nome.setMaxWidth(200);
				nome.setEllipsisString("...");
				nome.setTextOverrun(OverrunStyle.ELLIPSIS);

				Button giocaBtn = new Button("Gioca");
				giocaBtn.setOnAction(
						e -> callback.accept(new EventoSalvataggio(EventoSalvataggio.Tipo.GIOCA, s, null)));

				Button rinominaBtn = new Button("Rinomina");
				rinominaBtn.setOnAction(e -> {
					TextInputDialog dialog = new TextInputDialog(s);
					dialog.setTitle("Rinomina salvataggio");
					dialog.setHeaderText("Rinomina in:");
					Button okBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
					okBtn.setDisable(true);
					dialog.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
						okBtn.setDisable(newValue.trim().isEmpty()
						/* || !ManagerPersistenza.verificaRinominaSalvataggio(s, newValue) */);
					});
					DialogPane dialogPane = dialog.getDialogPane();
		            dialogPane.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
					dialog.showAndWait().ifPresent(newName -> {
						callback.accept(new EventoSalvataggio(EventoSalvataggio.Tipo.RINOMINA, s, newName));
					});
				});

				Button eliminaBtn = new Button("Elimina");
				eliminaBtn.setOnAction(e -> {
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Elimina salvataggio");
					alert.setHeaderText("Sei sicuro di voler eliminare?");
					DialogPane dialogPane = alert.getDialogPane();
		            dialogPane.getStylesheets().add(getClass().getResource("/stile/base.css").toExternalForm());
					alert.showAndWait().ifPresent(response -> {
						if (response == ButtonType.OK) {
							callback.accept(new EventoSalvataggio(EventoSalvataggio.Tipo.ELIMINA, s, null));
						}
					});
				});

				HBox riga = new HBox(20, nome, giocaBtn, rinominaBtn, eliminaBtn);
				riga.setAlignment(Pos.CENTER_LEFT);

				lista.getChildren().add(riga);
			}
		});

	}

}
