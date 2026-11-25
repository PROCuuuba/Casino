module org.example.casino.putilin {
    requires org.example.casino.api;
    opens ru.study.chain to javafx.graphics;
    provides org.example.casino.GameMachine with ru.study.chain.PouchMachine;
}
