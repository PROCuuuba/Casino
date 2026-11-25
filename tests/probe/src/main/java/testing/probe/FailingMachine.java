package testing.probe;

import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

import java.io.IOException;

public final class FailingMachine extends FxGameMachine {
    public FailingMachine() {
        super("test.probe", "Испытательный автомат");
    }

    @Override
    protected void prepare(Stage stage) throws IOException {
        throw new IOException("Имитированный отказ запуска");
    }
}
