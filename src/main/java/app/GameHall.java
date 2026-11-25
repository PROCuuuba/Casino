package app;

import api.GameMachine;
import games.threebags.ThreeBagsAdapter;
import games.threeinrow.ThreeInRowAdapter;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameHall {
    private GameCatalog catalog = new GameCatalog();
    private Stage primaryStage;

    public static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }

    public static class JavaFXApp extends Application {
        @Override
        public void start(Stage stage) {
            GameHall hall = new GameHall();
            hall.primaryStage = stage;

            // Регистрация автоматов
            hall.catalog.register(new ThreeBagsAdapter());
            hall.catalog.register(new ThreeInRowAdapter());
            hall.showCatalog();
        }
    }

    public void showCatalog() {
        primaryStage.setOnCloseRequest(null); // Сбрасываем обработчик
        primaryStage.setTitle("Игровой Зал");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20px; -fx-background-color: #ecf0f1;");

        Label title = new Label("Добро пожаловать в Игровой Зал!");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ListView<GameMachine> gameList = new ListView<>();
        gameList.getItems().addAll(catalog.getMachines());

        gameList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GameMachine item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        Button playBtn = new Button("▶ Играть");
        playBtn.setStyle("-fx-font-size: 16px; -fx-background-color: #2ecc71; -fx-text-fill: white;");

        playBtn.setOnAction(e -> {
            GameMachine selected = gameList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                launchGame(selected);
            } else {
                showError("Выберите игру из списка!");
            }
        });

        root.getChildren().addAll(title, gameList, playBtn);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void launchGame(GameMachine machine) {
        try {
            machine.start(primaryStage, this::showCatalog);
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка запуска");
            alert.setHeaderText("Не удалось запустить автомат: " + machine.getTitle());
            alert.setContentText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
            alert.showAndWait();

            showCatalog();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }
}