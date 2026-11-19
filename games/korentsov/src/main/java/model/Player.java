package model;

// Хранит данные игрока: имя, баланс монеток и число сыгранных раундов
public class Player {
    private String name;
    private Integer count;
    private Integer number;

    // Создает нового игрока с начальным запасом монеток
    public Player(String name, Integer number) {
        this.name = name;
        this.number = number;
        this.count = 0;
    }

    // Списывает ставку за игру и увеличивает счетчик сыгранных партий
    public boolean pay(Integer cost) {
        if (cost <= this.number) {
            this.number -= cost;
            this.count++;
            return true;
        }
        return false;
    }

    // Возвращает общее количество сыгранных раундов
    public Integer getCount() {
        return count;
    }

    // Возвращает текущий баланс монеток
    public Integer getNumber() {
        return number;
    }

    // Пополняет баланс игрока при выигрыше или забросе монетки
    public void addNumber(Integer coins) {
        this.number += coins;
    }

    // Возвращает имя игрока
    public String getName() {
        return name;
    }
}