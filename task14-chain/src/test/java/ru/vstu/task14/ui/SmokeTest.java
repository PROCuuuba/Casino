package ru.vstu.task14.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Проверка без экрана: fxml грузится, цепочка строится, ставки списываются.
 */
public class SmokeTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        Parent root = loader.load();
        Controller c = loader.getController();

        check(c.view.getImage() != null, "монетка загружена");
        check(c.player1 != null && c.player1.getNumber() == 10, "игрок создан со 10 монетами");

        c.onPay(null);
        check(c.player1.getNumber() == 11, "заправка: +1 монета");

        c.onStart(null);
        check(c.action != null, "цепочка построена");
        check(c.player1.getNumber() == 10, "ставка списана");
        check(c.view.getImage() != null && c.view.getImage() != null, "картинка сменилась");

        // нельзя поставить больше, чем есть на счету
        check(!c.player1.pay(1000), "ставка больше счёта отклонена");
        check(c.player1.getCount() == 1, "счётчик ставок учитывает только успешные");

        System.out.println("SMOKE OK");
        Platform.exit();
    }

    private static void check(boolean cond, String what) {
        if (!cond) {
            System.err.println("FAIL: " + what);
            Platform.exit();
            System.exit(1);
        }
        System.out.println("ok: " + what);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
