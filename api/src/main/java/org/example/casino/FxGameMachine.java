package org.example.casino;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public abstract class FxGameMachine implements GameMachine {
    private final String id;
    private final String title;

    protected FxGameMachine(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public final String id() {
        return id;
    }

    @Override
    public final String title() {
        return title;
    }

    @Override
    public final void open() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        try {
            prepare(stage);
            addNavigation(stage);
            stage.setTitle(title);
            stage.showAndWait();
        } catch (Exception failure) {
            throw new IllegalStateException("Не удалось открыть «" + title + "»", failure);
        } finally {
            stage.close();
        }
    }

    protected abstract void prepare(Stage stage) throws Exception;

    private void addNavigation(Stage stage) {
        Scene scene = Objects.requireNonNull(stage.getScene(), "Автомат не создал сцену");
        Parent game = scene.getRoot();
        Button back = new Button("В каталог");
        back.setId("returnToCatalog");
        back.setOnAction(event -> stage.close());
        HBox footer = new HBox(back);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(5, 12, 5, 12));
        BorderPane frame = new BorderPane();
        scene.setRoot(frame);
        frame.setCenter(game);
        frame.setBottom(footer);
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> {
            double height = footer.prefHeight(-1);
            double originalHeight = stage.getHeight();
            if (stage.getMinHeight() > 0) {
                stage.setMinHeight(stage.getMinHeight() + height);
            }
            stage.setHeight(originalHeight + height);
        });
    }
}
