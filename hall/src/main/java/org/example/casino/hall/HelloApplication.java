package org.example.casino.hall;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.casino.ButtonSound;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 940, 610);
        scene.getStylesheets().add(HelloApplication.class.getResource("hall.css").toExternalForm());
        ButtonSound.install(scene);
        stage.setTitle("Игровой зал");
        stage.setMinWidth(760);
        stage.setMinHeight(510);
        stage.setScene(scene);
        stage.show();
    }
}
