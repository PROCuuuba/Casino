package org.example.casino.hall;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 490);
        stage.setTitle("Игровой зал");
        stage.setMinWidth(380);
        stage.setMinHeight(390);
        stage.setScene(scene);
        stage.show();
    }
}
