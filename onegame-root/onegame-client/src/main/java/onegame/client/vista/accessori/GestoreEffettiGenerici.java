package onegame.client.vista.accessori;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GestoreEffettiGenerici {

    public static void assegnaPulsazioneColorata(Node node, Color color) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(30);
        glow.setSpread(0.8);
        node.setEffect(glow);

        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(glow.radiusProperty(), 10),
                new KeyValue(glow.colorProperty(), color.deriveColor(1, 1, 1, 0.3))
            ),
            new KeyFrame(Duration.seconds(1),
                new KeyValue(glow.radiusProperty(), 40),
                new KeyValue(glow.colorProperty(), color.deriveColor(1, 1, 1, 1.0))
            )
        );

        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();
    }
    
    public static class EffettoGiocaCarta {
        private final Timeline glowPulse;
        private final Timeline scalePulse;

        public EffettoGiocaCarta(Node cardNode, Color color) {
            DropShadow glow = new DropShadow();
            glow.setColor(color);
            glow.setRadius(20);
            glow.setSpread(0.6);
            cardNode.setEffect(glow);

            // Glow pulsante
            glowPulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(glow.radiusProperty(), 15),
                    new KeyValue(glow.colorProperty(), color.deriveColor(1,1,1,0.5))
                ),
                new KeyFrame(Duration.seconds(0.8),
                    new KeyValue(glow.radiusProperty(), 35),
                    new KeyValue(glow.colorProperty(), color.deriveColor(1,1,1,1.0))
                )
            );
            glowPulse.setAutoReverse(true);
            glowPulse.setCycleCount(Animation.INDEFINITE);

            // Effetto "respiro" (scala)
            scalePulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(cardNode.scaleXProperty(), 1.0),
                    new KeyValue(cardNode.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(0.8),
                    new KeyValue(cardNode.scaleXProperty(), 1.05),
                    new KeyValue(cardNode.scaleYProperty(), 1.05)
                )
            );
            scalePulse.setAutoReverse(true);
            scalePulse.setCycleCount(Animation.INDEFINITE);
        }

        public void play() {
            glowPulse.play();
            scalePulse.play();
        }

        public void stop(Node cardNode) {
            glowPulse.stop();
            scalePulse.stop();
            cardNode.setEffect(null);
            cardNode.setScaleX(1.0);
            cardNode.setScaleY(1.0);
        }
    }

}
