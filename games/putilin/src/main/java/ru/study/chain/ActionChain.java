package ru.study.chain;

import java.util.Random;

public final class ActionChain {
    public static final int SUCCESS = 1;
    public static final int CHANCE = 2;
    public static final int LOSS = 3;
    public static final int EMPTY = 4;

    private static final int NUM_HANDLER = 4;
    private final Random generate = new Random();
    private final Handler chain;

    public ActionChain(Main game) {
        chain = new NegativeHandler(game,
                new PositiveHandler(game,
                        new ChanceHandler(game,
                                new EmptyHandler(game, null))));
    }

    public boolean process() {
        int result = generate.nextInt(NUM_HANDLER);
        return process(result);
    }

    public boolean process(Integer result) {
        return chain.process(1 + result % NUM_HANDLER);
    }
}
