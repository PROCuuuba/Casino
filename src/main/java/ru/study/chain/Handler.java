package ru.study.chain;

public abstract class Handler {
    private final Handler next;

    protected Handler(Handler next) {
        this.next = next;
    }

    public long process(BetColor selected, BetColor result, int bet) {
        return next == null ? 0 : next.process(selected, result, bet);
    }
}
