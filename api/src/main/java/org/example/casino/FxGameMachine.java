package org.example.casino;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
            ButtonSound.install(stage.getScene());
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
        Label arrow = new Label("←");
        arrow.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #301116;");
        back.setGraphic(arrow);
        back.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-color: #e0ad42; "
                + "-fx-text-fill: #301116; -fx-background-radius: 8; -fx-cursor: hand;");
        back.setOnAction(event -> stage.close());
        Label hint = new Label("Выберите другой автомат в игровом зале");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #f0d8c2;");
        BorderPane footer = new BorderPane();
        footer.setLeft(hint);
        footer.setRight(back);
        BorderPane.setAlignment(back, Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(8, 14, 8, 14));
        footer.setStyle("-fx-background-color: linear-gradient(to right, #35131a, #6d202b); -fx-border-color: #b27a32 transparent transparent transparent; -fx-border-width: 1;");
        BorderPane frame = new BorderPane();
        frame.setCenter(game);
        frame.setBottom(footer);
        Scene framedScene = new Scene(frame, scene.getWidth(), scene.getHeight() + footer.prefHeight(scene.getWidth()));
        framedScene.getStylesheets().setAll(scene.getStylesheets());
        stage.setScene(framedScene);
    }
}
