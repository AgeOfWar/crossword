package io.github.ageofwar;

import io.github.ageofwar.csp.Domain;

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
        Domain<byte[]>[] domains = new Domain[0];
        if (!fill(clone, domains)) return null;
        return clone;
    }

    private boolean fill(Crossword crossword, Domain<byte[]>[] domains) {
        // TODO
        return false;
    }
}
