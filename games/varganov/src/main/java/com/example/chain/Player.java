package com.example.chain;

import org.example.casino.BalanceManager;

public class Player {
    private final String name;
    private int gamesCount;

    public Player(String name, int coins) {
        this.name = name;
    }

    // Списывает монету за обычный ход.
    public boolean payForGame() {
        if (!BalanceManager.getInstance().spendCoins(1)) return false;
        gamesCount++;
        return true;
    }

    public void addCoins(int count) {
        BalanceManager.getInstance().addCoins(count);
    }

    public String getName() {
        return name;
    }

    public int getCoins() {
        return BalanceManager.getInstance().getCoins();
    }

    public int getGamesCount() {
        return gamesCount;
    }
}
