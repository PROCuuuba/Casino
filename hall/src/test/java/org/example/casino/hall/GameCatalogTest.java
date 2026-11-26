package org.example.casino.hall;

import org.example.casino.GameMachine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameCatalogTest {
    @Test
    void discoversAllIntegratedGames() {
        var machines = GameCatalog.discover().machines();
        var ids = machines.stream().map(GameMachine::id).collect(Collectors.toSet());
        assertEquals(Set.of("barkalov.gamble", "barkalov.lotto536", "barkalov.orbita", "chernixa.ludka", "korentsov.pixel",
                "larin.threerows", "putilin.pouches", "varganov.bags"),
                ids.stream().filter(id -> !id.equals("test.probe")).collect(Collectors.toSet()));
        assertEquals(machines.size(), ids.size());
        assertTrue(machines.stream().noneMatch(machine -> machine.title().isBlank()));
    }

    @Test
    void discoversAnExtraProviderFromAnIndependentTestModule() {
        var extension = GameCatalog.discover().machines().stream()
                .filter(machine -> machine.id().equals("test.probe")).findFirst().orElseThrow();
        assertEquals("org.example.casino.probe", extension.getClass().getModule().getName());
    }

    @Test
    void rejectsDuplicateIdsRatherThanSilentlyReplacingAGame() {
        GameMachine machine = GameCatalog.discover().machines().getFirst();
        var failure = assertThrows(IllegalArgumentException.class, () -> new GameCatalog(List.of(machine, machine)));
        assertTrue(failure.getMessage().contains(machine.id()));
    }
}
