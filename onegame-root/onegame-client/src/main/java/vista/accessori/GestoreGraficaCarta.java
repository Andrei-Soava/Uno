package vista.accessori;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import onegame.modello.carte.Carta;
import onegame.modello.carte.CartaNumero;
import onegame.modello.carte.CartaSpeciale;
import onegame.modello.carte.Colore;
import onegame.modello.carte.CartaSpeciale.TipoSpeciale;
import javafx.scene.control.Label;

public class GestoreGraficaCarta {
	private final ObjectProperty<Paint> fillProperty = new SimpleObjectProperty<>();
	private final SimpleStringProperty numeroText = new SimpleStringProperty();
	private final ObjectProperty<Image> tipoImage = new SimpleObjectProperty<>();

	// Mappa enum Colore → javafx.scene.paint.Color
	public static Paint mapColore(Colore c) {
		Color coloreCarta;
	    switch (c) {
	        case ROSSO: coloreCarta=Color.CRIMSON; break;
	        case GIALLO: coloreCarta=Color.GOLD; break;
	        case VERDE: coloreCarta=Color.SEAGREEN; break;
	        case BLU:  coloreCarta=Color.ROYALBLUE; break;
	        default: coloreCarta=Color.BLACK; break;
	    };
	    return coloreCarta;
	}
	
	private static Image mapTipo(TipoSpeciale t) {
		String name;
		if(t!=null)
			name = t.name().toLowerCase();
		else
			name="logo";
	    return new Image(GestoreGraficaCarta.class.getResourceAsStream("/immagini/"+ name +".png"));
	}
	
	// Sincronizza le proprietà intermedie con il modello
	private void syncFromModel(Carta carta) {
		ObjectProperty<Paint> fillProperty = new SimpleObjectProperty<>();
		SimpleStringProperty numeroText = new SimpleStringProperty();
		ObjectProperty<Image> tipoImage = new SimpleObjectProperty<>();
	    fillProperty.set(mapColore(carta.getColore()));

	    if (carta instanceof CartaNumero) {
	        numeroText.set(Integer.toString(((CartaNumero)carta).getNumero()));
	        tipoImage.set(null);
	    } else if (carta instanceof CartaSpeciale) {
	        tipoImage.set(mapTipo(((CartaSpeciale)carta).getTipo()));
	        numeroText.set("");
	    } else {
	        numeroText.set("");
	        tipoImage.set(null);
	    }
	}
	
	
	public static StackPane creaVistaCarta(Carta carta) {
		// Aggiorna le proprietà intermedie in base alla carta passata
		ObjectProperty<Paint> fillProperty = new SimpleObjectProperty<>();
		SimpleStringProperty numeroText = new SimpleStringProperty();
		ObjectProperty<Image> tipoImage = new SimpleObjectProperty<>();
	    fillProperty.set(mapColore(carta.getColore()));

	    if (carta instanceof CartaNumero) {
	        numeroText.set(Integer.toString(((CartaNumero)carta).getNumero()));
	        tipoImage.set(null);
	    } else if (carta instanceof CartaSpeciale) {
	        tipoImage.set(mapTipo(((CartaSpeciale)carta).getTipo()));
	        numeroText.set("");
	    } else {
	        numeroText.set("");
	        tipoImage.set(null);
	    }

	    // Sfondo rettangolare
	    Rectangle sfondo = new Rectangle(80, 120);
	    sfondo.setArcWidth(20);
	    sfondo.setArcHeight(20);
	    sfondo.setStroke(Color.WHITE);
	    sfondo.setStrokeWidth(3);
	    sfondo.fillProperty().bind(fillProperty);

	    // Contenuto
	    Label numeroLabel = new Label();
	    numeroLabel.textProperty().bind(numeroText);
	    numeroLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

	    ImageView tipoView = new ImageView();
	    tipoView.imageProperty().bind(tipoImage);
	    tipoView.setFitWidth(50);
	    tipoView.setFitHeight(50);

	    // StackPane con sfondo + (numero o immagine)
	    StackPane contenitore = new StackPane();
	    contenitore.getChildren().add(sfondo);

	    // Mostra numero se presente, altrimenti immagine
	    // (puoi anche gestire la visibilità con binding)
	    if (carta instanceof CartaNumero) {
	        contenitore.getChildren().add(numeroLabel);
	    } else if (carta instanceof CartaSpeciale) {
	        contenitore.getChildren().add(tipoView);
	    }
	    
	    return contenitore;	
	}

}
