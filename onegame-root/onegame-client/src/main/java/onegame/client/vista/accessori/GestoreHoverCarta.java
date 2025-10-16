package onegame.client.vista.accessori;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;
import onegame.client.vista.accessori.GestoreEffettiGenerici.EffettoGiocaCarta;

public class GestoreHoverCarta {

	@Deprecated //però buono come backup
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
    
    public static void bindOverlayNuovo(Pane mano, Pane overlayLayer, ScrollPane scrollPane, double liftPx, boolean spectator) {
        for (Node cardNode : mano.getChildren()) {
            cardNode.setOnMouseEntered(e -> {
            	overlayLayer.getChildren().clear();
                for (Node c : mano.getChildren()) {
                    c.setOpacity(1);
                    c.getProperties().remove("overlayView");
                    AnimationTimer f = (AnimationTimer) c.getProperties().remove("overlayFollow");
                    if (f != null) f.stop();
                    ChangeListener<Object> r = (ChangeListener<Object>) c.getProperties().remove("overlayRelayout");
                    if (r != null) {
                        scrollPane.hvalueProperty().removeListener(r);
                        scrollPane.viewportBoundsProperty().removeListener(r);
                        c.layoutBoundsProperty().removeListener(r);
                        c.localToSceneTransformProperty().removeListener(r);
                    }
                }
                Node hovered = (Node) e.getSource();

                // Snapshot della carta
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                WritableImage snap = hovered.snapshot(params, null);

                ImageView iv = new ImageView(snap);
                iv.setMouseTransparent(true);
                iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 16, 0.2, 0, 6);");

                // Posizionamento iniziale
                Bounds bScene = hovered.localToScene(hovered.getBoundsInLocal());
                Point2D inOverlay = overlayLayer.sceneToLocal(bScene.getMinX(), bScene.getMinY());
                iv.setLayoutX(inOverlay.getX());
                iv.setLayoutY(inOverlay.getY());

                // Nascondo l’originale per evitare doppioni
                hovered.setOpacity(0);
                overlayLayer.getChildren().add(iv);

                // Animazione di sollevamento
                TranslateTransition lift = new TranslateTransition(Duration.millis(200), iv);
                lift.setByY(-liftPx);
                lift.setInterpolator(Interpolator.EASE_BOTH);
                
                if(!spectator) {
                	lift.setOnFinished(ev -> {
                		EffettoGiocaCarta effect = new EffettoGiocaCarta(iv, Color.LIMEGREEN);
                		effect.play();
                		hovered.getProperties().put("playEffect", effect);
                	});
                }
                lift.play();

                // Timer per seguire la posizione della carta originale
                AnimationTimer follow = new AnimationTimer() {
                    @Override public void handle(long now) {
                        Bounds bs = hovered.localToScene(hovered.getBoundsInLocal());
                        Point2D p = overlayLayer.sceneToLocal(bs.getMinX(), bs.getMinY());
                        iv.setLayoutX(p.getX());
                        iv.setLayoutY(p.getY() - liftPx); // sempre sollevata
                    }
                };
                follow.start();

                // Listener per aggiornare posizione su scroll/resize
                ChangeListener<Object> relayout = (obs, o, n) -> {
                    Bounds bs = hovered.localToScene(hovered.getBoundsInLocal());
                    Point2D p = overlayLayer.sceneToLocal(bs.getMinX(), bs.getMinY());
                    iv.setLayoutX(p.getX());
                    iv.setLayoutY(p.getY() - liftPx);
                };
                scrollPane.hvalueProperty().addListener(relayout);
                scrollPane.viewportBoundsProperty().addListener(relayout);
                hovered.layoutBoundsProperty().addListener(relayout);
                hovered.localToSceneTransformProperty().addListener(relayout);

                // Salvo overlay e listener per cleanup
                hovered.getProperties().put("overlayView", iv);
                hovered.getProperties().put("overlayFollow", follow);
                hovered.getProperties().put("overlayRelayout", relayout);
            });

            cardNode.setOnMouseExited(e -> {
                Node hovered = (Node) e.getSource();
                ImageView iv = (ImageView) hovered.getProperties().remove("overlayView");
                if (iv == null) return;

                // Animazione di ritorno
                TranslateTransition lower = new TranslateTransition(Duration.millis(200), iv);
                lower.setToY(0);
                lower.setInterpolator(Interpolator.EASE_BOTH);

                lower.setOnFinished(ev -> {
                	if(!spectator) {
                		EffettoGiocaCarta effect = (EffettoGiocaCarta) hovered.getProperties().remove("playEffect");
                		if (effect != null) {
                			effect.stop(iv);
                		}
                	}
                    overlayLayer.getChildren().remove(iv);
                    hovered.setOpacity(1);

                    AnimationTimer follow = (AnimationTimer) hovered.getProperties().remove("overlayFollow");
                    if (follow != null) follow.stop();

                    @SuppressWarnings("unchecked")
                    ChangeListener<Object> relayout = (ChangeListener<Object>) hovered.getProperties().remove("overlayRelayout");
                    if (relayout != null) {
                        scrollPane.hvalueProperty().removeListener(relayout);
                        scrollPane.viewportBoundsProperty().removeListener(relayout);
                        hovered.layoutBoundsProperty().removeListener(relayout);
                        hovered.localToSceneTransformProperty().removeListener(relayout);
                    }
                });

                lower.play();
            });
            
        }
    }
    

}

