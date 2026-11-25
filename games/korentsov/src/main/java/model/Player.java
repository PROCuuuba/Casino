package model;

import org.example.casino.BalanceManager;

// Хранит данные игрока: имя, баланс монеток и число сыгранных раундов
public class Player {
    private String name;
    private Integer count;

    // Создает нового игрока с начальным запасом монеток
    public Player(String name, Integer number) {
        this.name = name;
        this.count = 0;
    }

    // Списывает ставку за игру и увеличивает счетчик сыгранных партий
    public boolean pay(Integer cost) {
        if (BalanceManager.getInstance().spendCoins(cost)) {
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
        return BalanceManager.getInstance().getCoins();
    }

    // Пополняет баланс игрока при выигрыше или забросе монетки
    public void addNumber(Integer coins) {
        BalanceManager.getInstance().addCoins(coins);
    }

    // Возвращает имя игрока
    public String getName() {
        return name;
    }
}
