module org.example.casino.larin {
    requires org.example.casino.api;
    requires javafx.fxml;
    opens games.threebags to javafx.fxml, javafx.graphics;
    provides org.example.casino.GameMachine with games.threebags.ThreeBagsAdapter;
}
