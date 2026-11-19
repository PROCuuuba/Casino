package ru.study.chain;

import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

public final class PouchMachine extends FxGameMachine {
    public PouchMachine() {
        super("putilin.pouches", "Казик — Путилин");
    }

    @Override
    protected void prepare(Stage stage) {
        new Main().prepare(stage);
    }
}
