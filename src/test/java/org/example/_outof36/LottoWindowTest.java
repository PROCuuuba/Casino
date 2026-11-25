package org.example._outof36;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.casino.BalanceManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfSystemProperty(named = "casino.gui.tests", matches = "true")
class LottoWindowTest {
    @BeforeAll
    static void startFx() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    @AfterAll
    static void stopFx() {
        Platform.exit();
    }

    @Test
    void playsOnceAndShowsTheVerifiedPrizeAndMatches() throws Exception {
        withGame(stage -> {
            stage.setWidth(850);
            stage.setHeight(680);
            Parent root = stage.getScene().getRoot();
            assertTextLayout(root);
            Button draw = button(root, "#drawButton");
            selectTwoMatches(root);
            assertTrue(draw.isDisabled());
            button(root, "#topUpButton").fire();
            button(root, "#topUpButton").fire();
            assertFalse(draw.isDisabled());
            assertTextLayout(root);

            draw.fire();
            draw.fire();
            assertEquals(8, BalanceManager.getInstance().getCoins());
            assertTrue(number(root, 1).isDisabled());
            assertTrue(button(root, "#resetButton").isDisabled());
            assertTrue(button(root, "#randomButton").isDisabled());

            Label result = label(root, "#roundResult");
            assertEquals("Идёт розыгрыш…", result.getText());
            assertTextLayout(root);
            waitForResult(result);
            assertEquals("Совпало 2 из 5 · Приз 5 монет", result.getText());
            assertTextLayout(root);
            assertEquals(13, BalanceManager.getInstance().getCoins());
            assertEquals("13", label(root, "#balanceValue").getText());
            assertEquals("1", label(root, "#ball-1").getText());
            assertTrue(label(root, "#ball-1").getAccessibleText().contains("совпадение"));
            assertFalse(label(root, "#ball-3").getAccessibleText().contains("совпадение"));
            assertEquals(2, root.lookupAll(".ball-check").stream().filter(node -> node.isVisible()).count());
            assertFalse(draw.isDisabled());

            button(root, "#resetButton").fire();
            assertEquals("Выбрано 0 из 5", label(root, "#selectionCount").getText());
            assertTrue(draw.isDisabled());
            button(root, "#randomButton").fire();
            assertEquals("Выбрано 5 из 5", label(root, "#selectionCount").getText());
            assertFalse(draw.isDisabled());
        });
    }

    @Test
    void settlesThePaidRoundOnceWhenClosedDuringReveal() throws Exception {
        withGame(stage -> {
            Parent root = stage.getScene().getRoot();
            selectTwoMatches(root);
            button(root, "#topUpButton").fire();
            button(root, "#drawButton").fire();
            assertEquals(3, BalanceManager.getInstance().getCoins());

            stage.close();
            assertEquals(8, BalanceManager.getInstance().getCoins());
            stage.close();
            assertEquals(8, BalanceManager.getInstance().getCoins());
        });
    }

    private static void assertTextLayout(Parent root) {
        root.applyCss();
        root.layout();
        boolean arialAvailable = Font.getFamilies().contains("Arial");
        Stream.of(".label", ".button", ".toggle-button")
                .flatMap(selector -> root.lookupAll(selector).stream())
                .filter(node -> node.isVisible())
                .map(Labeled.class::cast)
                .filter(control -> control.getText() != null && !control.getText().isEmpty())
                .forEach(control -> {
                    Text rendered = (Text) control.lookup(".text");
                    String caption = control.getText();
                    assertEquals(caption, rendered.getText(), caption);
                    assertTrue(rendered.getLayoutBounds().getWidth() <= control.getWidth(), caption);
                    assertTrue(rendered.getLayoutBounds().getHeight() <= control.getHeight(), caption);
                    if (arialAvailable) {
                        assertEquals("Arial", control.getFont().getFamily(), caption);
                    }
                });
    }

    private static void waitForResult(Label result) {
        Object eventLoop = new Object();
        result.textProperty().addListener((observable, previous, current) -> {
            if (current.startsWith("Совпало")) {
                Platform.exitNestedEventLoop(eventLoop, true);
            }
        });
        PauseTransition timeout = new PauseTransition(Duration.seconds(5));
        timeout.setOnFinished(event -> Platform.exitNestedEventLoop(eventLoop, false));
        timeout.play();
        try {
            assertEquals(true, Platform.enterNestedEventLoop(eventLoop));
        } finally {
            timeout.stop();
        }
    }

    private static void selectTwoMatches(Parent root) {
        for (int number : new int[]{1, 2, 11, 12, 13}) {
            number(root, number).fire();
        }
        assertEquals("Выбрано 5 из 5", label(root, "#selectionCount").getText());
        assertTrue(number(root, 14).isDisabled());
        number(root, 14).fire();
        assertFalse(number(root, 14).isSelected());
    }

    private static Button button(Parent root, String selector) {
        return (Button) root.lookup(selector);
    }

    private static Label label(Parent root, String selector) {
        return (Label) root.lookup(selector);
    }

    private static ToggleButton number(Parent root, int value) {
        return (ToggleButton) root.lookup("#number-" + value);
    }

    private static void withGame(WindowCheck check) throws Exception {
        var task = new FutureTask<Void>(() -> {
            BalanceManager balance = BalanceManager.getInstance();
            if (balance.getCoins() > 0) {
                balance.spendCoins(balance.getCoins());
            }
            Stage stage = new Stage();
            try {
                loadGame(stage);
                check.run(stage);
                return null;
            } finally {
                stage.close();
            }
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    private static void loadGame(Stage stage) throws IOException {
        Random fixedDraw = new Random() {
            @Override
            public int nextInt(int origin, int bound) {
                return origin;
            }
        };
        FXMLLoader loader = new FXMLLoader(LottoApplication.class.getResource("lotto-view.fxml"));
        loader.setControllerFactory(type -> new LottoController(fixedDraw));
        stage.setScene(new Scene(loader.load(), 884, 660));
        loader.<LottoController>getController().attach(stage, true);
        stage.show();
    }

    @FunctionalInterface
    private interface WindowCheck {
        void run(Stage stage);
    }
}
