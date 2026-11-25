package ru.study.chain;

public final class Player {
    private long balance = 100;

    public long getBalance() { return balance; }

    public void addCoins(long amount) {
        if (amount < 0) throw new IllegalArgumentException("Количество монет не может быть отрицательным.");
        balance = Math.addExact(balance, amount);
    }

    public void pay(int bet) {
        if (bet <= 0 || bet > balance) throw new IllegalArgumentException("Недостаточно монет для ставки.");
        balance -= bet;
    }
}
