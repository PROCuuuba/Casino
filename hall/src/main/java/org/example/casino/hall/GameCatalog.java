package org.example.casino.hall;

import org.example.casino.GameMachine;

import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeMap;

final class GameCatalog {
    private final List<GameMachine> machines;

    GameCatalog(Iterable<GameMachine> providers) {
        var byId = new TreeMap<String, GameMachine>();
        for (GameMachine machine : providers) {
            String id = machine.id();
            if (id == null || id.isBlank() || machine.title() == null || machine.title().isBlank()) {
                throw new IllegalArgumentException("У автомата должны быть идентификатор и название");
            }
            if (byId.putIfAbsent(id, machine) != null) {
                throw new IllegalArgumentException("Повтор идентификатора: " + id);
            }
        }
        machines = List.copyOf(byId.values());
    }

    static GameCatalog discover() {
        return new GameCatalog(ServiceLoader.load(GameMachine.class));
    }

    List<GameMachine> machines() {
        return machines;
    }
}
