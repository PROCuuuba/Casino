package sample;

import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

public final class LudkaMachine extends FxGameMachine {
    public LudkaMachine() {
        super("chernixa.ludka", "Ludka — Черных");
    }

    @Override
    protected void prepare(Stage stage) throws Exception {
        new Main().prepare(stage);
    }
}
