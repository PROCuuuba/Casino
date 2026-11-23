package ru.vstu.task14.model;

import org.example.casino.BalanceManager;

/**
 * Игрок: имя, монеты на счету и число сделанных ставок.
 */
public class Player {

    private final String name;
    private int count;   // ставок сделано

    public Player(String name, int number) {
        this.name = name;
        count = 0;
    }

    /** Списывает ставку, если на счету хватает денег. */
    public boolean pay(int number) {
        if (BalanceManager.getInstance().spendCoins(number)) {
            count++;
            return true;
        }
        return false;
    }

    public void addNumber(int number) {
        BalanceManager.getInstance().addCoins(number);
    }

    public int getCount() {
        return count;
    }

    public int getNumber() {
        return BalanceManager.getInstance().getCoins();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": на счете " + getNumber() + " монет, ставок сделано " + count;
    }
}
