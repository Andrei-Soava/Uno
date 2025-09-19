package vista;


import esecuzione.AppWithMaven;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class VistaIniziale {

    private Scene scene;

    public VistaIniziale(AppWithMaven app) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Button btnCarica = new Button("Carica Partita");
        Button btnNuova = new Button("Nuova Partita");

        btnCarica.setOnAction(e -> app.mostraVistaSalvataggi());
        btnNuova.setOnAction(e -> app.mostraVistaConfigurazione());

        root.getChildren().addAll(btnCarica, btnNuova);
        
        
        scene = new Scene(root);
    }

    public Scene getScene() {
        return scene;
    }
}
