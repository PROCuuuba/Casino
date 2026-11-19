package ru.study.chain;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public final class Main extends Application {
    private final Player player = new Player("Игрок", 0);
    private final Label info = new Label();
    private final Label status = new Label("Внесите монету и нажмите «Старт».");
    private final Button start = new Button("Старт");
    private final Button[] bags = new Button[3];
    private ActionChain action;
    private boolean freeNext;

    @Override public void start(Stage stage) {
        prepare(stage);
        stage.show();
    }

    void prepare(Stage stage) {
        Label title = new Label("Казик");
        title.setStyle("-fx-font-size: 25px; -fx-font-weight: bold; -fx-text-fill: #463826;");
        info.setStyle("-fx-font-size: 14px; -fx-text-fill: #504330;");

        Button addCoin = new Button("Внести монету");
        addCoin.setOnAction(e -> {
            player.addNumber(1);
            status.setText("Монета добавлена.");
            refresh();
        });
        start.setOnAction(e -> startGame());

        HBox bagRow = new HBox(12);
        bagRow.setAlignment(Pos.CENTER);
        Image pouchImage = new Image(Main.class.getResourceAsStream("images/pouch.png"));
        String[] cardColors = {"#fffaf2", "#f7f2e8", "#f1eee5"};
        for (int i = 0; i < bags.length; i++) {
            int number = i + 1;
            ImageView picture = new ImageView(pouchImage);
            picture.setFitWidth(82);
            picture.setFitHeight(82);
            picture.setPreserveRatio(true);

            Button bag = new Button("" + number, picture);
            bag.setTooltip(new Tooltip("Мешочек " + number));
            bag.setPrefSize(112, 116);
            bag.setStyle("-fx-background-color: " + cardColors[i]
                    + "; -fx-background-radius: 10; -fx-border-color: #ded2c0; -fx-border-radius: 10;");
            bag.setOnAction(e -> chooseBag(number));
            bags[i] = bag;
            bagRow.getChildren().add(bag);
        }

        HBox controls = new HBox(10, addCoin, start);
        controls.setAlignment(Pos.CENTER);
        status.setWrapText(true);
        status.setMaxWidth(430);
        status.setAlignment(Pos.CENTER);

        VBox root = new VBox(13, title, info, controls,
                new Label("Выберите мешочек"), bagRow, status);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5efe4;");

        stage.setTitle("Казик");
        stage.setScene(new Scene(root, 440, 360));
        stage.setMinWidth(440);
        stage.setMinHeight(360);
        refresh();
    }

    private void startGame() {
        if (!player.pay(1)) {
            status.setText("Недостаточно монет.");
            refresh();
            return;
        }
        action = new ActionChain(this);
        status.setText("Выберите один из мешочков.");
        refresh();
    }

    private void chooseBag(int number) {
        if (action == null) return;
        status.setText("Выбран мешочек " + number + ".");
        boolean again = action.process();

        if (!again) {
            action = null;
            status.setText("Игра завершена.");
        } else if (freeNext) {
            freeNext = false;
            status.setText("Бесплатная попытка.");
        } else if (player.pay(1)) {
            status.setText("Монета принята. Выберите мешочек ещё раз.");
        } else {
            action = null;
            status.setText("Монеты закончились.");
        }
        refresh();
    }

    boolean afterLoss() {
        return ask("Проигрыш", "Попробовать ещё раз за монету?", "Играть ещё", "Закончить");
    }

    boolean afterWin() {
        player.addWinnings(3);
        refresh();
        boolean again = ask("Выигрыш!", "Забрать выигрыш или продолжить?", "Продолжить", "Забрать");
        if (again) freeNext = true;
        else player.collectWinnings();
        refresh();
        return again;
    }

    boolean afterChance() {
        boolean again = ask("Шанс", "Следующая попытка бесплатная.", "Продолжить", "Закончить");
        if (again) freeNext = true;
        return again;
    }

    boolean afterEmptyDraw() {
        boolean again = ask("Пусто", "Повторить бесплатно?", "Повторить", "Закончить");
        if (again) freeNext = true;
        return again;
    }

    private boolean ask(String title, String message, String yesText, String noText) {
        ButtonType yes = new ButtonType(yesText, ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(noText, ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, yes, no);
        alert.setTitle(title);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yes;
    }

    private void refresh() {
        info.setText("Монеты: " + player.getNumber() + "   ·   Выигрыш: " + player.getWinnings()
                + "   ·   Игры: " + player.getCount());
        boolean playing = action != null;
        start.setDisable(playing || player.getNumber() == 0);
        for (Button bag : bags) bag.setDisable(!playing);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
