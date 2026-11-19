package ru.study.chain;

public final class NegativeHandler extends Handler {
    private final Main game;

    public NegativeHandler(Main game, Handler processor) {
        super(processor);
        this.game = game;
    }

    @Override public boolean process(Integer request) {
        if (request != ActionChain.LOSS) return super.process(request);
        return game.afterLoss();
    }
}
