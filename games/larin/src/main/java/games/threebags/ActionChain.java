package games.threebags;

import java.util.Random;

public class ActionChain {
    Handler chain;
    Player player;

    public static int SUCCESS = 1;
    public static int CHANCE = 2;
    public static int LOSS = 3;
    private boolean requireLossNext = false;
    Random generate;
    final int NUMHANDLER = 3;

    public ActionChain(Player player) {
        this.player = player;
        generate = new Random();
        buildChain();
    }

    private void buildChain() {
        chain = new PositiveHandler(new ChanceHandler(new NegativeHandler(null), player), player);
    }

    public boolean process() {
        int type;

        if (requireLossNext) {
            type = LOSS;
            requireLossNext = false;
        } else {
            type = generate.nextInt(NUMHANDLER) + 1;

            if (type == SUCCESS) {
                requireLossNext = true;
            }
        }

        return process(type);
    }


    public boolean process(Integer a) {
        return chain.process(a);
    }
}
