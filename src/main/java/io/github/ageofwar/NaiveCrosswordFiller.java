package io.github.ageofwar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class NaiveCrosswordFiller implements CrosswordFiller {
    private final Dictionary dictionary;
    private final Random random;

    public NaiveCrosswordFiller(Dictionary dictionary, Random random) {
        this.dictionary = dictionary;
        this.random = random;
    }

    public NaiveCrosswordFiller(Dictionary dictionary) {
        this(dictionary, new Random());
    }

    @Override
    public Crossword fill(Crossword crossword) {
        var clone = crossword.copy();
        var fillPositions = crossword.getPositions(2);
        var heap = new ScoreHeap<Crossword.PositionDirectionLength>(fillPositions.size());
        for (var position : fillPositions) {
            heap.set(position, dictionary.fromPatternCount(crossword.get(position)));
        }
        if (fill(clone, heap)) {
            return clone;
        }
        return null;
    }

    private boolean fill(Crossword crossword, ScoreHeap<Crossword.PositionDirectionLength> heap) {
        var position = heap.poll();
        if (position == null) return true;
        if (position.score() == 0) {
            heap.set(position.value(), position.score());
            return false;
        }
        var pattern = crossword.get(position.value());
        var words = dictionary.fromPattern(pattern);

        var randomWords = RandomIterator.randomStartIterator(words, random);
        while (randomWords.hasNext()) {
            var affectedPositions = crossword.setAndGetAffected(position.value(), randomWords.next(), 2);
            var oldEntries = new ArrayList<ScoreHeap.Entry<Crossword.PositionDirectionLength>>(affectedPositions.size());
            for (var affected : affectedPositions) {
                oldEntries.add(new ScoreHeap.Entry<>(heap.getScore(affected), affected));
                var score = dictionary.fromPatternCount(crossword.get(affected));
                heap.set(affected, score);
            }
            if (fill(crossword, heap)) return true;
            crossword.set(position.value(), pattern);
            for (var entry : oldEntries) {
                heap.set(entry.value(), entry.score());
            }
        }

        heap.set(position.value(), position.score());
        return false;
    }
}
