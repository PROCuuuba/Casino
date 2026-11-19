package api;

import javafx.stage.Stage;

public interface GameMachine {
    String getTitle();
    void start(Stage primaryStage, Runnable onReturnToCatalog) throws Exception;
}
