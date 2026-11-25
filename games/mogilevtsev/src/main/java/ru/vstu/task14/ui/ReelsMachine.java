package ru.vstu.task14.ui;

import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

import java.io.IOException;

public final class ReelsMachine extends FxGameMachine {
    public ReelsMachine() {
        super("mogilevtsev.reels", "Три барабана — Могилевцев");
    }

    @Override
    protected void prepare(Stage stage) throws IOException {
        new SlotMachineApp().prepare(stage);
    }
}
