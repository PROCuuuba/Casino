package ru.study.chain;

import java.util.Objects;
import java.util.Random;

public final class RouletteGame {
    private static final BetColor[] SECTORS = {
            BetColor.RED, BetColor.BLACK, BetColor.RED, BetColor.BLACK,
            BetColor.RED, BetColor.BLACK, BetColor.RED, BetColor.BLACK, BetColor.GREEN
    };
    private final Player player = new Player();
    private final ActionChain chain = new ActionChain();
    private final Random random;

    public RouletteGame() {
        this(new Random());
    }

    RouletteGame(Random random) {
        this.random = Objects.requireNonNull(random);
    }

    public Player getPlayer() { return player; }
    public int getSectorCount() { return SECTORS.length; }
    public BetColor getSectorColor(int index) { return SECTORS[index]; }

    public SpinResult play(BetColor selected, int bet) {
        Objects.requireNonNull(selected);
        if (bet < 1 || bet > 10000) throw new IllegalArgumentException("Введите ставку от 1 до 10000 монет.");
        player.pay(bet);
        int sector = random.nextInt(SECTORS.length);
        BetColor color = SECTORS[sector];
        long payout = chain.process(selected, color, bet);
        player.addCoins(payout);
        return new SpinResult(sector, color, payout);
    }

    public record SpinResult(int sector, BetColor color, long payout) { }
}
