package games.threebags;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.casino.FxGameMachine;

import java.io.IOException;

public final class ThreeBagsAdapter extends FxGameMachine {
    public ThreeBagsAdapter() {
        super("larin.threebags", "Три коробки — Ларин");
    }

    @Override
    protected void prepare(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        controller.setOnReturnToCatalog(primaryStage::close);
        Scene scene = new Scene(root, 650, 450);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }
}