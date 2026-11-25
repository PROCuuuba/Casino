module org.example.casino.probe {
    requires org.example.casino.api;
    provides org.example.casino.GameMachine with testing.probe.FailingMachine;
}
