package ru.study.chain;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public final class Main extends Application {
    private final RouletteGame game = new RouletteGame();
    private final Group wheel = new Group();
    private final Label balance = new Label();
    private final Label status = new Label("Выберите цвет и сделайте ставку.");
    private final TextField bet = new TextField("10");
    private final HBox presetRow = new HBox(8);
    private final Button spin = new Button("Крутить");
    private final Button addCoins = new Button("+10 монет");
    private final ToggleGroup colors = new ToggleGroup();
    private HBox colorRow;
    private boolean spinning;
    private RotateTransition animation;

    @Override public void start(Stage stage) {
        prepare(stage);
        stage.show();
    }

    void prepare(Stage stage) {
        Label title = new Label("Рулетка");
        title.setStyle("-fx-font-size: 27px; -fx-font-weight: bold;");
        balance.setStyle("-fx-font-size: 17px;");

        Pane wheelPane = createWheel();
        colorRow = new HBox(8);
        colorRow.setAlignment(Pos.CENTER);
        for (BetColor color : BetColor.values()) {
            ToggleButton button = new ToggleButton(color.getTitle() + " ×" + color.getMultiplier());
            button.setUserData(color);
            button.setToggleGroup(colors);
            button.setPrefSize(135, 40);
            button.selectedProperty().addListener((obs, oldValue, selected) -> {
                button.setStyle("-fx-background-color: " + color.getHex()
                        + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 7;"
                        + "-fx-border-radius: 7; -fx-border-width: 3; -fx-border-color: "
                        + (selected ? "#dbab45" : "transparent") + ";");
            });
            button.setSelected(true);
            colorRow.getChildren().add(button);
        }
        colors.selectToggle(colors.getToggles().get(0));
        colors.selectedToggleProperty().addListener((obs, oldValue, value) -> refresh());

        bet.setPrefWidth(105);
        bet.setId("betAmount");
        bet.setMaxWidth(105);
        bet.setPromptText("1–10000");
        bet.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("[0-9]{0,5}") ? change : null));
        bet.textProperty().addListener((obs, oldValue, value) -> refresh());
        bet.setOnAction(e -> spinWheel());
        presetRow.setAlignment(Pos.CENTER);
        for (int amount : new int[]{10, 25, 50, 100}) {
            Button preset = new Button(String.valueOf(amount));
            preset.setPrefWidth(65);
            preset.setOnAction(e -> bet.setText(String.valueOf(amount)));
            presetRow.getChildren().add(preset);
        }
        spin.setPrefSize(110, 36);
        spin.setId("spinWheel");
        balance.setId("rouletteBalance");
        status.setId("rouletteStatus");
        spin.setStyle("-fx-background-color: #dbab45; -fx-font-weight: bold; -fx-background-radius: 7;");
        spin.setOnAction(e -> spinWheel());
        addCoins.setOnAction(e -> {
            game.getPlayer().addCoins(10);
            refresh();
        });

        HBox stakeRow = new HBox(10, new Label("Ставка:"), bet, spin);
        stakeRow.setAlignment(Pos.CENTER);
        HBox balanceRow = new HBox(15, balance, addCoins);
        balanceRow.setAlignment(Pos.CENTER);
        status.setWrapText(true);
        status.setAlignment(Pos.CENTER);
        status.setMinHeight(42);
        status.setMaxWidth(430);
        Label hint = new Label("Выплата включает ставку. Все 9 секторов равновероятны.");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #746e65;");

        VBox root = new VBox(10, title, balanceRow, wheelPane, colorRow, stakeRow, presetRow, status, hint);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f0e7; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px;");
        stage.setTitle("Рулетка");
        stage.setScene(new Scene(root, 500, 760));
        stage.setMinWidth(500);
        stage.setMinHeight(800);
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
            if (animation != null) animation.stop();
            spinning = false;
        });
        refresh();
    }

    private Pane createWheel() {
        double angle = 360.0 / game.getSectorCount();
        for (int i = 0; i < game.getSectorCount(); i++) {
            Arc sector = new Arc(0, 0, 135, 135, 90 - i * angle, -angle);
            sector.setType(ArcType.ROUND);
            sector.setFill(Color.web(game.getSectorColor(i).getHex()));
            sector.setStroke(Color.web("#f5f0e7"));
            sector.setStrokeWidth(2);
            wheel.getChildren().add(sector);
        }
        Circle rim = new Circle(135, Color.TRANSPARENT);
        rim.setStroke(Color.web("#b59253"));
        rim.setStrokeWidth(5);
        Circle center = new Circle(17, Color.web("#e9ca86"));
        wheel.getChildren().addAll(rim, center);
        wheel.setLayoutX(160);
        wheel.setLayoutY(160);
        Polygon pointer = new Polygon(148, 13, 172, 13, 160, 36);
        pointer.setFill(Color.web("#dbab45"));
        pointer.setStroke(Color.web("#795f2e"));
        Pane pane = new Pane(wheel, pointer);
        pane.setMinSize(320, 310);
        pane.setPrefSize(320, 310);
        pane.setMaxSize(320, 310);
        return pane;
    }

    private void spinWheel() {
        if (spinning || colors.getSelectedToggle() == null) return;
        BetColor selected = (BetColor) colors.getSelectedToggle().getUserData();
        int amount = getBetAmount();
        if (amount < 1 || amount > 10000) {
            status.setText("Введите ставку от 1 до 10000 монет.");
            return;
        }
        RouletteGame.SpinResult result;
        try {
            result = game.play(selected, amount);
        } catch (IllegalArgumentException ex) {
            status.setText(ex.getMessage());
            refresh();
            return;
        }
        spinning = true;
        balance.setText("Монеты: " + (game.getPlayer().getBalance() - result.payout()));
        status.setText("Колесо вращается…");
        refreshControls();

        double angle = 360.0 / game.getSectorCount();
        double target = 360 - (result.sector() + 0.5) * angle;
        double current = (wheel.getRotate() % 360 + 360) % 360;
        double delta = (target - current + 360) % 360;
        animation = new RotateTransition(Duration.seconds(3), wheel);
        animation.setByAngle(4 * 360 + delta);
        animation.setInterpolator(Interpolator.EASE_OUT);
        animation.setOnFinished(e -> {
            wheel.setRotate(target);
            spinning = false;
            status.setText("Выпал " + result.color().getTitle().toLowerCase() + ". "
                    + (result.payout() > 0 ? "Выплата: " + result.payout() + " монет."
                    : "Ставка проиграна: " + amount + " монет."));
            refresh();
        });
        animation.play();
    }

    private void refresh() {
        if (!spinning) balance.setText("Монеты: " + game.getPlayer().getBalance());
        refreshControls();
    }

    private void refreshControls() {
        int amount = getBetAmount();
        spin.setDisable(spinning || colors.getSelectedToggle() == null
                || amount < 1 || amount > 10000 || amount > game.getPlayer().getBalance());
        bet.setDisable(spinning);
        presetRow.setDisable(spinning);
        addCoins.setDisable(spinning);
        if (colorRow != null) colorRow.setDisable(spinning);
    }

    private int getBetAmount() {
        return bet.getText().isEmpty() ? 0 : Integer.parseInt(bet.getText());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
