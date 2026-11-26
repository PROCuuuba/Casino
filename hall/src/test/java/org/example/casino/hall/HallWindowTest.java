package org.example.casino.hall;

import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.example.casino.BalanceManager;
import org.example.casino.GameMachine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "casino.gui.tests", matches = "true")
class HallWindowTest {
    record Play(String id, String pay, String funded, String start, String started) {}

    static Stream<Play> bagGames() {
        return Stream.of(
                new Play("barkalov.gamble", "Внести монету", "Монета внесена. Нажмите «Начать игру».", "Начать игру", "Выберите один из трёх мешочков."),
                new Play("chernixa.ludka", "Кинуть монетку", "Монеток у игрока: 1", "Старт игры", "Игра идет..."),
                new Play("korentsov.pixel", "Кинуть монетку (+1)", "Монеток: 1", "Начать игру", "Сыграно раундов: 1"),
                new Play("varganov.bags", "Добавить 1 монету", "Монеты: 1 | Сыграно попыток: 0", "Начать игру", "Выберите любой сундук.")
        );
    }

    @BeforeAll
    static void startFx() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    @AfterAll
    static void stopFx() {
        Platform.exit();
    }

    @BeforeEach
    void resetBalance() throws Exception {
        var reset = new FutureTask<Void>(() -> {
            BalanceManager balance = BalanceManager.getInstance();
            if (balance.getCoins() > 0) balance.spendCoins(balance.getCoins());
            return null;
        });
        Platform.runLater(reset);
        reset.get(5, TimeUnit.SECONDS);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bagGames")
    void startsTheSelectedGameAndReturnsViaItsButton(Play scenario) throws Exception {
        withHall(hall -> {
            play(hall, scenario.id(), game -> {
                Parent root = game.getScene().getRoot();
                button(root, scenario.pay()).fire();
                assertTrue(labels(root).contains(scenario.funded()), labels(root).toString());
                button(root, scenario.start()).fire();
                assertTrue(labels(root).contains(scenario.started()), labels(root).toString());
                button(root, "В каталог").fire();
            });
        });
    }

    @Test
    void aFailedLaunchDoesNotPreventPlayingOtherGamesInTheSameHall() throws Exception {
        withHall(hall -> {
            select(hall, "test.probe");
            button(hall.getScene().getRoot(), "Открыть автомат").fire();
            assertTrue(status(hall).contains("Имитированный отказ запуска"), status(hall));
            assertEquals(List.of(hall), List.copyOf(Window.getWindows()));
            play(hall, "varganov.bags", game -> {
                game.fireEvent(new WindowEvent(game, WindowEvent.WINDOW_CLOSE_REQUEST));
            });
            play(hall, "barkalov.gamble", game -> button(game.getScene().getRoot(), "В каталог").fire());
        });
    }

    @Test
    void finishesAPaidRoundBeforeOpeningAnotherMachine() throws Exception {
        withHall(hall -> {
            BalanceManager balance = BalanceManager.getInstance();
            balance.addCoins(100);
            CompletableFuture<Void> completed = new CompletableFuture<>();
            int[] settled = new int[1];
            select(hall, "putilin.pouches");
            Parent card = (Parent) hall.getScene().lookup(".putilin-pouches");
            assertTrue(labels(card).contains("Рулетка — Путилин"));
            assertTrue(labels(card).contains("Ставки на цвет: красный и чёрный ×2, зелёный ×5"));
            play(hall, "putilin.pouches", game -> {
                Parent root = game.getScene().getRoot();
                ((TextField) root.lookup("#betAmount")).setText("25");
                root.lookupAll(".toggle-button").stream().map(ToggleButton.class::cast)
                        .filter(toggle -> toggle.getText().equals("Зелёный ×5")).findFirst().orElseThrow().fire();
                button(root, "Крутить").fire();
                settled[0] = balance.getCoins();
                assertTrue(settled[0] == 75 || settled[0] == 200);
                PauseTransition wait = new PauseTransition(Duration.seconds(3.5));
                wait.setOnFinished(event -> {
                    try {
                        assertTrue(((Label) root.lookup("#rouletteStatus")).getText().startsWith("Выпал "));
                        assertEquals("Монеты: " + settled[0], ((Label) root.lookup("#rouletteBalance")).getText());
                        assertEquals(settled[0], balance.getCoins());
                        completed.complete(null);
                    } catch (Throwable failure) {
                        completed.completeExceptionally(failure);
                    } finally {
                        button(root, "В каталог").fire();
                    }
                });
                wait.play();
            });
            completed.get(2, TimeUnit.SECONDS);
            assertEquals("🪙 " + settled[0], ((Label) hall.getScene().lookup("#balance")).getText());
            play(hall, "barkalov.gamble", game -> {
                Parent root = game.getScene().getRoot();
                assertTrue(labels(root).contains(Integer.toString(settled[0])), labels(root).toString());
                button(root, "Начать игру").fire();
                assertEquals(settled[0] - 1, balance.getCoins());
                button(root, "В каталог").fire();
            });
            assertEquals(settled[0] - 1, balance.getCoins());
        });
    }

    @Test
    void playsThreeInRowAndLottoWithSharedBalance() throws Exception {
        withHall(hall -> {
            BalanceManager balance = BalanceManager.getInstance();
            balance.addCoins(5);
            play(hall, "larin.threerows", game -> {
                Parent root = game.getScene().getRoot();
                assertTrue(labels(root).contains("Общий баланс: 5 монет"), labels(root).toString());
                button(root, "Вращение (1 монета)").fire();
                assertTrue(labels(root).contains("Вращений: 1"), labels(root).toString());
                button(root, "В каталог").fire();
            });
            if (balance.getCoins() < 2) {
                balance.addCoins(2);
            }
            int beforeDraw = balance.getCoins();
            play(hall, "barkalov.lotto536", game -> {
                Parent root = game.getScene().getRoot();
                ((Button) root.lookup("#randomButton")).fire();
                ((Button) root.lookup("#drawButton")).fire();
                assertEquals(beforeDraw - 2, balance.getCoins());
                button(root, "В каталог").fire();
            });
            assertTrue(balance.getCoins() >= beforeDraw - 2);
        });
    }

    private static GameMachine select(Stage hall, String id) {
        @SuppressWarnings("unchecked")
        var list = (ListView<GameMachine>) hall.getScene().lookup("#machines");
        var machine = list.getItems().stream().filter(item -> item.id().equals(id)).findFirst().orElseThrow();
        list.getSelectionModel().select(machine);
        return machine;
    }

    private static void play(Stage hall, String id, WindowCheck action) throws Exception {
        GameMachine machine = select(hall, id);
        var interaction = new FutureTask<Void>(() -> {
            try {
                var game = Window.getWindows().stream().filter(window -> window != hall)
                        .map(Stage.class::cast).findFirst().orElseThrow();
                assertEquals(machine.title(), game.getTitle());
                assertEquals(Modality.APPLICATION_MODAL, game.getModality());
                action.run(game);
                return null;
            } catch (Exception | AssertionError failure) {
                List.copyOf(Window.getWindows()).stream().filter(window -> window != hall).forEach(Window::hide);
                throw failure;
            }
        });
        Platform.runLater(interaction);
        button(hall.getScene().getRoot(), "Открыть автомат").fire();
        assertTrue(interaction.isDone(), () -> "Игра не запустилась: " + status(hall));
        interaction.get(2, TimeUnit.SECONDS);
        assertEquals(List.of(hall), List.copyOf(Window.getWindows()));
        assertTrue(status(hall).contains("вернулись в каталог"));
    }

    private static Button button(Parent root, String text) {
        return root.lookupAll(".button").stream().filter(Button.class::isInstance).map(Button.class::cast)
                .filter(button -> text.equals(button.getText()) && !button.isDisabled()).findFirst().orElseThrow();
    }

    private static List<String> labels(Parent root) {
        return root.lookupAll(".label").stream().map(Label.class::cast).map(Label::getText).toList();
    }

    private static String status(Stage hall) {
        return ((Label) hall.getScene().lookup("#status")).getText();
    }

    @FunctionalInterface
    interface WindowCheck {
        void run(Stage stage) throws Exception;
    }

    private static void withHall(WindowCheck check) throws Exception {
        var task = new FutureTask<Void>(() -> {
            Stage hall = new Stage();
            try {
                new HelloApplication().start(hall);
                check.run(hall);
                return null;
            } finally {
                hall.close();
            }
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }
}
