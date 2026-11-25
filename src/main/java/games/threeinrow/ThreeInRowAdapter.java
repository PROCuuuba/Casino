package games.threeinrow;

import api.GameMachine;
import javafx.stage.Stage;

public class ThreeInRowAdapter implements GameMachine {

    @Override
    public String getTitle() {
        return "Три в ряд";
    }

    @Override
    public void start(Stage primaryStage, Runnable onReturnToCatalog) throws Exception {
        new ThreeInRowGame(primaryStage, onReturnToCatalog).start();
    }
}