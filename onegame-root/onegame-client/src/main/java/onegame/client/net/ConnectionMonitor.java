package onegame.client.net;

import io.socket.client.Socket;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ConnectionMonitor {
    private final SimpleBooleanProperty connected = new SimpleBooleanProperty(false);

    public ConnectionMonitor(ClientSocket cs) {
    	cs.on(Socket.EVENT_CONNECT, args -> connected.set(cs.isConnected()));
    	cs.on(Socket.EVENT_DISCONNECT, args -> connected.set(cs.isConnected()));
    }

    public ReadOnlyBooleanProperty connectedProperty() {
        return connected;
    }
}

