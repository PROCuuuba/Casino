module org.example.casino.korentsov {
    requires org.example.casino.api;
    opens ui to javafx.graphics;
    provides org.example.casino.GameMachine with ui.PixelMachine;
}
