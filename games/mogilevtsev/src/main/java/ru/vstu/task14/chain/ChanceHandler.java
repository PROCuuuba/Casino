package ru.vstu.task14.chain;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import ru.vstu.task14.model.Player;

import java.util.Random;

/**
 * Шанс: кидает свой кубик и забирает ход, если выпал средний диапазон —
 * ставка возвращается, розыгрыш выходит бесплатным.
 */
public class ChanceHandler extends Handler {

    private static final int CHANCE_FROM_NEWBIE = 45;  // первые 3 ставки: 45–74
    private static final int CHANCE_TO_NEWBIE = 75;
    private static final int CHANCE_FROM_REGULAR = 20; // дальше: 20–44
    private static final int CHANCE_TO_REGULAR = 45;

    private final Player player;
    private final Random generate = new Random();

    public ChanceHandler(Player player, Handler processor) {
        super(processor);
        this.player = player;
    }

    @Override
    public boolean process() {
        int roll = generate.nextInt(100);
        int from = player.getCount() < 3 ? CHANCE_FROM_NEWBIE : CHANCE_FROM_REGULAR;
        int to = player.getCount() < 3 ? CHANCE_TO_NEWBIE : CHANCE_TO_REGULAR;
        if (roll < from || roll >= to) {
            return super.process(); // не наш случай — дальше
        }

        player.addNumber(1); // возвращаем ставку
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Почти проиграли!");
        alert.setHeaderText("Но судьба дает Вам шанс — монетка возвращена,\nследующий розыгрыш бесплатно!");
        alert.getButtonTypes().setAll(new ButtonType("Спасибо, судьба!"));
        alert.showAndWait();
        return true;
    }
}
