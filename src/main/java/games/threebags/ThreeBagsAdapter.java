package games.threebags;

import api.GameMachine;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ThreeBagsAdapter implements GameMachine{
    @Override
    public String getTitle(){
        return "Три мешочка";
    }
    @Override
    public void start(Stage primaryStage, Runnable onReturnCatalog) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent root = loader.load();
        GameController controller = loader.getController();
        controller.setOnReturnToCatalog(onReturnCatalog);

        Scene scene = new Scene(root, 650, 450);
        primaryStage.setTitle(getTitle());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            onReturnCatalog.run();
        });
    }

}
