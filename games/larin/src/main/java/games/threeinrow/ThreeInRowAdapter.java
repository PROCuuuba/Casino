package games.threeinrow;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.casino.FxGameMachine;

public final class ThreeInRowAdapter extends FxGameMachine {

    public ThreeInRowAdapter() {
        super("larin.threerows", "Три в ряд — Ларин");
    }

    @Override
    protected void prepare(Stage stage) {
        ThreeInRowGame game = new ThreeInRowGame();
        stage.setScene(new Scene(game.createRoot(), 520, 560));
        stage.setResizable(false);
       stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> game.dispose());
    }
}