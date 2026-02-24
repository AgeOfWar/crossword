package io.github.ageofwar.sat;

import io.github.ageofwar.Crossword;

import java.util.Map;

public class CrosswordSatDecoder {
    private final Map<Integer, CrosswordSatEncoder.PositionLetter> variables;

    public CrosswordSatDecoder(Map<Integer, CrosswordSatEncoder.PositionLetter> variables) {
        this.variables = variables;
    }

    public Crossword decode(Crossword crossword, int[] model) {
        crossword = crossword.copy();
        for (int var : model) {
            if (var < 0) continue;
            var positionLetter = variables.get(var);
            if (positionLetter != null) {
                crossword.set(positionLetter.position(), positionLetter.letter());
            }
        }
        return  crossword;
    }
}
