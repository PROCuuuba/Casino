module org.example.casino.chernixa {
    requires org.example.casino.api;
    requires javafx.fxml;
    opens sample to javafx.fxml, javafx.graphics;
    provides org.example.casino.GameMachine with sample.LudkaMachine;
}
