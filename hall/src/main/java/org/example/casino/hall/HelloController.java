package org.example.casino.hall;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.casino.BalanceManager;
import org.example.casino.GameMachine;

public class HelloController {
    private final GameCatalog catalog;
    private final BalanceManager balanceManager = BalanceManager.getInstance();
    private final BackgroundMusic music = new BackgroundMusic();

    @FXML private ListView<GameMachine> machines;
    @FXML private FlowPane cards;
    @FXML private Button playButton;
    @FXML private Button musicButton;
    @FXML private Label status;
    @FXML private Label balance;

    public HelloController() {
        this(GameCatalog.discover());
    }

    HelloController(GameCatalog catalog) {
        this.catalog = catalog;
    }

    @FXML
    private void initialize() {
        machines.getItems().setAll(catalog.machines());
        machines.setPlaceholder(new Label("Нет подключённых автоматов"));
        machines.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(GameMachine machine, boolean empty) {
                super.updateItem(machine, empty);
                setText(empty || machine == null ? null : machine.title());
            }
        });
        playButton.disableProperty().bind(machines.getSelectionModel().selectedItemProperty().isNull());
        machines.getSelectionModel().selectedItemProperty().addListener((value, oldMachine, newMachine) -> refreshCards());
        machines.getSelectionModel().selectFirst();
        refreshCards();
        balanceManager.addListener(coins -> Platform.runLater(() -> balance.setText("🪙 " + coins)));
        music.start();
    }

    @FXML
    private void openSelected() {
        GameMachine machine = machines.getSelectionModel().getSelectedItem();
        if (machine == null) return;
        try {
            machine.open();
            status.setText("Вы вернулись в каталог. Можно выбрать другой автомат.");
        } catch (RuntimeException | LinkageError failure) {
            Throwable cause = failure.getCause() == null ? failure : failure.getCause();
            String detail = cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
            status.setText("Не удалось открыть «" + machine.title() + "»: " + detail);
        }
    }

    @FXML
    private void addCoins() {
        balanceManager.addCoins(5);
        status.setText("В общий баланс добавлено 5 монет.");
    }

    @FXML
    private void toggleMusic() {
        boolean playing = music.toggle();
        musicButton.setText(playing ? "♫ Музыка" : "♫ Без звука");
    }

    private void refreshCards() {
        if (cards == null) return;
        cards.getChildren().clear();
        for (GameMachine machine : catalog.machines()) cards.getChildren().add(createCard(machine));
    }

    private VBox createCard(GameMachine machine) {
        Label icon = new Label(icon(machine));
        icon.getStyleClass().add("card-icon");
        Label title = new Label(machine.title());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);
        Label detail = new Label(description(machine));
        detail.getStyleClass().add("card-detail");
        detail.setWrapText(true);
        Button open = new Button("Играть");
        open.getStyleClass().add("card-button");
        open.setOnAction(event -> {
            machines.getSelectionModel().select(machine);
            openSelected();
        });
        VBox card = new VBox(9, icon, title, detail, open);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("game-card");
        card.getStyleClass().add(machine.id().replace('.', '-'));
        if (machine == machines.getSelectionModel().getSelectedItem()) card.getStyleClass().add("selected-card");
        card.setOnMouseClicked(event -> machines.getSelectionModel().select(machine));
        return card;
    }

    private String description(GameMachine machine) {
        return switch (machine.id()) {
            case "barkalov.gamble", "chernixa.ludka", "larin.threebags", "putilin.pouches" -> "Испытайте удачу и выберите мешочек";
            case "varganov.bags" -> "Откройте сундук и найдите награду";
            case "korentsov.pixel" -> "Пиксельный автомат с собственным стилем";
            case "mogilevtsev.reels" -> "Классический автомат с барабанами";
            default -> "Игровой автомат участника команды";
        };
    }

    private String icon(GameMachine machine) {
        return switch (machine.id()) {
            case "barkalov.gamble" -> "♠";
            case "chernixa.ludka" -> "♦";
            case "korentsov.pixel" -> "▣";
            case "larin.threebags" -> "♣";
            case "mogilevtsev.reels" -> "777";
            case "putilin.pouches" -> "★";
            case "varganov.bags" -> "▰";
            default -> "?";
        };
    }
}
