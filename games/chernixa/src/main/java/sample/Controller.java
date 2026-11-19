package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label title;

    @FXML
    private Label labelMonet;

    @FXML
    private Label labelPopitok;

    @FXML
    private ImageView mesh1;

    @FXML
    private ImageView mesh2;

    @FXML
    private ImageView mesh3;

    @FXML
    private ImageView view;

    @FXML
    private Button btnPay;

    @FXML
    private Button btnStart;

    @FXML
    private Label status;

    Player player1;
    ActionChain action = null;

    Image kartinkaMoneta;
    Image kartinkaMeshki;

    @FXML
    void initialize() {
        kartinkaMoneta = new Image(getClass().getResourceAsStream("images/monetka.jpg"));
        kartinkaMeshki = new Image(getClass().getResourceAsStream("images/meshochki.png"));

        view.setImage(kartinkaMoneta);

        player1 = new Player("Vasya", 0);
        obnovitLabel();

        mesh1.setImage(kartinkaMeshki);
        mesh2.setImage(kartinkaMeshki);
        mesh3.setImage(kartinkaMeshki);

        mesh1.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> klikPoMeshku());
        mesh2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> klikPoMeshku());
        mesh3.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> klikPoMeshku());

        view.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> klikPoMeshku());
    }

    private void klikPoMeshku() {
        if (action == null) {
            status.setText("Сначала нажми Start!");
            return;
        }

        boolean prodolzhat = action.process();

        int vyigral = action.getVyigryshSumma();
        if (vyigral > 0) {
            player1.addNumber(vyigral);
            PositiveHandler.vyigryshSumma = 0;
        }

        if (prodolzhat) {
            if (ChanceHandler.isBesplatno) {
                ChanceHandler.isBesplatno = false;
            } else {
                if (!init()) {
                    action = null;
                }
            }
        } else {
            action = null;
            view.setImage(kartinkaMoneta);
            title.setText("Игра окончена! Попыток было: " + player1.getCount());
            status.setText("Можешь кинуть еще монеток и сыграть снова");
        }
        obnovitLabel();
    }

    public void onPay(ActionEvent actionEvent) {
        player1.addNumber(1);
        obnovitLabel();
        status.setText("Добавлена монетка!");
    }

    public void onStart(ActionEvent actionEvent) {
        if (!init()) return;

        view.setImage(kartinkaMeshki);
        title.setText("Выбери мешочек!");
        status.setText("Игра идет...");
        action = new ActionChain();
    }

    public boolean init() {
        if (!player1.pay(1)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Средств на счете недостаточно, еще монетку плисс!");
            alert.show();
            action = null;
            view.setImage(kartinkaMoneta);
            title.setText("Кинь монетку и начни сначала");
            return false;
        } else {
            obnovitLabel();
            return true;
        }
    }

    private void obnovitLabel() {
        labelMonet.setText("Монеток у игрока: " + player1.getNumber());
        labelPopitok.setText("Попыток: " + player1.getCount());
    }
}
