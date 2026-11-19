package games.threebags;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class PositiveHandler extends Handler {
    private Player player;

    public PositiveHandler(Handler processor, Player player) {
        super(processor);
        this.player = player;
    }

    @Override
    public boolean process(Integer request) {
        if (request != ActionChain.SUCCESS) return super.process(request);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы выиграли!");
        alert.setHeaderText("В мешочке оказались монеты! Ваш выигрыш: 3 монеты.");

        ButtonType take = new ButtonType("Забрать выигрыш", ButtonBar.ButtonData.YES);
        ButtonType play = new ButtonType("Сыграть на выигранное", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(take, play);

        Optional<ButtonType> option = alert.showAndWait();

        player.addNumber(3);

        return option.isPresent() && option.get().getButtonData() != ButtonBar.ButtonData.YES;
    }
}
