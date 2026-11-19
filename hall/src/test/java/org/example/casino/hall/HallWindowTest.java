package org.example.casino.hall;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.example.casino.GameMachine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
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
                new Play("korentsov.pixel", "Кинуть монетку (+1)", "Монеток: 2", "Начать игру", "Сыграно раундов: 1"),
                new Play("larin.threebags", "🪙 Вставить монету", "Баланс: 1 монет | Ходов: 0", "▶ Начать игру", "Выберите одну из трех коробок!"),
                new Play("mogilevtsev.reels", "Заправить монетку", "Игрок: на счете 11 монет, ставок сделано 0", "Начать игру", "Игрок: на счете 10 монет, ставок сделано 1"),
                new Play("putilin.pouches", "Внести монету", "Монета добавлена.", "Старт", "Выберите один из мешочков."),
                new Play("varganov.bags", "Добавить 1 монету", "Монеты: 1 | Сыграно попыток: 0", "Начать игру", "Выберите любой мешочек.")
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
            play(hall, "putilin.pouches", game -> {
                Parent root = game.getScene().getRoot();
                button(root, "Внести монету").fire();
                button(root, "Старт").fire();
                var answer = new FutureTask<Void>(() -> {
                    var dialogs = Window.getWindows().stream().filter(window -> window != hall && window != game).toList();
                    try {
                        assertEquals(1, dialogs.size());
                        DialogPane dialog = (DialogPane) dialogs.getFirst().getScene().getRoot();
                        var finish = dialog.getButtonTypes().stream()
                                .filter(type -> type.getButtonData() == ButtonBar.ButtonData.NO).findFirst().orElseThrow();
                        ((Button) dialog.lookupButton(finish)).fire();
                    } finally {
                        dialogs.forEach(Window::hide);
                    }
                    return null;
                });
                Platform.runLater(answer);
                button(root, "1").fire();
                assertTrue(answer.isDone());
                answer.get(2, TimeUnit.SECONDS);
                assertTrue(labels(root).contains("Игра завершена."));
                button(root, "В каталог").fire();
            });
            play(hall, "larin.threebags", game -> button(game.getScene().getRoot(), "В каталог").fire());
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
                assertFalse(game.isShowing(), "Игровое окно не закрылось");
                return null;
            } finally {
                List.copyOf(Window.getWindows()).stream().filter(window -> window != hall).forEach(Window::hide);
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
