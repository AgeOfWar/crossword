package io.github.ageofwar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CspCrosswordFiller implements CrosswordFiller {
    private final Dictionary dictionary;
    private final Random random;

    public CspCrosswordFiller(Dictionary dictionary, Random random) {
        this.dictionary = dictionary;
        this.random = random;
    }

    public CspCrosswordFiller(Dictionary dictionary) {
        this(dictionary, new Random());
    }

    @Override
    public Crossword fill(Crossword crossword) {
        var clone = crossword.copy();
        var fillPositions = crossword.getPositions(2);
        var heap = new ScoreHeap<Crossword.PositionDirectionLength>(fillPositions.size());
        var domains = new HashMap<Crossword.PositionDirectionLength, List<byte[]>>();
        for (var position : fillPositions) {
            var candidates = dictionary.fromPattern(crossword.get(position));
            domains.put(position, candidates);
            heap.set(position, candidates.size());
        }
        if (fill(clone, domains, heap)) {
            return clone;
        }
        return null;
    }

    private boolean fill(Crossword crossword, Map<Crossword.PositionDirectionLength, List<byte[]>> domains, ScoreHeap<Crossword.PositionDirectionLength> parentHeap) {
        var heap = parentHeap.copy();
        var position = heap.poll();
        if (position == null) return true;
        if (position.score() == 0) return false;
        var pattern = crossword.get(position.value());
        var words = dictionary.fromPattern(pattern);
        if (words.isEmpty()) return false;
        var randomWords = RandomIterator.randomStartIterator(words, random);
        while (randomWords.hasNext()) {
            var affectedPositions = crossword.setAndGetAffected(position.value(), randomWords.next(), 2);
            for (var affected : affectedPositions) {
                var score = dictionary.fromPatternCount(crossword.get(affected));
                heap.set(affected, score);
            }
            if (fill(crossword, domains, heap)) return true;
        }
        crossword.set(position.value(), pattern);
        return false;
    }
}
