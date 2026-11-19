module org.example.casino {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.casino to javafx.fxml;
    exports org.example.casino;
}