package ru.study.chain;

import org.example.casino.BalanceManager;

public final class Player {
    private final BalanceManager balance = BalanceManager.getInstance();

    public int getBalance() { return balance.getCoins(); }

    public void addCoins(long amount) {
        if (amount < 0) throw new IllegalArgumentException("Количество монет не может быть отрицательным.");
        if (amount > 0) balance.addCoins(Math.toIntExact(amount));
    }

    public void pay(int bet) {
        if (bet <= 0) throw new IllegalArgumentException("Ставка должна быть положительной.");
        if (!balance.spendCoins(bet)) throw new IllegalArgumentException("Недостаточно монет для ставки.");
    }
}
