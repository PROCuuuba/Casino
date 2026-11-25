package games.threebags;

import javafx.scene.control.Alert;

public class ChanceHandler extends Handler {
    private Player player;

    public ChanceHandler(Handler processor, Player player) {
        super(processor);
        this.player = player;
    }

    @Override
    public boolean process(Integer request) {
        if (request != ActionChain.CHANCE) return super.process(request);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Вы проиграли!");
        alert.setHeaderText("Но судьба дает Вам шанс сыграть еще раз бесплатно!");
        alert.showAndWait();

        player.addNumber(1);
        return true;
    }
}