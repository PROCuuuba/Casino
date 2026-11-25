package games.threeinrow;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

public final class ThreeInRowAdapter extends FxGameMachine {

    public ThreeInRowAdapter() {

        super("larin.threerows", "Три в ряд — Ларин");
    }

    @Override
    protected void prepare(Stage primaryStage) throws Exception {
        ThreeInRowGame game = new ThreeInRowGame();
        Parent root = game.createRoot();
        Scene scene = new Scene(root, 650, 500);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }
}