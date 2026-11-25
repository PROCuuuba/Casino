module org.example.casino.varganov {
    requires org.example.casino.api;
    opens com.example.chain to javafx.graphics;
    provides org.example.casino.GameMachine with com.example.gamehall.games.BagMachineAdapter;
}
