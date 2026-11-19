package ru.vstu.task14.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Загружает интерфейс из view.fxml и показывает окно автомата.
 */
public class SlotMachineApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        prepare(stage);
        stage.show();
    }

    void prepare(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Scene scene = new Scene(loader.load(), 520, 500);
        stage.setTitle("Task 14. Цепочка обязанностей — игровой автомат");
        stage.setScene(scene);
        stage.setResizable(false);
    }
}
