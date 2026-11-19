package games.threebags;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class GameController {
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;
    @FXML private HBox bagsBox;
    private Runnable onReturnToCatalog;
    private Player player = new Player("Игрок", 0);
    private ActionChain action = null;
    Runnable setOnReturnToCatalog(Runnable onReturnToCatalog){
        this.onReturnToCatalog = onReturnToCatalog;
        return onReturnToCatalog;
    }

    @FXML
    public void initialize() {
        showBags(false);
        updateUI();
    }

    @FXML
    public void onPay() {
        player.addNumber(1);
        updateUI();
        statusLabel.setText("Монета добавлена. Нажмите 'Начать игру'");
    }

    @FXML
    public void onStart() {
        if (!checkAndPay()) return;
        showBags(true);
        action = new ActionChain(player);
        statusLabel.setText("Выберите одну из трех коробок!");
    }

    @FXML
    public void onBagClicked() {
        if (action == null) return;
        showBags(false);

        if (action.process()) {
            if (checkAndPay()) {
                showBags(true);
                statusLabel.setText("Выберите мешочек!");
            }
        } else {
            action = null;
            statusLabel.setText("Игра окончена. Вставьте монету для новой игры.");
            if (onReturnToCatalog != null){
                onReturnToCatalog.run();
            }
        }
    }

    private boolean checkAndPay() {
        if (!player.pay(1)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Средств на счете недостаточно!");
            alert.show();
            action = null;
            showBags(false);
            statusLabel.setText("Недостаточно средств.");
            return false;
        }
        updateUI();
        return true;
    }

    private void updateUI() {
        balanceLabel.setText("Баланс: " + player.getNumber() + " монет | Ходов: " + player.getCount());
    }

    private void showBags(boolean show) {
        bagsBox.setVisible(show);
        bagsBox.setManaged(show);
    }
}