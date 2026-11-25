package ru.study.chain;

import org.example.casino.BalanceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RouletteGameTest {
    private final BalanceManager balance = BalanceManager.getInstance();

    @BeforeEach
    void resetBalance() {
        if (balance.getCoins() > 0) balance.spendCoins(balance.getCoins());
        balance.addCoins(100);
    }

    @Test
    void hasFourRedFourBlackAndOneGreenSector() {
        RouletteGame game = new RouletteGame();
        assertEquals(9, game.getSectorCount());
        for (int i = 0; i < 8; i++) {
            assertEquals(i % 2 == 0 ? BetColor.RED : BetColor.BLACK, game.getSectorColor(i));
        }
        assertEquals(BetColor.GREEN, game.getSectorColor(8));
    }

    @Test
    void paysOnlyForTheSelectedColorOnEverySector() {
        for (int sector = 0; sector < 9; sector++) {
            for (BetColor selected : BetColor.values()) {
                resetBalance();
                RouletteGame game = gameLandingOn(sector);
                var result = game.play(selected, 10);
                BetColor landed = sector == 8 ? BetColor.GREEN : sector % 2 == 0 ? BetColor.RED : BetColor.BLACK;
                long payout = selected == landed ? (landed == BetColor.GREEN ? 50 : 20) : 0;
                assertEquals(sector, result.sector());
                assertEquals(landed, result.color());
                assertEquals(payout, result.payout());
                assertEquals(90 + payout, balance.getCoins());
            }
        }
    }

    @Test
    void rejectsInvalidBetsWithoutSpendingCoins() {
        RouletteGame game = gameLandingOn(8);
        for (int bet : new int[]{-1, 0, 101, 10001, Integer.MAX_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> game.play(BetColor.GREEN, bet));
            assertEquals(100, balance.getCoins());
        }
        assertThrows(NullPointerException.class, () -> game.play(null, 10));
        assertEquals(100, balance.getCoins());
    }

    @Test
    void usesTheHallBalanceAcrossGameInstancesWithoutFreeStartingCoins() {
        new RouletteGame();
        assertEquals(100, balance.getCoins());
        gameLandingOn(8).play(BetColor.GREEN, 25);
        assertEquals(200, balance.getCoins());
        RouletteGame second = gameLandingOn(1);
        assertEquals(200, second.getPlayer().getBalance());
        second.play(BetColor.RED, 50);
        assertEquals(150, balance.getCoins());
        balance.spendCoins(5);
        assertEquals(145, second.getPlayer().getBalance());
        second.getPlayer().addCoins(10);
        assertEquals(155, balance.getCoins());
    }

    @Test
    void readingAResultDoesNotPayItAgain() {
        var result = gameLandingOn(8).play(BetColor.GREEN, 10);
        assertEquals(140, balance.getCoins());
        assertEquals(50, result.payout());
        assertEquals(50, result.payout());
        assertEquals(140, balance.getCoins());
    }

    @Test
    void acceptsTheMaximumBet() {
        balance.addCoins(9900);
        assertEquals(50000, gameLandingOn(8).play(BetColor.GREEN, 10000).payout());
        assertEquals(50000, balance.getCoins());
    }

    private RouletteGame gameLandingOn(int sector) {
        return new RouletteGame(new Random() {
            @Override public int nextInt(int bound) {
                assertEquals(9, bound);
                return sector;
            }
        });
    }
}
