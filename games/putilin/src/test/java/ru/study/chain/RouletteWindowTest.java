package ru.study.chain;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Arc;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.example.casino.BalanceManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "casino.gui.tests", matches = "true")
class RouletteWindowTest {
    private final BalanceManager balance = BalanceManager.getInstance();

    @BeforeAll
    static void startFx() throws Exception {
        FutureTask<Void> started = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(started);
        started.get(10, TimeUnit.SECONDS);
    }

    @AfterAll
    static void stopFx() {
        Platform.exit();
    }

    @BeforeEach
    void resetBalance() {
        if (balance.getCoins() > 0) balance.spendCoins(balance.getCoins());
        balance.addCoins(100);
    }

    @Test
    void preparesWithoutShowingAndSupportsKeyboardAndPresetBets() throws Exception {
        onFx(() -> {
            Stage stage = new Stage();
            try {
                new Main().prepare(stage);
                assertFalse(stage.isShowing());
                stage.show();
                Parent root = stage.getScene().getRoot();
                assertEquals(9, root.lookupAll("Arc").stream().filter(Arc.class::isInstance).count());
                TextField input = (TextField) root.lookup("#betAmount");
                Button spin = (Button) root.lookup("#spinWheel");
                for (int amount : new int[]{10, 25, 50, 100}) {
                    button(root, String.valueOf(amount)).fire();
                    assertEquals(String.valueOf(amount), input.getText());
                    assertFalse(spin.isDisabled());
                }
                input.setText("37");
                assertFalse(spin.isDisabled());
                input.setText("abc");
                assertEquals("37", input.getText());
                for (String invalid : new String[]{"", "0", "10001", "101"}) {
                    input.setText(invalid);
                    assertTrue(spin.isDisabled());
                }
                input.setText("25");
                input.fireEvent(new ActionEvent());
                long settled = balance.getCoins();
                assertTrue(settled == 75 || settled == 125);
                assertTrue(spin.isDisabled());
                assertTrue(button(root, "50").isDisabled());
                input.fireEvent(new ActionEvent());
                assertEquals(settled, balance.getCoins());
            } finally {
                stage.close();
            }
            return null;
        });
    }

    @Test
    void finishingTheAnimationShowsTheSettledBalanceWithoutAnotherPayment() throws Exception {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        onFx(() -> {
            Stage stage = new Stage();
            new Main().prepare(stage);
            stage.show();
            Parent root = stage.getScene().getRoot();
            Button spin = (Button) root.lookup("#spinWheel");
            spin.fire();
            int settled = balance.getCoins();
            PauseTransition wait = new PauseTransition(Duration.seconds(3.5));
            wait.setOnFinished(event -> {
                try {
                    assertEquals(settled, balance.getCoins());
                    assertEquals("Монеты: " + settled, ((Label) root.lookup("#rouletteBalance")).getText());
                    assertTrue(((Label) root.lookup("#rouletteStatus")).getText().startsWith("Выпал "));
                    assertFalse(spin.isDisabled());
                    finished.complete(null);
                } catch (Throwable failure) {
                    finished.completeExceptionally(failure);
                } finally {
                    stage.close();
                }
            });
            wait.play();
            return null;
        });
        finished.get(10, TimeUnit.SECONDS);
    }

    @Test
    void opensThroughTheSharedAdapterAndReturnsWithoutResettingBalance() throws Exception {
        onFx(() -> {
            PouchMachine machine = new PouchMachine();
            assertEquals("putilin.pouches", machine.id());
            assertEquals("Рулетка — Путилин", machine.title());
            for (int session = 0; session < 2; session++) {
                int expectedBalance = balance.getCoins();
                FutureTask<Void> interaction = new FutureTask<>(() -> {
                    Stage stage = (Stage) Window.getWindows().getFirst();
                    try {
                        assertEquals(Modality.APPLICATION_MODAL, stage.getModality());
                        Parent root = stage.getScene().getRoot();
                        assertEquals("Монеты: " + expectedBalance,
                                ((Label) root.lookup("#rouletteBalance")).getText());
                        assertTrue(root.lookupAll(".toggle-button").stream()
                                .map(ToggleButton.class::cast).anyMatch(button -> button.getText().equals("Зелёный ×5")));
                        button(root, "+10 монет").fire();
                        assertEquals(expectedBalance + 10, balance.getCoins());
                        ((Button) root.lookup("#returnToCatalog")).fire();
                        return null;
                    } finally {
                        stage.close();
                    }
                });
                Platform.runLater(interaction);
                machine.open();
                interaction.get(5, TimeUnit.SECONDS);
                assertEquals(List.of(), List.copyOf(Window.getWindows()));
            }
            assertEquals(120, balance.getCoins());
            return null;
        });
    }

    private static Button button(Parent root, String text) {
        return root.lookupAll(".button").stream().filter(Button.class::isInstance).map(Button.class::cast)
                .filter(button -> text.equals(button.getText())).findFirst().orElseThrow();
    }

    private static <T> T onFx(java.util.concurrent.Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }
}
