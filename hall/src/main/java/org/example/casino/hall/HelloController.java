package org.example.casino.hall;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.example.casino.GameMachine;

public class HelloController {
    private final GameCatalog catalog;

    @FXML private ListView<GameMachine> machines;
    @FXML private Button playButton;
    @FXML private Label status;

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
        machines.getSelectionModel().selectFirst();
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
}
