package chain;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import model.Player;

import java.time.LocalDateTime;
import java.util.Optional;

// Обработчик выигрыша (1-е звено): смешивает входной запрос с памятью и временем и решает судьбу выигрыша
public class PositiveHandler extends Handler {
    private static final int WIN_REWARD = 2;

    public PositiveHandler(Handler processor, Player player) {
        super(processor, player);
    }

    public PositiveHandler(Handler processor, Player player, ResultDisplay display) {
        super(processor, player, display);
    }

    // Рандомизирует запрос через свободную память JVM и микросекунды, проверяя стратегию по пути
    @Override
    public boolean process(Integer request) {
        long freeRamKb = Runtime.getRuntime().freeMemory() / 1024;
        int timeMicros = LocalDateTime.now().getNano() / 1000;
        int roll = (int) (Math.abs((long) request + freeRamKb + timeMicros) % 100);

        int gamesPlayed = player.getCount();
        int coinsLeft = player.getNumber();

        String strategyName;

        if (gamesPlayed <= 2) {
            strategyName = "Дать надежду (новичок)";
            if (roll >= 65) {
                return super.process(roll);
            }
        } else if (coinsLeft == 0) {
            // При 0 монет передаем измененный запрос дальше для проверки удержания в ChanceHandler
            return super.process(roll);
        } else if (gamesPlayed >= 6) {
            strategyName = "Обобрать (азартный игрок)";
            if (roll < 90) {
                return super.process(roll);
            }
        } else {
            strategyName = "Стандартный баланс";
            int currentType = 1 + (roll % ActionChain.NUMHANDLER);
            if (currentType != ActionChain.SUCCESS) {
                return super.process(roll);
            }
        }

        player.addNumber(WIN_REWARD);

        updateScreen(
                "[ 7 ]  [ 7 ]  [ 7 ]",
                "ВЫИГРЫШ! +" + WIN_REWARD + " МОНЕТЫ",
                strategyName,
                "Баланс: " + player.getNumber() + " монет"
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы выиграли!");
        alert.setHeaderText("Поздравляем! На экране выпала комбинация 7-7-7 (+" + WIN_REWARD + " монеты)!");
        alert.setContentText("Стратегия: " + strategyName + "\nВаш баланс: " + player.getNumber() + " монет.");

        ButtonType continuePlay = new ButtonType("Играть на выигранное", ButtonBar.ButtonData.YES);
        ButtonType takeMoney = new ButtonType("Забрать выигрыш", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(continuePlay, takeMoney);

        Optional<ButtonType> option = alert.showAndWait();
        return option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
}
