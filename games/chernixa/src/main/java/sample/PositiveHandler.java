package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.time.LocalTime;
import java.util.Optional;

public class PositiveHandler extends Handler {

    public static final int SUCCESS = 1;
    public static int vyigryshSumma = 0;

    public PositiveHandler(Handler processor) {
        super(processor);
    }

    @Override
    public boolean process(Integer request) {
        int sekunda = LocalTime.now().getSecond() % 4;
        int tip = 1 + sekunda;

        if (tip != SUCCESS) {
            return super.process(request);
        }

        vyigryshSumma = (int) (Math.random() * 4) + 1;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы выиграли!");
        alert.setHeaderText("Поздравляю! Вам доступно " + vyigryshSumma + " монеток!");
        alert.setContentText("Что будем делать?");

        ButtonType zabrat = new ButtonType("Забрать и уйти", ButtonBar.ButtonData.NO);
        ButtonType igratDalshe = new ButtonType("Играть дальше на выигранное", ButtonBar.ButtonData.YES);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(igratDalshe, zabrat);
        Optional<ButtonType> option = alert.showAndWait();

        return option.get().getButtonData() == ButtonBar.ButtonData.YES;
    }
}
