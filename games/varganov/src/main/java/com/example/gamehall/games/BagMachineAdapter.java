package com.example.gamehall.games;

import com.example.chain.GameApplication;
import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

public final class BagMachineAdapter extends FxGameMachine {
    public BagMachineAdapter() {
        super("varganov.bags", "Сундуки — Варганов");
    }

    @Override
    protected void prepare(Stage stage) {
        new GameApplication().prepare(stage);
    }
}
