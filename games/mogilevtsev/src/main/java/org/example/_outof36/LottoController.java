package org.example._outof36;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.example.casino.BalanceManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.random.RandomGenerator;

public final class LottoController {
    private static final int GRID_COLUMNS = 6;
    private static final int REVEAL_STEP_MS = 200;
    private static final PseudoClass MATCH = PseudoClass.getPseudoClass("match");
    private static final PseudoClass REVEALED = PseudoClass.getPseudoClass("revealed");

    private final BalanceManager balance = BalanceManager.getInstance();
    private final IntConsumer balanceListener = this::updateBalance;
    private final RandomGenerator random;
    private final NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru-RU"));
    private final Set<Integer> ticket = new HashSet<>();
    private final List<ToggleButton> numberButtons = new ArrayList<>();
    private final List<Label> balls = new ArrayList<>();
    private final List<Label> checkmarks = new ArrayList<>();

    private PendingRound pendingRound;

    @FXML private GridPane numberGrid;
    @FXML private Label balanceValue;
    @FXML private Button topUpButton;
    @FXML private Label selectionCount;
    @FXML private Button randomButton;
    @FXML private Button resetButton;
    @FXML private Button drawButton;
    @FXML private Label gameStatus;
    @FXML private HBox drawnBalls;
    @FXML private Label roundResult;
    @FXML private GridPane prizes;

    public LottoController() {
        this(RandomGenerator.getDefault());
    }

    LottoController(RandomGenerator random) {
        this.random = random;
    }

    @FXML
    private void initialize() {
        for (int number = 1; number <= LottoRules.NUMBER_COUNT; number++) {
            int cell = number - 1;
            ToggleButton button = new ToggleButton(Integer.toString(number));
            button.setId("number-" + number);
            button.getStyleClass().add("number-button");
            button.setOnAction(event -> toggleNumber(cell + 1));
            numberGrid.add(button, cell % GRID_COLUMNS, cell / GRID_COLUMNS);
            numberButtons.add(button);
        }

        for (int index = 0; index < LottoRules.PICKS; index++) {
            Label ball = new Label();
            ball.setId("ball-" + (index + 1));
            ball.getStyleClass().add("drawn-ball");
            Label checkmark = new Label("\u2713");
            checkmark.getStyleClass().add("ball-check");
            var slot = new StackPane(ball, checkmark);
            StackPane.setAlignment(checkmark, Pos.TOP_RIGHT);
            drawnBalls.getChildren().add(slot);
            balls.add(ball);
            checkmarks.add(checkmark);
        }

        addPrizeRow(0, "0–1", LottoRules.prizeFor(0));
        for (int matches = 2; matches <= LottoRules.PICKS; matches++) {
            addPrizeRow(matches - 1, Integer.toString(matches), LottoRules.prizeFor(matches));
        }

        clearResult();
        updateBalance(balance.getCoins());
    }

    void attach(Stage stage) {
        balance.addListener(balanceListener);
        stage.addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> {
            balance.removeListener(balanceListener);
            finishRound(false);
        });
    }

    @FXML
    private void topUp() {
        balance.addCoins(5);
    }

    @FXML
    private void pickRandomly() {
        ticket.clear();
        ticket.addAll(LottoRules.draw(random));
        ticketChanged();
    }

    @FXML
    private void resetTicket() {
        ticket.clear();
        ticketChanged();
    }

    @FXML
    private void draw() {
        if (pendingRound != null || ticket.size() != LottoRules.PICKS || balance.getCoins() < LottoRules.STAKE) {
            return;
        }

        LottoRules.Result result = LottoRules.evaluate(ticket, LottoRules.draw(random));
        Timeline timeline = new Timeline();
        for (int index = 0; index < result.drawn().size(); index++) {
            int slot = index;
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(REVEAL_STEP_MS * (index + 1)),
                    event -> reveal(slot, result)));
        }
        timeline.setOnFinished(event -> finishRound(true));

        if (!balance.spendCoins(LottoRules.STAKE)) {
            updateControls();
            return;
        }

        pendingRound = new PendingRound(result, timeline);
        try {
            clearResult();
            roundResult.setText("Идёт розыгрыш…");
            updateControls();
            timeline.playFromStart();
        } catch (RuntimeException failure) {
            finishRound(false);
            throw failure;
        }
    }

    private void finishRound(boolean showResult) {
        PendingRound round = pendingRound;
        if (round == null) {
            return;
        }

        pendingRound = null;
        round.timeline().stop();
        LottoRules.Result result = round.result();
        if (result.prize() > 0) {
            balance.addCoins(result.prize());
        }
        if (showResult) {
            roundResult.setText("Совпало %d из %d · Приз %s монет"
                    .formatted(result.matches().size(), LottoRules.PICKS, numberFormat.format(result.prize())));
            updateControls();
        }
    }

    private void reveal(int index, LottoRules.Result result) {
        int number = result.drawn().get(index);
        boolean matched = result.matches().contains(number);
        Label ball = balls.get(index);
        ball.setText(Integer.toString(number));
        ball.setAccessibleText("Выпало " + number + (matched ? ", совпадение" : ""));
        ball.pseudoClassStateChanged(REVEALED, true);
        ball.pseudoClassStateChanged(MATCH, matched);
        checkmarks.get(index).setVisible(matched);
        if (matched) {
            numberButtons.get(number - 1).pseudoClassStateChanged(MATCH, true);
        }
    }

    private void toggleNumber(int number) {
        if (!ticket.remove(number) && ticket.size() < LottoRules.PICKS) {
            ticket.add(number);
        }
        ticketChanged();
    }

    private void ticketChanged() {
        clearResult();
        updateControls();
    }

    private void clearResult() {
        roundResult.setText("Розыгрыш ещё не начался");
        for (int index = 0; index < balls.size(); index++) {
            Label ball = balls.get(index);
            ball.setText("–");
            ball.setAccessibleText("Шар " + (index + 1) + " ещё не выпал");
            ball.pseudoClassStateChanged(REVEALED, false);
            ball.pseudoClassStateChanged(MATCH, false);
            checkmarks.get(index).setVisible(false);
        }
        for (ToggleButton button : numberButtons) {
            button.pseudoClassStateChanged(MATCH, false);
        }
    }

    private void updateBalance(int coins) {
        balanceValue.setText(numberFormat.format(coins));
        updateControls();
    }

    private void updateControls() {
        boolean playing = pendingRound != null;
        boolean ticketReady = ticket.size() == LottoRules.PICKS;
        boolean canAfford = balance.getCoins() >= LottoRules.STAKE;
        selectionCount.setText("Выбрано %d из %d".formatted(ticket.size(), LottoRules.PICKS));
        for (int index = 0; index < numberButtons.size(); index++) {
            ToggleButton button = numberButtons.get(index);
            boolean selected = ticket.contains(index + 1);
            button.setSelected(selected);
            button.setDisable(playing || (ticketReady && !selected));
        }
        randomButton.setDisable(playing);
        resetButton.setDisable(playing || ticket.isEmpty());
        topUpButton.setDisable(playing);
        drawButton.setDisable(playing || !ticketReady || !canAfford);
        if (playing) {
            gameStatus.setText("Выпадают шары");
        } else if (!canAfford) {
            gameStatus.setText("Для розыгрыша нужны 2 монеты");
        } else if (ticketReady) {
            gameStatus.setText("Билет готов к розыгрышу");
        } else {
            gameStatus.setText("Отметьте пять разных чисел");
        }
    }

    private void addPrizeRow(int row, String matches, int prize) {
        Label matchLabel = new Label(matches + " из 5");
        matchLabel.getStyleClass().add("prize-matches");
        Label amountLabel = new Label(numberFormat.format(prize) + " монет");
        amountLabel.getStyleClass().add("prize-amount");
        prizes.addRow(row, matchLabel, amountLabel);
    }

    private record PendingRound(LottoRules.Result result, Timeline timeline) {
    }
}
