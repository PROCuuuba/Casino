package com.example.chain;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.example.casino.ButtonSound;

public class GameApplication extends Application {
    private final Player player = new Player("Игрок", 0);
    private final ActionChain actionChain = new ActionChain();
    private final Label coinsLabel = new Label();
    private final Label infoLabel = new Label("Добавьте монету и начните игру.");
    private final Button startButton = new Button("Начать игру");
    private final Button[] chests = new Button[3];
    private final ChestShape[] chestShapes = new ChestShape[3];
    private Stage gameStage;
    private boolean freeNextGame;
    private boolean gameStarted;

    @Override
    public void start(Stage stage) {
        prepare(stage);
        stage.show();
    }

    public void prepare(Stage stage) {
        gameStage = stage;
        stage.setTitle("Сундуки удачи");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(18, 28, 24, 28));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #211215, #513324);");

        Label gameName = new Label("СУНДУКИ УДАЧИ");
        gameName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e5b85a;");

        Label title = new Label("Выбери один из сундуков");
        title.setStyle("-fx-font-size: 27px; -fx-font-weight: bold; -fx-text-fill: #fff0ce;");

        Label subtitle = new Label("Один из сундуков может принести монеты или бесплатную попытку.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #ddc4a2;");

        coinsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f4c950;");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #f2dfc3; -fx-padding: 8 14 8 14; "
                + "-fx-background-color: rgba(0,0,0,0.22); -fx-background-radius: 8;");

        Button addCoinButton = new Button("Добавить 1 монету");
        addCoinButton.setStyle("-fx-font-size: 14px; -fx-background-color: #e1aa3e; -fx-font-weight: bold;");
        addCoinButton.setOnAction(event -> addCoin());
        startButton.setStyle("-fx-font-size: 14px; -fx-background-color: #5e9f62; -fx-text-fill: white; -fx-font-weight: bold;");
        startButton.setOnAction(event -> startGame());

        HBox topButtons = new HBox(10, addCoinButton, startButton);
        topButtons.setAlignment(Pos.CENTER);

        VBox top = new VBox(8, gameName, title, subtitle, coinsLabel, infoLabel, topButtons);
        top.setAlignment(Pos.CENTER);
        root.setTop(top);

        HBox chestBox = new HBox(18);
        chestBox.setAlignment(Pos.CENTER);
        chestBox.setPadding(new Insets(28, 0, 4, 0));
        for (int i = 0; i < chests.length; i++) {
            int index = i;
            Button chest = createChestButton(i + 1);
            chest.setDisable(true);
            chest.setOnAction(event -> chooseChest(index));
            chests[i] = chest;
            chestBox.getChildren().add(chest);
        }
        root.setCenter(chestBox);

        updatePlayerInfo();
        Scene scene = new Scene(root, 690, 420);
        ButtonSound.install(scene);
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private Button createChestButton(int number) {
        ChestShape picture = new ChestShape();
        chestShapes[number - 1] = picture;

        StackPane imageBox = new StackPane(picture);
        imageBox.setPrefSize(126, 100);
        Label text = new Label("СУНДУК " + number + "\nОткрыть");
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f9df9f; -fx-text-alignment: center;");
        VBox content = new VBox(2, imageBox, text);
        content.setAlignment(Pos.CENTER);

        Button chest = new Button();
        chest.setGraphic(content);
        chest.setPrefSize(185, 170);
        chest.setMinSize(185, 170);
        chest.setStyle("-fx-background-color: #35211b; -fx-background-radius: 16; -fx-border-radius: 16; "
                + "-fx-border-color: #b9863d; -fx-border-width: 2; -fx-cursor: hand;");
        return chest;
    }

    private void addCoin() {
        player.addCoins(1);
        infoLabel.setText("Монета добавлена. Теперь можно начать игру.");
        updatePlayerInfo();
    }

    private void startGame() {
        if (player.getCoins() == 0) {
            showSimpleMessage("Недостаточно монет", "Сначала добавьте хотя бы одну монету.");
            return;
        }

        resetChests();
        gameStarted = true;
        infoLabel.setText("Выберите любой сундук.");
        updateChestButtons();
    }

    private void chooseChest(int index) {
        if (!gameStarted) return;
        if (!freeNextGame && !player.payForGame()) {
            finishGame("Монеты закончились. Добавьте монету для новой игры.");
            return;
        }

        freeNextGame = false;
        chestShapes[index].open();
        GameDecision decision = actionChain.play(player);
        updatePlayerInfo();
        showDecision(decision);
    }

    private void showDecision(GameDecision decision) {
        boolean continueGame = showDecisionDialog(decision);
        if (!continueGame) {
            finishGame("Игра завершена. Вы можете добавить монеты и начать заново.");
            return;
        }

        freeNextGame = decision.isFreeNextGame();
        if (!freeNextGame && player.getCoins() == 0) {
            finishGame("Монеты закончились. Добавьте монету для новой игры.");
            return;
        }

        resetChests();
        infoLabel.setText(freeNextGame ? "Следующий сундук можно открыть бесплатно." : "Выберите сундук для следующей попытки.");
        updateChestButtons();
    }

    private boolean showDecisionDialog(GameDecision decision) {
        Stage dialog = new Stage();
        dialog.initOwner(gameStage);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);

        String signText = decision.getType() == ResultType.WIN ? "★" : decision.getType() == ResultType.CHANCE ? "↻" : "✕";
        Label sign = new Label(signText);
        sign.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #f2c45a;");
        Label title = new Label(decision.getTitle());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffe9bd;");
        HBox heading = new HBox(10, sign, title);
        heading.setAlignment(Pos.CENTER);

        Label message = new Label(decision.getMessage());
        message.setWrapText(true);
        message.setMaxWidth(350);
        message.setAlignment(Pos.CENTER);
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #f5d9cf; -fx-text-alignment: center;");

        boolean[] result = {false};
        Button continueButton = new Button("Играть ещё");
        continueButton.setPrefWidth(150);
        continueButton.setStyle("-fx-font-weight: bold; -fx-background-color: #e3ad3d; -fx-text-fill: #371014; -fx-background-radius: 8;");
        continueButton.setOnAction(event -> {
            result[0] = true;
            dialog.close();
        });
        Button finishButton = new Button("Забрать и выйти");
        finishButton.setPrefWidth(150);
        finishButton.setStyle("-fx-font-weight: bold; -fx-background-color: #7d2530; -fx-text-fill: #fff0d2; -fx-border-color: #d69e42; -fx-border-radius: 8; -fx-background-radius: 8;");
        finishButton.setOnAction(event -> dialog.close());

        HBox buttons = new HBox(12, continueButton, finishButton);
        buttons.setAlignment(Pos.CENTER);
        VBox content = new VBox(18, heading, message, buttons);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(26, 32, 28, 32));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #751d29, #2b0d15); -fx-border-color: #e0ad4e; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;");

        Scene scene = new Scene(content, 410, 210);
        ButtonSound.install(scene);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.showAndWait();
        return result[0];
    }

    private void finishGame(String message) {
        gameStarted = false;
        freeNextGame = false;
        infoLabel.setText(message);
        updateChestButtons();
    }

    private void resetChests() {
        for (ChestShape chestShape : chestShapes) chestShape.close();
    }

    private void updatePlayerInfo() {
        coinsLabel.setText("Монеты: " + player.getCoins() + " | Сыграно попыток: " + player.getGamesCount());
    }

    private void updateChestButtons() {
        for (Button chest : chests) chest.setDisable(!gameStarted);
    }

    private void showSimpleMessage(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    private static final class ChestShape extends Pane {
        private final Group lid = new Group();

        private ChestShape() {
            setPrefSize(126, 100);

            Polygon back = polygon(23, 43, 38, 33, 113, 33, 101, 43);
            back.setFill(Color.web("#5c3a1c"));
            Polygon right = polygon(101, 43, 113, 33, 113, 77, 101, 88);
            right.setFill(Color.web("#7a4c20"));
            Rectangle front = rectangle(23, 43, 78, 45, "#a36a2c");
            Rectangle rim = rectangle(23, 43, 78, 7, "#e0a342");
            Rectangle lock = rectangle(57, 42, 12, 17, "#d8c079");
            lock.setStroke(Color.web("#402718"));
            lock.setStrokeWidth(2);
            Rectangle leftPixel = rectangle(31, 57, 12, 8, "#c58436");
            Rectangle rightPixel = rectangle(78, 70, 14, 8, "#7c4b20");

            Polygon lidTop = polygon(23, 31, 38, 20, 113, 20, 101, 31);
            lidTop.setFill(Color.web("#c48132"));
            Polygon lidFront = polygon(23, 31, 101, 31, 101, 48, 23, 48);
            lidFront.setFill(Color.web("#8b5522"));
            Polygon lidSide = polygon(101, 31, 113, 20, 113, 37, 101, 48);
            lidSide.setFill(Color.web("#5c381b"));
            Rectangle lidStripe = rectangle(31, 35, 62, 5, "#e0a342");

            lid.getChildren().addAll(lidTop, lidFront, lidSide, lidStripe);
            getChildren().addAll(back, right, front, rim, leftPixel, rightPixel, lock, lid);
        }

        private void open() {
            lid.setTranslateY(-18);
            lid.setRotate(-16);
        }

        private void close() {
            lid.setTranslateY(0);
            lid.setRotate(0);
        }

        private static Polygon polygon(double... points) {
            Polygon polygon = new Polygon(points);
            polygon.setStroke(Color.web("#2b1b14"));
            polygon.setStrokeWidth(2);
            return polygon;
        }

        private static Rectangle rectangle(double x, double y, double width, double height, String color) {
            Rectangle rectangle = new Rectangle(x, y, width, height);
            rectangle.setFill(Color.web(color));
            rectangle.setStroke(Color.web("#2b1b14"));
            rectangle.setStrokeWidth(1.5);
            return rectangle;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
