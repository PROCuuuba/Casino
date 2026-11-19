package chain;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import model.Player;

import java.time.LocalDateTime;
import java.util.Optional;

// Обработчик бесплатного шанса (2-е звено): дополнительно перемешивает запрос с адресом объекта и датой/временем
public class ChanceHandler extends Handler {

    public ChanceHandler(Handler processor, Player player) {
        super(processor, player);
    }

    public ChanceHandler(Handler processor, Player player, ResultDisplay display) {
        super(processor, player, display);
    }

    // Повторно рандомизирует запрос от предыдущего звена и решает, дать ли бесплатный шанс
    @Override
    public boolean process(Integer request) {
        int objectHash = System.identityHashCode(new Object());
        LocalDateTime now = LocalDateTime.now();
        int dateTimeMix = now.getDayOfYear() * 100 + now.getSecond() + (now.getNano() / 100);
        int roll = (int) (Math.abs((long) request * 31 + objectHash + dateTimeMix) % 100);

        int gamesPlayed = player.getCount();
        int coinsLeft = player.getNumber();

        String strategyName;

        if (gamesPlayed <= 2) {
            strategyName = "Дать надежду (новичок)";
        } else if (coinsLeft == 0) {
            strategyName = "Удержать (0 монет)";
            if (roll >= 60) {
                return super.process(roll);
            }
        } else if (gamesPlayed >= 6) {
            strategyName = "Обобрать (азартный игрок)";
            if (roll < 70 || roll >= 90) {
                return super.process(roll);
            }
        } else {
            strategyName = "Стандартный баланс";
            int currentType = 1 + (roll % ActionChain.NUMHANDLER);
            if (currentType != ActionChain.CHANCE) {
                return super.process(roll);
            }
        }

        player.addNumber(1);

        updateScreen(
                "[ BAR ] [ BAR ] [ ? ]",
                "ШАНС! БЕСПЛАТНЫЙ ХОД",
                strategyName,
                "Монета возвращена. Баланс: " + player.getNumber()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы проиграли!");
        alert.setHeaderText("Но судьба дает Вам шанс сыграть еще раз бесплатно!");
        alert.setContentText("Стратегия: " + strategyName
                + "\nМонетка возвращена на счет. Баланс: " + player.getNumber() + " монет.");

        ButtonType useChance = new ButtonType("Продолжить бесплатно", ButtonBar.ButtonData.YES);
        ButtonType stop = new ButtonType("Отдохнуть", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(useChance, stop);

        Optional<ButtonType> option = alert.showAndWait();
        return option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
}
