package org.example._outof36;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class LottoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        prepare(stage, true);
        stage.show();
    }

    static void prepare(Stage stage, boolean standalone) throws IOException {
        FXMLLoader loader = new FXMLLoader(LottoApplication.class.getResource("lotto-view.fxml"));
        stage.setScene(new Scene(loader.load(), 884, 660));
        stage.setTitle("5 из 36");
        stage.setMinWidth(850);
        stage.setMinHeight(680);
        loader.<LottoController>getController().attach(stage, standalone);
    }
}
