package games.threeinrow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class ThreeInRowGame {
    private static final int SPIN_COST = 1;
    private static final String[] SYMBOLS = {"🍒", "🍋", "⭐️", "💎", "🔔", "7"};
    private static final String DEFAULT_CELL_STYLE =
            "-fx-font-size: 40px; " +
            "-fx-border-color: #34495e; " +
            "-fx-border-width: 2; " +
            "-fx-background-color: #ecf0f1; " +
            "-fx-min-width: 80px; " +
            "-fx-min-height: 80px;";

    private final Stage stage;
    private final Runnable onReturnToCatalog;
    private final Random random = new Random();
    private final Label[][] cells = new Label[3][3];
    private final Label balanceLabel = new Label();
    private final Label statusLabel = new Label("Вставьте монету и нажмите «Вращение»");

    private int balance = 0;

    public ThreeInRowGame(Stage stage, Runnable onReturnToCatalog) {
        this.stage = stage;
        this.onReturnToCatalog = onReturnToCatalog;
    }

    public void start() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Label cell = new Label("?");
                cell.setAlignment(Pos.CENTER);
                cell.setStyle(DEFAULT_CELL_STYLE);
                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        Button addCoinButton = new Button("Вставить монету");
        addCoinButton.setOnAction(e -> {
            balance++;
            updateUI();
            statusLabel.setText("Монета добавлена. Можно вращать.");
        });

        Button spinButton = new Button("Вращение");
        spinButton.setOnAction(e -> spin());

        Button backButton = new Button("В каталог");
        backButton.setOnAction(e -> onReturnToCatalog.run());

        HBox controls = new HBox(10, addCoinButton, spinButton, backButton);
        controls.setAlignment(Pos.CENTER);

        balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        statusLabel.setStyle("-fx-font-size: 14px;");

        VBox root = new VBox(15, balanceLabel, grid, controls, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #bdc3c7;");

        Scene scene = new Scene(root, 650, 450);
        stage.setTitle(getTitle());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            e.consume();
            onReturnToCatalog.run();
        });

        updateUI();
    }

    private void spin() {
        if (balance < SPIN_COST) {
            statusLabel.setText("Недостаточно монет. Вставьте монету.");
            return;
        }

        // Каждый ход списывает 1 монету
        balance -= SPIN_COST;
        resetCellStyles();

        // Вращение барабанов
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col].setText(SYMBOLS[random.nextInt(SYMBOLS.length)]);
            }
        }

        int winningLines = countWinningLines();

        if (winningLines == 0) {
            statusLabel.setText("Выигрышных рядов нет. Списана 1 монетка.");
        } else {
            // 1 монетка за каждый выигрышный ряд
            int payout = winningLines;
            balance += payout;
            statusLabel.setText("Выигрышных рядов: " + winningLines
                    + ". Начислено: +" + payout + " монет.");
        }

        updateUI();
    }

    private int countWinningLines() {
        int count = 0;
        // Горизонтальные линии
        for (int row = 0; row < 3; row++) {
            if (isSame(row, 0, row, 1, row, 2)) {
                count++;
            }
        }

        // Вертикальные линии
        for (int col = 0; col < 3; col++) {
            if (isSame(0, col, 1, col, 2, col)) {
                count++;
            }
        }

        // Диагонали
        if (isSame(0, 0, 1, 1, 2, 2)) {
            count++;
        }
        if (isSame(0, 2, 1, 1, 2, 0)) {
            count++;
        }

        return count;
    }

    private boolean isSame(int r1, int c1, int r2, int c2, int r3, int c3) {
        String a = cells[r1][c1].getText();
        return a != null
                && !a.equals("?")
                && a.equals(cells[r2][c2].getText())
                && a.equals(cells[r3][c3].getText());
    }

    private void resetCellStyles() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                cells[row][col].setStyle(DEFAULT_CELL_STYLE);
            }
        }
    }

    private void updateUI() {
        balanceLabel.setText("Баланс: " + balance + " монет");
    }

    private String getTitle() {
        return "Три в ряд";
    }
}