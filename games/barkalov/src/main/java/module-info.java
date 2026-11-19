module org.example.casino.barkalov {
    requires org.example.casino.api;
    requires javafx.fxml;
    requires java.desktop;
    opens org.example.gamble to javafx.fxml, javafx.graphics;
    provides org.example.casino.GameMachine with org.example.gamble.GambleMachine;
}
