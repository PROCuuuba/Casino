package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        prepare(primaryStage);
        primaryStage.show();
    }

    void prepare(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/sample/sample.fxml"));
        primaryStage.setTitle("Игровой автомат - Task 14");
        primaryStage.setScene(new Scene(root, 700, 500));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
