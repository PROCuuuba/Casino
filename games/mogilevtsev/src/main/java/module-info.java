module org.example.casino.mogilevtsev {
    requires org.example.casino.api;
    requires javafx.fxml;

    opens org.example._outof36 to javafx.fxml, javafx.graphics;
    provides org.example.casino.GameMachine with org.example._outof36.LottoMachine;
}
