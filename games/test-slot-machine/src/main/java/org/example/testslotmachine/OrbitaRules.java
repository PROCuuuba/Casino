package org.example.testslotmachine;

import java.util.concurrent.ThreadLocalRandom;

final class OrbitaRules {
    static final int STAKE = 1;
    static final int PAIR_PAYOUT = 1;
    static final int TRIPLE_PAYOUT = 6;

    enum Symbol {
        PLANET("●", "Планета"),
        RING("◎", "Кольцо"),
        STAR("✦", "Звезда"),
        COMET("◆", "Комета");

        final String glyph;
        final String title;

        Symbol(String glyph, String title) {
            this.glyph = glyph;
            this.title = title;
        }
    }

    private static final Symbol[] SYMBOLS = Symbol.values();

    private OrbitaRules() {
    }

    static Symbol draw() {
        return SYMBOLS[ThreadLocalRandom.current().nextInt(SYMBOLS.length)];
    }

    static int payout(Symbol first, Symbol second, Symbol third) {
        if (first == second && second == third) {
            return TRIPLE_PAYOUT;
        }
        if (first == second || first == third || second == third) {
            return PAIR_PAYOUT;
        }
        return 0;
    }
}
