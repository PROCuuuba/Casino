package org.example.casino;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import static org.junit.jupiter.api.Assertions.*;

class BalanceManagerTest {

    private BalanceManager manager;

    @BeforeEach
    void resetBalance() {
        manager = BalanceManager.getInstance();
        int current = manager.getCoins();
        if (current > 0) {
            assertTrue(manager.spendCoins(current));
        }
        assertEquals(0, manager.getCoins());
    }

    @Test
    void t2_1_spendsCoinsWhenBalanceIsEnough() {
        manager.addCoins(10);

        boolean spent = manager.spendCoins(3);

        assertTrue(spent);
        assertEquals(7, manager.getCoins());
    }

    @Test
    void t2_2_rejectsSpendWhenNotEnoughCoinsAndKeepsBalance() {
        manager.addCoins(2);

        boolean spent = manager.spendCoins(3);

        assertFalse(spent);
        assertEquals(2, manager.getCoins());
    }

    @Test
    void t2_3_addsCoinsToBalance() {
        manager.addCoins(7);
        manager.addCoins(5);

        assertEquals(12, manager.getCoins());
    }

    @Test
    void t2_4_rejectsZeroAndNegativeAmounts() {
        manager.addCoins(10);

        assertThrows(IllegalArgumentException.class, () -> manager.addCoins(0));
        assertThrows(IllegalArgumentException.class, () -> manager.addCoins(-1));
        assertThrows(IllegalArgumentException.class, () -> manager.spendCoins(0));
        assertThrows(IllegalArgumentException.class, () -> manager.spendCoins(-4));

        assertEquals(10, manager.getCoins());
    }

    @Test
    void notifiesListenersAndRespectsRemoval() {
        List<Integer> seen = new ArrayList<>();
        IntConsumer listener = seen::add;

        manager.addListener(listener);
        manager.addCoins(5);
        manager.spendCoins(2);
        manager.removeListener(listener);
        manager.addCoins(10);

        assertEquals(List.of(0, 5, 3), seen);
    }

    @Test
    void singletonReturnsSameInstance() {
        AtomicInteger first = new AtomicInteger(BalanceManager.getInstance().getCoins());
        assertEquals(first.get(), manager.getCoins());
        assertSame(manager, BalanceManager.getInstance());
    }
}
