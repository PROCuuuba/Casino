package ru.vstu.task14.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import ru.vstu.task14.chain.ActionChain;
import ru.vstu.task14.model.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final int START_COINS = 10;

    @FXML
    ImageView view;
    @FXML
    Label infoLabel;
    @FXML
    Button payButton;
    @FXML
    Button startButton;

    Player player1;
    ActionChain action = null; // розыгрыш; null — игра не запущена

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        player1 = new Player("Игрок", START_COINS);
        view.setImage(new Image(getClass().getResourceAsStream("монетка.jpg")));
        updateInfo();

        // клик по картинке — очередной розыгрыш
        view.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (action == null) return; // игра не запущена — клик не работает
            if (action.play()) {
                if (!init()) return; // игрок продолжает — списываем ставку
            } else {
                action = null; // игрок ушёл
            }
            updateInfo();
        });
    }

    /** Кнопка "Заправить монетку": +1 монета на счёт. */
    @FXML
    public void onPay(ActionEvent actionEvent) {
        player1.addNumber(1);
        updateInfo();
    }

    /** Кнопка "Начать игру": ставка списана, мешочки на экране, цепочка готова. */
    @FXML
    public void onStart(ActionEvent actionEvent) {
        if (!init()) return;
        view.setImage(new Image(getClass().getResourceAsStream("мешочки.png")));
        action = new ActionChain(player1);
        updateInfo();
    }

    /** Списывает ставку. Денег нет — игра закрывается. */
    private boolean init() {
        if (!player1.pay(1)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Средств на счете недостаточно, еще монетку плисс!");
            alert.show();
            action = null;
            view.setImage(new Image(getClass().getResourceAsStream("монетка.jpg")));
            updateInfo();
            return false;
        }
        updateInfo();
        return true;
    }

    private void updateInfo() {
        infoLabel.setText(player1.toString());
    }
}
