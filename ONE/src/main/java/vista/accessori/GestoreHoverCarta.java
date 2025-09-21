package vista.accessori;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GestoreHoverCarta {

    public static void bindOverlay(Node cardNode,
                                   Pane overlayLayer,
                                   ScrollPane scrollPane,
                                   double liftPx) {

        final class State {
            ImageView overlayView = null;
            AnimationTimer follow = null;
            ChangeListener<? super Object> relayoutListener = null;
        }
        State st = new State();

        Runnable show = () -> {
            if (st.overlayView != null) return;

            // Snapshot della carta (grafica identica, nessun reparent)
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage snap = cardNode.snapshot(params, null);

            ImageView iv = new ImageView(snap);
            iv.setMouseTransparent(true);
            iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 16, 0.2, 0, 6);");

            // Calcolo posizione nella scena -> overlay
            Runnable position = () -> {
                Bounds bScene = cardNode.localToScene(cardNode.getBoundsInLocal());
                Point2D inOverlay = overlayLayer.sceneToLocal(bScene.getMinX(), bScene.getMinY());
                iv.setLayoutX(inOverlay.getX());
                iv.setLayoutY(inOverlay.getY() - liftPx); // sollevamento
            };

            // Nascondi l’originale mentre l’overlay è visibile (niente doppio disegno)
            cardNode.setOpacity(0);

            overlayLayer.getChildren().add(iv);
            position.run();

            // Timer per seguire piccoli movimenti tra layout pass
            st.follow = new AnimationTimer() {
                @Override public void handle(long now) { position.run(); }
            };
            st.follow.start();

            // Listener per scroll/resize: forza riposizionamento
            ChangeListener<Object> r = (obs, o, n) -> position.run();
            st.relayoutListener = r;
            scrollPane.hvalueProperty().addListener(r);
            scrollPane.viewportBoundsProperty().addListener(r);
            cardNode.layoutBoundsProperty().addListener(r);
            cardNode.localToSceneTransformProperty().addListener(r);

            st.overlayView = iv;
        };

        Runnable hide = () -> {
            if (st.overlayView == null) return;

            if (st.follow != null) st.follow.stop();
            scrollPane.hvalueProperty().removeListener(st.relayoutListener);
            scrollPane.viewportBoundsProperty().removeListener(st.relayoutListener);
            cardNode.layoutBoundsProperty().removeListener(st.relayoutListener);
            cardNode.localToSceneTransformProperty().removeListener(st.relayoutListener);

            overlayLayer.getChildren().remove(st.overlayView);
            st.overlayView = null;
            st.follow = null;
            st.relayoutListener = null;

            cardNode.setOpacity(1);
        };

        // Mostra overlay alla sola entrata nell’area della carta
        cardNode.setOnMouseEntered(e -> show.run());
        // Rimuovi quando esci dalla carta (non dall’overlay, che è trasparente)
        cardNode.setOnMouseExited(e -> hide.run());
        // In caso di click/press prolungato puoi scegliere se tenerlo o nasconderlo
        cardNode.setOnMousePressed(e -> hide.run());
    }
}

