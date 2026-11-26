package ru.study.chain;

public final class BlackHandler extends Handler {
    public BlackHandler(Handler next) {
        super(next);
    }

    @Override public long process(BetColor selected, BetColor result, int bet) {
        if (result != BetColor.BLACK) return super.process(selected, result, bet);
        return selected == result ? (long) bet * result.getMultiplier() : 0;
    }
}
