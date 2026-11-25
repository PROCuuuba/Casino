package org.example._outof36;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

final class LottoRules {
    static final int NUMBER_COUNT = 36;
    static final int PICKS = 5;
    static final int STAKE = 2;

    private LottoRules() {
    }

    static List<Integer> draw(RandomGenerator random) {
        var numbers = new ArrayList<>(IntStream.rangeClosed(1, NUMBER_COUNT).boxed().toList());
        for (int index = 0; index < PICKS; index++) {
            Collections.swap(numbers, index, random.nextInt(index, numbers.size()));
        }
        return List.copyOf(numbers.subList(0, PICKS));
    }

    static Result evaluate(Set<Integer> ticket, List<Integer> drawn) {
        if (ticket.size() != PICKS || drawn.size() != PICKS || new HashSet<>(drawn).size() != PICKS
                || !ticket.stream().allMatch(LottoRules::inRange)
                || !drawn.stream().allMatch(LottoRules::inRange)) {
            throw new IllegalArgumentException("Билет и тираж должны содержать по пять разных чисел от 1 до 36");
        }

        List<Integer> matches = drawn.stream().filter(ticket::contains).toList();
        return new Result(List.copyOf(drawn), matches, prizeFor(matches.size()));
    }

    static int prizeFor(int matches) {
        return switch (matches) {
            case 0, 1 -> 0;
            case 2 -> 5;
            case 3 -> 20;
            case 4 -> 100;
            case 5 -> 1_000;
            default -> throw new IllegalArgumentException("Число совпадений должно быть от 0 до 5");
        };
    }

    private static boolean inRange(int number) {
        return number >= 1 && number <= NUMBER_COUNT;
    }

    record Result(List<Integer> drawn, List<Integer> matches, int prize) {
    }
}
