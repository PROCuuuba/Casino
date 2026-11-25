package ru.vstu.task14.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import org.example.casino.BalanceManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "casino.gui.tests", matches = "true")
class SmokeTest {
    @Test
    void loadsArtworkAndSpendsOnlyAvailableCoins() throws Exception {
        var check = new FutureTask<Void>(() -> {
            BalanceManager balance = BalanceManager.getInstance();
            if (balance.getCoins() > 0) balance.spendCoins(balance.getCoins());
            balance.addCoins(10);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
            loader.load();
            Controller controller = loader.getController();
            var coin = controller.view.getImage();
            assertFalse(coin.isError());
            assertEquals(10, controller.player1.getNumber());
            controller.onPay(null);
            assertEquals(11, controller.player1.getNumber());
            controller.onStart(null);
            assertNotNull(controller.action);
            assertNotSame(coin, controller.view.getImage());
            assertFalse(controller.view.getImage().isError());
            assertEquals(10, controller.player1.getNumber());
            assertFalse(controller.player1.pay(1000));
            assertEquals(1, controller.player1.getCount());
            return null;
        });
        Platform.startup(check);
        try {
            check.get(15, TimeUnit.SECONDS);
        } finally {
            Platform.exit();
        }
    }
}
