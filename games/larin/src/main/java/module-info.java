module org.example.casino.larin {
    requires org.example.casino.api;

    provides org.example.casino.GameMachine with games.threeinrow.ThreeInRowAdapter;
}
