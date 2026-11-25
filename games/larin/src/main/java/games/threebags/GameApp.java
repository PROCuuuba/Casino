package games.threebags;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp {

    public static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }

    public static class JavaFXApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 650, 450);
            primaryStage.setTitle("Игровой автомат");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        }
    }
}