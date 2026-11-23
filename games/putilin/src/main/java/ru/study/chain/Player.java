package ru.study.chain;

import org.example.casino.BalanceManager;

public final class Player {
    private final String name;
    private Integer count;
    private Integer winnings;

    public Player(String name, Integer number) {
        this.name = name;
        count = 0;
        winnings = 0;
    }

    public boolean pay(Integer amount) {
        if (BalanceManager.getInstance().spendCoins(amount)) {
            count++;
            return true;
        }
        return false;
    }

    public Integer getCount() { return count; }
    public Integer getNumber() { return BalanceManager.getInstance().getCoins(); }
    public String getName() { return name; }
    public Integer getWinnings() { return winnings; }

    public void addNumber(Integer amount) { BalanceManager.getInstance().addCoins(amount); }
    public void addWinnings(Integer amount) { winnings += amount; }

    public void collectWinnings() {
        BalanceManager.getInstance().addCoins(winnings);
        winnings = 0;
    }
}
