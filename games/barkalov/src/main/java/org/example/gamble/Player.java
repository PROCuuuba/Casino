package org.example.gamble;

import org.example.casino.BalanceManager;

public class Player {
    private final String name;
    private Integer count;

    public Player(String name, Integer number) {
        this.name = name;
        count = 0;
    }

    public boolean pay(Integer number) {
        if (BalanceManager.getInstance().spendCoins(number)) {
            count++;
            return true;
        } else
            return false;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getNumber() {
        return BalanceManager.getInstance().getCoins();
    }

    public void addNumber(Integer number) {
        BalanceManager.getInstance().addCoins(number);
    }

    public Integer takeNumber() {
        return BalanceManager.getInstance().getCoins();
    }
}
