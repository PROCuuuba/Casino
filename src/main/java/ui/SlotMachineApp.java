package ui;

import chain.ActionChain;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Player;

import java.io.InputStream;
import java.util.Objects;

// Главное окно игрового автомата с пиксельным корпусом и выводом результата на центральный экран
public class SlotMachineApp extends Application {
    private static final double MACHINE_WIDTH = 418;
    private static final double MACHINE_HEIGHT = 560;

    private Player player1;
    private ActionChain action = null;

    private ImageView view;
    private Image coinSplashImage;
    private Image slotMachineImage;

    private Label lblBalance;
    private Label lblGamesCount;
    private Label lblHint;

    private Label lblScreenReels;
    private Label lblScreenResult;
    private Label lblScreenStrategy;
    private Label lblScreenDetails;

    // Создает интерфейс автомата, загружает PNG-ресурсы и настраивает обработчики
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Игровой автомат — Цепочка обязанностей");

        player1 = new Player("Николай", 1);

        coinSplashImage = loadPngImage("/splash.png");
        slotMachineImage = loadPngImage("/slot_machine.png");

        view = new ImageView(coinSplashImage);
        view.setFitWidth(MACHINE_WIDTH);
        view.setFitHeight(MACHINE_HEIGHT);
        view.setPreserveRatio(true);
        view.setSmooth(false);
        view.setCursor(Cursor.HAND);

        // Обработчик щелчка по изображению игрового автомата
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (action == null) return;

            if (action.process()) {
                updateStatsUI();
                if (initRound()) {
                    lblHint.setText("Монетка принята! Кликните по автомату для нового раунда.");
                }
            } else {
                action = null;
                view.setImage(coinSplashImage);
                lblHint.setText("Игра окончена. Нажмите «Начать игру» для нового раунда.");
                updateMachineScreen(
                        "[ $ ]  [ $ ]  [ $ ]",
                        "ИГРА ОКОНЧЕНА",
                        "Итоговый баланс: " + player1.getNumber(),
                        "Нажмите «Начать игру»"
                );
            }
            updateStatsUI();
        });

        VBox screenOverlay = createMachineScreenOverlay();

        Pane machinePane = new Pane(view, screenOverlay);
        machinePane.setPrefSize(MACHINE_WIDTH, MACHINE_HEIGHT);
        machinePane.setMinSize(MACHINE_WIDTH, MACHINE_HEIGHT);
        machinePane.setMaxSize(MACHINE_WIDTH, MACHINE_HEIGHT);

        lblBalance = new Label();
        lblBalance.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblBalance.setTextFill(Color.web("#FFD700"));

        lblGamesCount = new Label();
        lblGamesCount.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        lblGamesCount.setTextFill(Color.web("#E0F2F1"));

        HBox statsBox = new HBox(30, lblBalance, lblGamesCount);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10, 14, 10, 14));
        statsBox.setStyle("-fx-background-color: #1E2225; -fx-border-color: #D4AF37; -fx-border-width: 0 0 2 0;");

        lblHint = new Label("Нажмите «Начать игру», чтобы испытать удачу!");
        lblHint.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
        lblHint.setTextFill(Color.web("#F0F4F8"));

        Button btnPay = new Button("Кинуть монетку (+1)");
        btnPay.setStyle("-fx-font-size: 14px; -fx-background-color: #FFD700; -fx-text-fill: #1A1A1A; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 16 8 16;");
        btnPay.setCursor(Cursor.HAND);
        btnPay.setOnAction(this::onPay);

        Button btnStart = new Button("Начать игру");
        btnStart.setStyle("-fx-font-size: 14px; -fx-background-color: #43A047; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 18 8 18;");
        btnStart.setCursor(Cursor.HAND);
        btnStart.setOnAction(this::onStart);

        HBox controlsBox = new HBox(20, btnPay, btnStart);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10, 14, 14, 14));

        VBox centerBox = new VBox(8, lblHint, machinePane);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10, 12, 4, 12));

        BorderPane root = new BorderPane();
        root.setTop(statsBox);
        root.setCenter(centerBox);
        root.setBottom(controlsBox);
        root.setStyle("-fx-background-color: #282C2F;");

        updateStatsUI();
        updateMachineScreen(
                "[ $ ]  [ $ ]  [ $ ]",
                "ВСТАВЬТЕ МОНЕТКУ",
                "Автомат готов к игре",
                "Нажмите «Начать игру»"
        );

        Scene scene = new Scene(root, 520, 710);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // Загружает PNG-изображение из ресурсов приложения
    private Image loadPngImage(String resourcePath) {
        InputStream stream = Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Не найден ресурс изображения: " + resourcePath
        );
        return new Image(stream);
    }

    // Создает стилизованный ретро-экран, расположенный точно поверх экрана на изображении автомата
    private VBox createMachineScreenOverlay() {
        lblScreenReels = new Label();
        lblScreenReels.setFont(Font.font("Consolas", FontWeight.BOLD, 17));
        lblScreenReels.setTextAlignment(TextAlignment.CENTER);
        lblScreenReels.setWrapText(true);

        lblScreenResult = new Label();
        lblScreenResult.setFont(Font.font("System", FontWeight.BOLD, 13));
        lblScreenResult.setTextAlignment(TextAlignment.CENTER);
        lblScreenResult.setWrapText(true);

        lblScreenStrategy = new Label();
        lblScreenStrategy.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        lblScreenStrategy.setTextFill(Color.web("#80CBC4"));
        lblScreenStrategy.setTextAlignment(TextAlignment.CENTER);
        lblScreenStrategy.setWrapText(true);

        lblScreenDetails = new Label();
        lblScreenDetails.setFont(Font.font("System", 11));
        lblScreenDetails.setTextFill(Color.web("#CFD8DC"));
        lblScreenDetails.setTextAlignment(TextAlignment.CENTER);
        lblScreenDetails.setWrapText(true);

        VBox overlay = new VBox(5, lblScreenReels, lblScreenResult, lblScreenStrategy, lblScreenDetails);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(8));
        overlay.setLayoutX(102);
        overlay.setLayoutY(193);
        overlay.setPrefSize(202, 137);
        overlay.setMaxSize(202, 137);
        overlay.setMouseTransparent(true);
        overlay.setStyle("-fx-background-color: rgba(24, 38, 37, 0.90); -fx-background-radius: 6; -fx-border-color: #5D7A78; -fx-border-width: 1.5; -fx-border-radius: 6;");
        return overlay;
    }

    // Отображает результат розыгрыша и активную стратегию на центральном экране автомата
    private void updateMachineScreen(String reels, String resultTitle, String strategyName, String details) {
        lblScreenReels.setText(reels);
        lblScreenResult.setText(resultTitle);
        lblScreenStrategy.setText(strategyName);
        lblScreenDetails.setText(details);

        if (resultTitle.contains("ВЫИГРЫШ")) {
            lblScreenReels.setTextFill(Color.web("#FFD700"));
            lblScreenResult.setTextFill(Color.web("#7CFF6B"));
        } else if (resultTitle.contains("ШАНС")) {
            lblScreenReels.setTextFill(Color.web("#4DD0E1"));
            lblScreenResult.setTextFill(Color.web("#FFD166"));
        } else if (resultTitle.contains("ПРОИГРЫШ") || resultTitle.contains("НЕТ МОНЕТОК")) {
            lblScreenReels.setTextFill(Color.web("#FF6B6B"));
            lblScreenResult.setTextFill(Color.web("#FF8A80"));
        } else {
            lblScreenReels.setTextFill(Color.web("#FFD700"));
            lblScreenResult.setTextFill(Color.web("#E0F2F1"));
        }

        updateStatsUI();
    }

    // Пополняет счет игрока на одну монетку
    public void onPay(ActionEvent actionEvent) {
        player1.addNumber(1);
        updateStatsUI();
        lblHint.setText("Монетка добавлена! Нажмите «Начать игру».");
        if (action == null) {
            updateMachineScreen(
                    "[ $ ]  [ $ ]  [ $ ]",
                    "МОНЕТА ПРИНЯТА",
                    "Баланс: " + player1.getNumber() + " монет",
                    "Нажмите «Начать игру»"
            );
        }
    }

    // Запускает игру после проверки наличия монетки и загружает экран автомата
    public void onStart(ActionEvent actionEvent) {
        if (!initRound()) return;
        view.setImage(slotMachineImage);
        action = new ActionChain(player1, this::updateMachineScreen);
        lblHint.setText("Кликните по игровому автомату для розыгрыша!");
        updateMachineScreen(
                "[ ? ]  [ ? ]  [ ? ]",
                "ИГРА НАЧАТА",
                "Раунд №" + player1.getCount(),
                "Кликните по автомату!"
        );
        updateStatsUI();
    }

    // Проверяет наличие средств у игрока и списывает 1 монетку за раунд
    public boolean initRound() {
        if (!player1.pay(1)) {
            updateMachineScreen(
                    "[ ! ]  [ ! ]  [ ! ]",
                    "НЕТ МОНЕТОК",
                    "Баланс: 0 монет",
                    "Киньте монетку (+1)"
            );
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Нет монеток");
            alert.setHeaderText("Средств на счете недостаточно, еще монетку плисс!");
            alert.show();
            action = null;
            view.setImage(coinSplashImage);
            lblHint.setText("Киньте монетку, чтобы продолжить игру.");
            updateStatsUI();
            return false;
        }
        return true;
    }

    // Обновляет показатели баланса и количества раундов на верхней панели
    private void updateStatsUI() {
        lblBalance.setText("Монеток: " + player1.getNumber());
        lblGamesCount.setText("Сыграно раундов: " + player1.getCount());
    }
}
