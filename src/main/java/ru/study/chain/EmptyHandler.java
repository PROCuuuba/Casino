package ru.study.chain;

public final class EmptyHandler extends Handler {
    private final Main game;

    public EmptyHandler(Main game, Handler processor) {
        super(processor);
        this.game = game;
    }

    @Override public boolean process(Integer request) {
        if (request != ActionChain.EMPTY) return super.process(request);
        return game.afterEmptyDraw();
    }
}
