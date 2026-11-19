package ru.vstu.task14.chain;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import ru.vstu.task14.model.Player;

import java.util.Optional;
import java.util.Random;

/**
 * Проигрыш: кидает свой кубик и забирает ход, если выпал проигрышный
 * диапазон, иначе передаёт ход дальше по цепочке.
 */
public class NegativeHandler extends Handler {

    private static final int LOSS_FROM_NEWBIE = 75;  // первые 3 ставки: 75–99
    private static final int LOSS_FROM_REGULAR = 45; // дальше: 45–99

    private final Player player;
    private final Random generate = new Random();

    public NegativeHandler(Player player, Handler processor) {
        super(processor);
        this.player = player;
    }

    @Override
    public boolean process() {
        int roll = generate.nextInt(100);
        int from = player.getCount() < 3 ? LOSS_FROM_NEWBIE : LOSS_FROM_REGULAR;
        if (roll < from) {
            return super.process(); // не наш случай — дальше
        }

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
