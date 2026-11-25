package ru.study.chain;

public final class GreenHandler extends Handler {
    public GreenHandler(Handler next) {
        super(next);
    }

    @Override public long process(BetColor selected, BetColor result, int bet) {
        if (result != BetColor.GREEN) return super.process(selected, result, bet);
        return selected == result ? (long) bet * result.getMultiplier() : 0;
    }
}
