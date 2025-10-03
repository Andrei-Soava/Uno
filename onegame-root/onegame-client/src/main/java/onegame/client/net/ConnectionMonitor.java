package onegame.client.net;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

public class ConnectionMonitor {
    private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);
    private final Timeline timeline;

    public ConnectionMonitor(ClientSocket cs) {
        timeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> update(cs)),
            new KeyFrame(Duration.seconds(2), e -> update(cs))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void update(ClientSocket cs) {
        // qui leggi lo stato reale del socket
        connected.set(cs.isConnected());
    }

    public ReadOnlyBooleanProperty connectedProperty() {
        return connected;
    }

    public void stop() {
        timeline.stop();
    }
}
