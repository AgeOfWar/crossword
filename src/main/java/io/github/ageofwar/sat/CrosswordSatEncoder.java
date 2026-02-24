package io.github.ageofwar.sat;

import io.github.ageofwar.Crossword;
import io.github.ageofwar.Dictionary;
import io.github.ageofwar.Trie;

import java.util.*;

public class CrosswordSatEncoder {
    private final Dictionary dictionary;

    public CrosswordSatEncoder(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Output encode(Crossword crossword) {
        var builder = new SatBuilder();
        var variables = new HashMap<Crossword.Position, int[]>();
        var outputVariables = new HashMap<Integer, PositionLetter>();

        var positions = crossword.getPositions(2);
        for (var position : crossword.emptyCells()) {
            var cell = encode(crossword, position, builder, outputVariables);
            if (cell != null) variables.put(position, cell);
        }
        for (var position : positions) {
            encode(crossword, position, builder, variables);
        }

        return new Output(builder.build(), outputVariables);
    }

    private int[] encode(Crossword crossword, Crossword.Position position, SatBuilder builder, Map<Integer, PositionLetter> variables) {
        var pattern = crossword.get(position);
        if (pattern == Crossword.BLACK) return null;
        // TODO handle already filled cells

        var oneCharInCell = new int[26];
        for (byte letter = 0; letter < oneCharInCell.length; letter++) {
            oneCharInCell[letter] = builder.newVar();
            variables.put(oneCharInCell[letter], new PositionLetter(position, letter));
        }

        builder.addOneOfClauses(oneCharInCell);
        return oneCharInCell;
    }

    private void encode(Crossword crossword, Crossword.PositionDirectionLength position, SatBuilder builder, Map<Crossword.Position, int[]> variables) {
        var pattern = crossword.get(position);
        var candidates = dictionary.fromPattern(pattern);
        var oneCandidateInSlot = new int[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            oneCandidateInSlot[i] = encode(crossword, position, builder, candidates.get(i), variables);
        }
        builder.addOneOfClauses(oneCandidateInSlot);
        for (var i = 0; i < position.length(); i++) {
            var cellPosition = position.getPosition(i);
            var cellVariables = variables.get(cellPosition);
            if (cellVariables == null) throw new IllegalStateException("Cell " + cellPosition + " is not empty"); // TODO handle already filled cells
            for (byte letter = 0; letter < cellVariables.length; letter++) {
                var candidatesForLetter = new ArrayList<Integer>();
                for (var j = 0; j < candidates.size(); j++) {
                    if (candidates.get(j)[i] == letter) {
                        candidatesForLetter.add(oneCandidateInSlot[j]);
                    }
                }
                var clause = new int[candidatesForLetter.size() + 1];
                clause[0] = -cellVariables[letter];
                for (int j = 0; j < candidatesForLetter.size(); j++) {
                    clause[j + 1] = candidatesForLetter.get(j);
                }
                builder.addOrClause(clause);
            }
        }
    }

    private int encode(Crossword crossword, Crossword.PositionDirectionLength position, SatBuilder builder, byte[] candidate, Map<Crossword.Position, int[]> variables) {
        var slotIsCandidate = builder.newVar();
        for (int i = 0; i < candidate.length; i++) {
            var cellPosition = position.getPosition(i);
            var cellVariables = variables.get(cellPosition);
            if (cellVariables == null) throw new IllegalStateException("Cell " + cellPosition + " is not empty"); // TODO handle already filled cells
            builder.addOrClause(-slotIsCandidate, cellVariables[candidate[i]]);
        }
        return slotIsCandidate;
    }

    public record Output(Sat sat, Map<Integer, CrosswordSatEncoder.PositionLetter> variables) {
    }

    public record PositionLetter(Crossword.Position position, byte letter) {
    }
}
