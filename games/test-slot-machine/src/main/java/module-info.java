module org.example.casino.orbita {
    requires org.example.casino.api;

    exports org.example.testslotmachine to javafx.graphics;

    provides org.example.casino.GameMachine with org.example.testslotmachine.OrbitaMachine;
}
