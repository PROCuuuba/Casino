package ru.study.chain;

public final class PositiveHandler extends Handler {
    private final Main game;

    public PositiveHandler(Main game, Handler processor) {
        super(processor);
        this.game = game;
    }

    @Override public boolean process(Integer request) {
        if (request != ActionChain.SUCCESS) return super.process(request);
        return game.afterWin();
    }
}
