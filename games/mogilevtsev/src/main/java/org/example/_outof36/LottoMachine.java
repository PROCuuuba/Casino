package org.example._outof36;

import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

import java.io.IOException;

public final class LottoMachine extends FxGameMachine {
    public LottoMachine() {
        super("barkalov.lotto536", "5 из 36");
    }

    @Override
    protected void prepare(Stage stage) throws IOException {
        LottoApplication.prepare(stage, false);
    }
}
