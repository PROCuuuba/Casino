module org.example.casino.hall {
    requires org.example.casino.api;
    requires javafx.fxml;
    requires javafx.media;
    uses org.example.casino.GameMachine;
    opens org.example.casino.hall to javafx.fxml;
    exports org.example.casino.hall to javafx.graphics;
}
