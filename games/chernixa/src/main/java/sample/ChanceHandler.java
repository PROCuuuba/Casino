package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.time.LocalTime;
import java.util.Optional;

public class ChanceHandler extends Handler {

    public static final int CHANCE = 2;
    public static boolean isBesplatno = false;

    public ChanceHandler(Handler processor) {
        super(processor);
    }

    @Override
    public boolean process(Integer request) {
        int sekunda = LocalTime.now().getSecond() % 4;
        int tip = 1 + sekunda;

        if (tip != CHANCE) {
            return super.process(request);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы проиграли!");
        alert.setHeaderText("Но судьба дает Вам шанс сыграть еще раз бесплатно!");

        ButtonType prinyat = new ButtonType("Принять шанс", ButtonBar.ButtonData.YES);
        ButtonType otkaz = new ButtonType("Не надо, хватит", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(prinyat, otkaz);
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get().getButtonData() == ButtonBar.ButtonData.YES) {
            isBesplatno = true;
            return true;
        } else {
            isBesplatno = false;
            return false;
        }
    }
}
