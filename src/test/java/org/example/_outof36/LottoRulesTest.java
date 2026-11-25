package org.example._outof36;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LottoRulesTest {
    @Test
    void drawsFiveDistinctNumbersInRange() {
        Random random = new Random(36);

        for (int round = 0; round < 100; round++) {
            List<Integer> drawn = LottoRules.draw(random);
            assertEquals(5, drawn.size());
            assertEquals(5, Set.copyOf(drawn).size());
            assertTrue(drawn.stream().allMatch(number -> number >= 1 && number <= 36));
        }
    }

    @Test
    void countsEveryPrizeTierRegardlessOfOrder() {
        Set<Integer> ticket = Set.of(1, 2, 3, 4, 5);
        int[] prizes = {0, 0, 5, 20, 100, 1_000};

        for (int count = 0; count < prizes.length; count++) {
            List<Integer> matches = IntStream.rangeClosed(1, count).boxed().toList().reversed();
            List<Integer> otherNumbers = IntStream.range(31, 36 - count).boxed().toList();
            List<Integer> drawn = Stream.concat(otherNumbers.stream(), matches.stream()).toList();
            LottoRules.Result result = LottoRules.evaluate(ticket, drawn);

            assertEquals(drawn, result.drawn());
            assertEquals(matches, result.matches());
            assertEquals(prizes[count], result.prize());
        }
    }

    @Test
    void rejectsInvalidTicketsAndDraws() {
        Set<Integer> ticket = Set.of(1, 2, 3, 4, 5);
        List<Integer> drawn = List.of(32, 33, 34, 35, 36);

        assertThrows(IllegalArgumentException.class, () -> LottoRules.evaluate(Set.of(1, 2), drawn));
        assertThrows(IllegalArgumentException.class, () -> LottoRules.evaluate(Set.of(0, 1, 2, 3, 4), drawn));
        assertThrows(IllegalArgumentException.class, () -> LottoRules.evaluate(ticket, List.of(1, 2, 3, 4, 4)));
        assertThrows(IllegalArgumentException.class, () -> LottoRules.evaluate(ticket, List.of(1, 2, 3, 4, 37)));
    }
}
