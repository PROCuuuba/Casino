package org.example.testslotmachine;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.casino.BalanceManager;
import org.example.testslotmachine.OrbitaRules.Symbol;

import java.util.function.IntConsumer;

final class OrbitaView {
    private final BalanceManager balance = BalanceManager.getInstance();
    private final Label balanceValue = label("", "balance-value");
    private final Label result = label("Пополните баланс или сделайте ставку", "result");
    private final Label firstReel = label("—", "reel");
    private final Label secondReel = label("—", "reel");
    private final Label thirdReel = label("—", "reel");
    private final Button spin = new Button("Крутить · 1 монета");
    private final IntConsumer balanceListener = coins -> {
        if (Platform.isFxApplicationThread()) {
            updateBalance(coins);
        } else {
            Platform.runLater(() -> updateBalance(balance.getCoins()));
        }
    };

    Parent createContent() {
        balanceValue.setId("orbitaBalance");
        result.setId("orbitaResult");
        firstReel.setId("orbitaReel1");
        secondReel.setId("orbitaReel2");
        thirdReel.setId("orbitaReel3");

        VBox wallet = new VBox(1, label("Общий баланс", "muted"), balanceValue);
        wallet.setAlignment(Pos.CENTER_RIGHT);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(14, label("Орбита", "game-title"), spacer, wallet);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox reels = new HBox(12, firstReel, secondReel, thirdReel);
        reels.setAlignment(Pos.CENTER);
        result.setAccessibleRoleDescription("Результат вращения");
        result.setMinHeight(Region.USE_PREF_SIZE);

        spin.setId("orbitaSpin");
        spin.getStyleClass().add("spin-button");
        spin.setDefaultButton(true);
        spin.setOnAction(event -> playRound());
        Button addCoin = new Button("+1 монета");
        addCoin.setId("orbitaAddCoin");
        addCoin.setOnAction(event -> balance.addCoins(1));
        HBox actions = new HBox(10, spin, addCoin);
        actions.setAlignment(Pos.CENTER);

        Label rules = label("Пара ×1  ·  Три одинаковых ×6", "muted");
        VBox content = new VBox(17, header, reels, result, actions, rules);
        content.setAlignment(Pos.TOP_CENTER);
        content.getStyleClass().add("orbita");
        return content;
    }

    void observeBalance() {
        balance.addListener(balanceListener);
    }

    void stopObservingBalance() {
        balance.removeListener(balanceListener);
    }

    private void updateBalance(int coins) {
        balanceValue.setText(Integer.toString(coins));
        spin.setDisable(coins < OrbitaRules.STAKE);
    }

    private void playRound() {
        if (!balance.spendCoins(OrbitaRules.STAKE)) {
            result.setText("Недостаточно монет");
            return;
        }

        Symbol first = OrbitaRules.draw();
        Symbol second = OrbitaRules.draw();
        Symbol third = OrbitaRules.draw();
        showSymbol(firstReel, first);
        showSymbol(secondReel, second);
        showSymbol(thirdReel, third);

        int payout = OrbitaRules.payout(first, second, third);
        if (payout > 0) {
            balance.addCoins(payout);
        }
        result.setText(switch (payout) {
            case OrbitaRules.TRIPLE_PAYOUT -> "Три одинаковых! Выплата: 6 монет";
            case OrbitaRules.PAIR_PAYOUT -> "Пара. Ставка возвращена";
            default -> "Все символы разные. Выплаты нет";
        });
    }

    private static void showSymbol(Label reel, Symbol symbol) {
        reel.setText(symbol.glyph);
        reel.setAccessibleText(symbol.title);
        reel.getStyleClass().setAll("label", "reel", symbol.name().toLowerCase(java.util.Locale.ROOT));
    }

    private static Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
