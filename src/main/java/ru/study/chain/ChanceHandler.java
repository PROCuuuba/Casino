package ru.study.chain;

public final class ChanceHandler extends Handler {
    private final Main game;

    public ChanceHandler(Main game, Handler processor) {
        super(processor);
        this.game = game;
    }

    @Override public boolean process(Integer request) {
        if (request != ActionChain.CHANCE) return super.process(request);
        return game.afterChance();
    }
}
