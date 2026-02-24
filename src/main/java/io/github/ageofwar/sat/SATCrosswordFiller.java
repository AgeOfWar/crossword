package io.github.ageofwar.sat;

import io.github.ageofwar.Crossword;
import io.github.ageofwar.CrosswordFiller;
import io.github.ageofwar.Dictionary;
import org.sat4j.specs.*;

public class SATCrosswordFiller implements CrosswordFiller {
    private final Dictionary dictionary;

    public SATCrosswordFiller(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public Crossword fill(Crossword crossword) {
        var encoder = new CrosswordSatEncoder(dictionary);
        var output = encoder.encode(crossword);
        var solver = new SatSolver();
        System.out.println("SAT problem has " + output.sat().numVars() + " variables and " + output.sat().clauses().length + " clauses");
        var model = solver.solve(output.sat());
        if (model == null) return null;
        var decoder = new CrosswordSatDecoder(output.variables());
        return decoder.decode(crossword, model);
    }

}
