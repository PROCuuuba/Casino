package org.example.casino;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

public final class BalanceManager {
    private static final BalanceManager instance = new BalanceManager();
    private final List<IntConsumer> listeners = new CopyOnWriteArrayList<>();
    private int coins;

    private BalanceManager() {
    }

    public static BalanceManager getInstance() {
        return instance;
    }

    public synchronized int getCoins() {
        return coins;
    }

    public synchronized void addCoins(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Количество монет должно быть положительным");
        }
        coins += amount;
        notifyListeners();
    }

    public synchronized boolean spendCoins(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ставка должна быть положительной");
        }
        if (coins < amount) {
            return false;
        }
        coins -= amount;
        notifyListeners();
        return true;
    }

    public void addListener(IntConsumer listener) {
        listeners.add(listener);
        listener.accept(getCoins());
    }

    public void removeListener(IntConsumer listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        int currentCoins = coins;
        for (IntConsumer listener : listeners) {
            listener.accept(currentCoins);
        }
    }
}
