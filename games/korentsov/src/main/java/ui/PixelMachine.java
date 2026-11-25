package ui;

import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

public final class PixelMachine extends FxGameMachine {
    public PixelMachine() {
        super("korentsov.pixel", "Пиксельный автомат — Коренцов");
    }

    @Override
    protected void prepare(Stage stage) {
        new SlotMachineApp().prepare(stage);
    }
}
