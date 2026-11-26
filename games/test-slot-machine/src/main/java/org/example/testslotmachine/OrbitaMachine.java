package org.example.testslotmachine;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.casino.FxGameMachine;

public final class OrbitaMachine extends FxGameMachine {
    public OrbitaMachine() {
        super("barkalov.orbita", "Орбита");
    }

    @Override
    protected void prepare(Stage stage) {
        OrbitaView game = new OrbitaView();
        Scene scene = new Scene(game.createContent(), 484, 356);
        scene.getStylesheets().add(OrbitaMachine.class.getResource("orbita.css").toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> game.observeBalance());
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> game.stopObservingBalance());
    }
}
