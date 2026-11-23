package com.example.chain;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class GameApplication extends Application {
    private final Player player = new Player("Игрок", 0);
    private final ActionChain actionChain = new ActionChain();
    private final Label coinsLabel = new Label();
    private final Label infoLabel = new Label("Добавьте монету и начните игру.");
    private final Button startButton = new Button("Начать игру");
    private final Button[] chests = new Button[3];
    private final ImageView[] chestImages = new ImageView[3];
    private final Image closedChest = new Image(GameApplication.class.getResource("chest-closed.jfif").toExternalForm());
    private final Image openChest = new Image(GameApplication.class.getResource("chest-open.jpg").toExternalForm());
    private boolean freeNextGame;
    private boolean gameStarted;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Игровой автомат");

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
        addCoinButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f0b94f; -fx-font-weight: bold;");
        addCoinButton.setOnAction(event -> addCoin());
        startButton.setStyle("-fx-font-size: 14px; -fx-background-color: #80a95b; -fx-text-fill: white; -fx-font-weight: bold;");
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
        ClickSound.install(scene);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    private Button createChestButton(int number) {
        ImageView picture = new ImageView(closedChest);
        picture.setFitWidth(118);
        picture.setFitHeight(96);
        picture.setPreserveRatio(true);
        picture.setSmooth(false);
        chestImages[number - 1] = picture;

        StackPane imageBox = new StackPane(picture);
        imageBox.setPrefSize(126, 100);
        Label text = new Label("СУНДУК " + number + "\nОткрыть");
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f9df9f; -fx-text-alignment: center;");

        VBox content = new VBox(2, imageBox, text);
        content.setAlignment(Pos.CENTER);

        Button bag = new Button();
        bag.setGraphic(content);
        bag.setPrefSize(185, 170);
        bag.setMinSize(185, 170);
        bag.setStyle("-fx-background-color: #35211b; -fx-background-radius: 16; -fx-border-radius: 16; "
                + "-fx-border-color: #b9863d; -fx-border-width: 2; -fx-cursor: hand;");
        return bag;
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
        if (!gameStarted) {
            return;
        }

        if (!freeNextGame && !player.payForGame()) {
            finishGame("Монеты закончились. Добавьте монету для новой игры.");
            return;
        }

        freeNextGame = false;
        chestImages[index].setImage(openChest);
        GameDecision decision = actionChain.play(player);
        updatePlayerInfo();
        showDecision(decision);
    }

    private void showDecision(GameDecision decision) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(decision.getTitle());
        alert.setHeaderText(decision.getMessage());

        ButtonType continueButton = new ButtonType("Играть ещё", ButtonBar.ButtonData.YES);
        ButtonType finishButton = new ButtonType("Забрать и выйти", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(continueButton, finishButton);

        Optional<ButtonType> answer = alert.showAndWait();
        boolean continueGame = answer.isPresent() && answer.get() == continueButton;

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
        infoLabel.setText(freeNextGame
                ? "Следующий сундук можно открыть бесплатно."
                : "Выберите сундук для следующей попытки.");
        updateChestButtons();
    }

    private void finishGame(String message) {
        gameStarted = false;
        freeNextGame = false;
        infoLabel.setText(message);
        updateChestButtons();
    }

    private void updatePlayerInfo() {
        coinsLabel.setText("Монеты: " + player.getCoins() + " | Сыграно попыток: " + player.getGamesCount());
    }

    private void resetChests() {
        for (ImageView chestImage : chestImages) chestImage.setImage(closedChest);
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

    public static void main(String[] args) {
        launch(args);
    }
}
