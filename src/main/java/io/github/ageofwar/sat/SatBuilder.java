package io.github.ageofwar.sat;

import java.util.ArrayList;

public class SatBuilder {
    private ArrayList<int[]> clauses;
    private int numVars;

    public SatBuilder() {
        clauses = new ArrayList<>();
        numVars = 0;
    }

    public int newVar() {
        return ++numVars;
    }

    public void addOrClause(int... literals) {
        clauses.add(literals);
    }

    public void addMostOneClauses(int... literals) {
        if (literals.length == 0) return;
        if (literals.length == 1) {
            addOrClause(literals[0]);
            return;
        }
        var aux = new int[literals.length - 1];
        for (int i = 0; i < aux.length; i++) {
            aux[i] = newVar();
        }

        addOrClause(-literals[0], aux[0]);
        for (int i = 1; i < literals.length - 1; i++) {
            addOrClause(-literals[i], aux[i]);
            addOrClause(-aux[i - 1], aux[i]);
            addOrClause(-literals[i], -aux[i - 1]);
        }
        addOrClause(-literals[literals.length - 1], -aux[literals.length - 2]);
    }

    public void addOneOfClauses(int... literals) {
        addOrClause(literals);
        addMostOneClauses(literals);
    }

    public Sat build() {
        return new Sat(clauses.toArray(new int[0][]), numVars);
    }
}
