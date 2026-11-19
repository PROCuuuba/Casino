package ru.vstu.task14.chain;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import ru.vstu.task14.model.Player;

import java.util.Optional;
import java.util.Random;

/**
 * Выигрыш: кидает свой кубик. Попал в нижний диапазон — начисляет монеты,
 * не попал — значит, никто в цепочке не выиграл, и игрок проиграл.
 */
public class PositiveHandler extends Handler {

    public static final int WIN = 3; // сколько монет дает автомат

    private static final int WIN_TO_NEWBIE = 45;  // первые 3 ставки: 0–44
    private static final int WIN_TO_REGULAR = 20; // дальше: 0–19

    private final Player player;
    private final Random generate = new Random();

    public PositiveHandler(Player player, Handler processor) {
        super(processor);
        this.player = player;
    }

    @Override
    public boolean process() {
        int roll = generate.nextInt(100);
        int to = player.getCount() < 3 ? WIN_TO_NEWBIE : WIN_TO_REGULAR;

        if (roll < to) {
            player.addNumber(WIN);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Вы выиграли!");
            alert.setHeaderText("В мешочке " + WIN + " монеты! Забрать выигрыш\nили рискнуть и сыграть на выигранное?");
            ButtonType again = new ButtonType("Играть дальше", ButtonBar.ButtonData.YES);
            ButtonType take = new ButtonType("Забрать и закончить", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(again, take);
            Optional<ButtonType> option = alert.showAndWait();
            return option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES;
        }

        // никто в цепочке не попал в свой диапазон — проигрыш
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы проиграли!");
        alert.setHeaderText("Мешочек оказался пуст — монетка потеряна,\nно всегда можно отыграться!");
        ButtonType replay = new ButtonType("Продолжить играть", ButtonBar.ButtonData.YES);
        ButtonType vacation = new ButtonType("Отдохнуть", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(replay, vacation);
        Optional<ButtonType> option = alert.showAndWait();
        return option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
}
