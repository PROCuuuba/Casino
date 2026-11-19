package chain;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import model.Player;

import java.time.LocalDateTime;
import java.util.Optional;

// Обработчик проигрыша (3-е звено): завершает цепочку с финальным перемешиванием через память и наносекунды
public class NegativeHandler extends Handler {

    public NegativeHandler(Handler processor, Player player) {
        super(processor, player);
    }

    public NegativeHandler(Handler processor, Player player, ResultDisplay display) {
        super(processor, player, display);
    }

    // В третий раз перемешивает значение запроса, определяет стратегию и обрабатывает проигрыш
    @Override
    public boolean process(Integer request) {
        long usedRam = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 512;
        int timePart = LocalDateTime.now().getNano() % 10000;
        int roll = (int) (Math.abs((long) request * 17 + usedRam + timePart) % 100);

        int gamesPlayed = player.getCount();
        int coinsLeft = player.getNumber();

        String strategyName;
        if (gamesPlayed <= 2) {
            strategyName = "Дать надежду (новичок)";
        } else if (coinsLeft == 0) {
            strategyName = "Удержать (0 монет)";
        } else if (gamesPlayed >= 6) {
            strategyName = "Обобрать (азартный игрок)";
        } else {
            strategyName = "Стандартный баланс";
        }

        String reels = (roll % 2 == 0) ? "[ X ]  [ X ]  [ X ]" : "[ 0 ]  [ X ]  [ 0 ]";
        String lossHeader = (roll < 50)
                ? "Монетка потеряна, но всегда можно отыграться!"
                : "Не повезло в этот раз, попробуйте снова!";

        updateScreen(
                reels,
                "ПРОИГРЫШ!",
                strategyName,
                "Монета потеряна. Баланс: " + player.getNumber()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы проиграли!");
        alert.setHeaderText(lossHeader);
        alert.setContentText("Стратегия: " + strategyName
                + "\nТекущий баланс: " + player.getNumber() + " монет.");

        ButtonType replay = new ButtonType("Продолжить играть", ButtonBar.ButtonData.YES);
        ButtonType vacation = new ButtonType("Отдохнуть", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(replay, vacation);

        Optional<ButtonType> option = alert.showAndWait();
        return option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
}
