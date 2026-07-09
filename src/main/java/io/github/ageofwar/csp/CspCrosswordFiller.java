package io.github.ageofwar.csp;

import io.github.ageofwar.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        var positions = crossword.getPositions(2);
        var positionsIndices = new HashMap<PositionDirectionLength, Integer>();
        for (var i = 0; i < positions.size(); i++) {
            positionsIndices.put(positions.get(i), i);
        }
        var domains = new ArrayList<byte[][]>(positions.size());
        var constraints = new ArrayList<Csp.BinaryConstraint<byte[]>>();
        for (int i = 0; i < positions.size(); i++) {
            var position = positions.get(i);
            var domain = dictionary.wordsByLength(position.length());
            Collections.shuffle(domain, random);
            domains.add(domain.toArray(new byte[0][]));
            for (var intersection : crossword.getIntersections(position)) {
                var intersectionIndex = positionsIndices.get(intersection);
                var leftIntersection = position.direction() == Direction.ACROSS ?
                        intersection.position().x() - position.position().x() :
                        intersection.position().y() - position.position().y();
                var rightIntersection = position.direction() == Direction.ACROSS ?
                        position.position().y() - intersection.position().y() :
                        position.position().x() - intersection.position().x();
                constraints.add(new Csp.BinaryConstraint<>(i, intersectionIndex, new CrosswordIntersectionConstraint(leftIntersection, rightIntersection)));
            }
        }

        var solver = new CspSolver<byte[]>();
        var assignments = solver.solve(new Csp<>(domains.toArray(new byte[0][][]), constraints));
        var clone = crossword.copy();
        for (int i = 0; i < assignments.size(); i++) {
            var position = positions.get(i);
            var assignment = assignments.get(i);
            clone.set(position, assignment);
        }
        return clone;
    }

    private static class CrosswordIntersectionConstraint implements BinaryConstraint<byte[]> {
        private final int leftIntersection;
        private final int rightIntersection;

        public CrosswordIntersectionConstraint(int leftIntersection, int rightIntersection) {
            this.leftIntersection = leftIntersection;
            this.rightIntersection = rightIntersection;
        }

        @Override
        public boolean test(byte[] left, byte[] right) {
            return left[leftIntersection] == right[rightIntersection];
        }

        @Override
        public void test(Domain<byte[]> left, Domain<byte[]> right) {
            var allowedLetters = new boolean[26];
            for (var word : left) allowedLetters[word[leftIntersection]] = true;
            right.removeIf(candidate -> !allowedLetters[candidate[rightIntersection]]);
        }
    }
}
