package games.threeinrow;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.casino.BalanceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntConsumer;

public final class ThreeInRowGame {

    private static final int SPIN_COST = 1;
    private static final String[] SYMBOLS = {"🍒", "🍋", "⭐", "💎", "🔔", "7"};

    private static final String CELL_STYLE =
            "-fx-font-size: 40px; -fx-border-color: #34495e; -fx-border-width: 2;"
                    + "-fx-background-color: #ecf0f1; -fx-min-width: 80px; -fx-min-height: 80px;";
    private static final String WIN_CELL_STYLE =
            "-fx-font-size: 40px; -fx-border-color: #f1c40f; -fx-border-width: 4;"
                    + "-fx-background-color: #fdf3d0; -fx-min-width: 80px; -fx-min-height: 80px;";

    private static final int[][][] LINES = {
            {{0, 0}, {0, 1}, {0, 2}},
            {{1, 0}, {1, 1}, {1, 2}},
            {{2, 0}, {2, 1}, {2, 2}},
            {{0, 0}, {1, 0}, {2, 0}},
            {{0, 1}, {1, 1}, {2, 1}},
            {{0, 2}, {1, 2}, {2, 2}},
            {{0, 0}, {1, 1}, {2, 2}},
            {{0, 2}, {1, 1}, {2, 0}}
    };

    private final BalanceManager wallet = BalanceManager.getInstance();
    private final Random random = new Random();
    private final Label[][] cells = new Label[3][3];
    private final Label balanceLabel = new Label();
    private final Label spinsLabel = new Label("Вращений: 0");
    private final Label statusLabel = new Label("Нажмите «Вращение». Ставка — 1 монета.");
    private final Button spinButton = new Button("Вращение (1 монета)");

    private final IntConsumer balanceListener = coins -> {
        if (Platform.isFxApplicationThread()) {
            onBalanceChanged(coins);
        } else {
            Platform.runLater(() -> onBalanceChanged(coins));
        }
    };

    private int spins;

    public Parent createRoot() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Label cell = new Label("?");
                cell.setAlignment(Pos.CENTER);
                cell.setStyle(CELL_STYLE);
                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        spinButton.setStyle("-fx-font-size: 15px; -fx-background-color: #2ecc71; -fx-text-fill: white;");
        spinButton.setOnAction(event -> spin());

        Label hint = new Label("Монеты общие для всего зала — пополнить можно кнопкой в каталоге.");
        hint.setStyle("-fx-font-size: 12px; -fx-text-fill: #4a5a63;");
        hint.setWrapText(true);
        hint.setMaxWidth(380);
        hint.setAlignment(Pos.CENTER);

        balanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        spinsLabel.setStyle("-fx-font-size: 13px;");
        statusLabel.setStyle("-fx-font-size: 14px;");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(400);
        statusLabel.setAlignment(Pos.CENTER);

        HBox stats = new HBox(20, balanceLabel, spinsLabel);
        stats.setAlignment(Pos.CENTER);

        VBox root = new VBox(14, stats, grid, spinButton, statusLabel, hint);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #bdc3c7;");

        // Подписка сразу же пришлёт текущее значение — метка заполнится сама.
        wallet.addListener(balanceListener);
        return root;
    }

    public void dispose() {
        wallet.removeListener(balanceListener);
    }

    private void spin() {
        // Ставка списывается из общего баланса; при нехватке монет ход не начинается.
        if (!wallet.spendCoins(SPIN_COST)) {
            statusLabel.setText("Недостаточно монет. Пополните общий баланс в игровом зале.");
            return;
        }

        spins++;
        spinsLabel.setText("Вращений: " + spins);
        resetCellStyles();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col].setText(SYMBOLS[random.nextInt(SYMBOLS.length)]);
            }
        }

        List<int[][]> winning = winningLines();
        if (winning.isEmpty()) {
            statusLabel.setText("Выигрышных рядов нет. Списана 1 монета.");
            return;
        }

        for (int[][] line : winning) {
            for (int[] cell : line) {
                cells[cell[0]][cell[1]].setStyle(WIN_CELL_STYLE);
            }
        }

        int payout = winning.size();          // 1 ряд — 1 монета, N рядов — N монет
        wallet.addCoins(payout);              // addCoins требует amount > 0
        statusLabel.setText("Выигрышных рядов: " + payout + ". Начислено: +" + payout + " монет.");
    }

    private List<int[][]> winningLines() {
        List<int[][]> found = new ArrayList<>();
        for (int[][] line : LINES) {
            if (isSame(line)) found.add(line);
        }
        return found;
    }

    private boolean isSame(int[][] line) {
        String first = symbol(line[0]);
        return first != null && !first.equals("?")
                && first.equals(symbol(line[1]))
                && first.equals(symbol(line[2]));
    }

    private String symbol(int[] cell) {
        return cells[cell[0]][cell[1]].getText();
    }

    private void resetCellStyles() {
        for (Label[] row : cells) {
            for (Label cell : row) cell.setStyle(CELL_STYLE);
        }
    }

    private void onBalanceChanged(int coins) {
        balanceLabel.setText("Общий баланс: " + coins + " монет");
        spinButton.setDisable(coins < SPIN_COST);
        if (coins < SPIN_COST) {
            statusLabel.setText("Монет нет. Вернитесь в каталог и пополните общий баланс.");
        }
    }
}