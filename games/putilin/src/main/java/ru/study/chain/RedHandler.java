package ru.study.chain;

public final class RedHandler extends Handler {
    public RedHandler(Handler next) {
        super(next);
    }

    @Override public long process(BetColor selected, BetColor result, int bet) {
        if (result != BetColor.RED) return super.process(selected, result, bet);
        return selected == result ? (long) bet * result.getMultiplier() : 0;
    }
}
