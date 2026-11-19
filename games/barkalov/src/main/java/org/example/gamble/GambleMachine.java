package org.example.gamble;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.casino.FxGameMachine;

import java.io.IOException;

public final class GambleMachine extends FxGameMachine {
    public GambleMachine() {
        super("barkalov.gamble", "Три мешочка — Баркалов");
    }

    @Override
    protected void prepare(Stage stage) throws IOException {
        GameSound.setEnabled(true);
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> GameSound.close());
        new GambleApplication().prepare(stage);
    }
}
