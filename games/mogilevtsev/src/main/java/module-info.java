module org.example.casino.mogilevtsev {
    requires org.example.casino.api;
    requires javafx.fxml;
    opens ru.vstu.task14.ui to javafx.fxml, javafx.graphics;
    provides org.example.casino.GameMachine with ru.vstu.task14.ui.ReelsMachine;
}
