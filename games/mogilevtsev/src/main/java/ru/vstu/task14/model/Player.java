package ru.vstu.task14.model;

/**
 * Игрок: имя, монеты на счету и число сделанных ставок.
 */
public class Player {

    private final String name;
    private int count;   // ставок сделано
    private int number;  // монеты на счету

    public Player(String name, int number) {
        this.name = name;
        this.number = number;
        count = 0;
    }

    /** Списывает ставку, если на счету хватает денег. */
    public boolean pay(int number) {
        if (number <= this.number) {
            this.number -= number;
            count++;
            return true;
        }
        return false;
    }

    public void addNumber(int number) {
        this.number += number;
    }

    public int getCount() {
        return count;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ": на счете " + number + " монет, ставок сделано " + count;
    }
}
