package onegame.client.vista.accessori;

import java.util.concurrent.CompletableFuture;

import javafx.scene.control.Button;

public class GestoreCallbackBottoni {
	/**
     * Restituisce una CompletableFuture che si completa
     * quando il bottone viene premuto.
     */
    public static CompletableFuture<Void> waitForClick(Button button) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        button.setOnAction(e -> {
            if (!future.isDone()) {
                future.complete(null);
            }
        });
        return future;
    }
}
