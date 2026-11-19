package ru.study.chain;

public final class Player {
    private final String name;
    private Integer count;
    private Integer number;
    private Integer winnings;

    public Player(String name, Integer number) {
        this.name = name;
        this.number = number;
        count = 0;
        winnings = 0;
    }

    public boolean pay(Integer amount) {
        if (amount <= number) {
            number -= amount;
            count++;
            return true;
        }
        return false;
    }

    public Integer getCount() { return count; }
    public Integer getNumber() { return number; }
    public String getName() { return name; }
    public Integer getWinnings() { return winnings; }

    public void addNumber(Integer amount) { number += amount; }
    public void addWinnings(Integer amount) { winnings += amount; }

    public void collectWinnings() {
        number += winnings;
        winnings = 0;
    }
}
