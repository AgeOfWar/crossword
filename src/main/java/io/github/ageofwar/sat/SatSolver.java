package io.github.ageofwar.sat;

import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

public class SatSolver {
    public SatSolver() {
    }

    public int[] solve(Sat sat) {
        try {
            var solver = SolverFactory.newDefault();
            solver.newVar(sat.numVars());
            var vecClauses = new Vec<IVecInt>();
            for (var clause : sat.clauses()) {
                vecClauses.push(new VecInt(clause));
            }
            solver.addAllClauses(vecClauses);
            if (!solver.isSatisfiable()) return null;
            return solver.model();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        } catch (ContradictionException e) {
            return null;
        }
    }
}
