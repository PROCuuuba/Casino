package ru.vstu.task14.chain;

import ru.vstu.task14.model.Player;

/**
 * Розыгрыш: пускает ход по цепочке обработчиков. Каждый кидает свой кубик,
 * кто попал в свой диапазон — тот и обрабатывает ход; если никто не попал,
 * проигрывает последний обработчик.
 */
public class ActionChain {

    private final Player player;

    public ActionChain(Player player) {
        this.player = player;
    }

    /** Собирает цепочку обработчиков прямо в момент розыгрыша. */
    private Handler buildChain() {
        return new NegativeHandler(player,
                new ChanceHandler(player,
                        new PositiveHandler(player, null)));
    }

    /** Ход игры. */
    public boolean play() {
        return buildChain().process();
    }
}
