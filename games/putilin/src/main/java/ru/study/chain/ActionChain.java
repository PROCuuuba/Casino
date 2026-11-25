package ru.study.chain;

public final class ActionChain {
    private final Handler chain = new RedHandler(new BlackHandler(new GreenHandler(null)));

    public long process(BetColor selected, BetColor result, int bet) {
        return chain.process(selected, result, bet);
    }
}
