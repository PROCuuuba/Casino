package chain;

import model.Player;

// Строит цепочку обработчиков без джекпота и запускает розыгрыш
public class ActionChain {
    public static final int SUCCESS = 1;
    public static final int CHANCE = 2;
    public static final int LOSS = 3;

    public static final int NUMHANDLER = 3;
    public static final int NUMMAX = 7;

    private Handler chain;
    private final Player player;
    private final Handler.ResultDisplay display;

    // Создает цепочку обработчиков для игрока без колбэка экрана
    public ActionChain(Player player) {
        this(player, null);
    }

    // Создает цепочку обработчиков с колбэком вывода результата на экран автомата
    public ActionChain(Player player, Handler.ResultDisplay display) {
        this.player = player;
        this.display = display;
        buildChain();
    }

    // Связывает три обработчика в последовательную цепочку: Positive -> Chance -> Negative
    private void buildChain() {
        chain = new PositiveHandler(
                new ChanceHandler(
                        new NegativeHandler(null, player, display),
                        player,
                        display),
                player,
                display);
    }

    // Запускает цепочку передачей стартового числа от параметров игрока
    public boolean process() {
        return process(player.getCount() + player.getNumber());
    }

    // Передает значение напрямую первому обработчику в цепочке
    public boolean process(Integer a) {
        return chain.process(a);
    }
}
