package io.github.ageofwar.csp;

import java.util.*;

public class CspSolver<T> {
    public CspSolver() {
    }

    public List<T> solve(Csp<T> csp) {
        var domains = new ArrayList<Variable<T>>(csp.domains().length);
        var groupedBinaryConstraints = new ArrayList<List<Csp.BinaryConstraint<T>>>(csp.domains().length);
        for (var i = 0; i < csp.domains().length; i++) {
            domains.add(new Variable<>(i, new Domain<>(csp.domains()[i])));
            groupedBinaryConstraints.add(new ArrayList<>());
        }
        for (var binaryConstraint : csp.binaryConstraints()) {
            groupedBinaryConstraints.get(binaryConstraint.left()).add(binaryConstraint);
            groupedBinaryConstraints.get(binaryConstraint.right()).add(binaryConstraint.swap());
        }
        return solve(domains, groupedBinaryConstraints, new ArrayList<T>(Collections.nCopies(domains.size(), null)));
    }

    private List<T> solve(List<Variable<T>> variables, List<List<Csp.BinaryConstraint<T>>> binaryConstraints, List<T> assignments) {
        var unassigned = variables.stream()
                .filter(v -> assignments.get(v.name()) == null)
                .toList();
        if (unassigned.isEmpty()) return assignments;

        var variable = selectVariable(unassigned, binaryConstraints);
        for (var candidate : variable.domain()) {
            assignments.set(variable.name(), candidate);
            for (var v : unassigned) v.domain().checkpoint();
            if (!arcConsistency(variables, binaryConstraints, assignments, variable.name())) {
                for (var v : unassigned) v.domain().reset();
                assignments.set(variable.name(), null);
                continue;
            }
            var solution = solve(variables, binaryConstraints, assignments);
            if (solution == null) {
                for (var v : unassigned) v.domain().reset();
                assignments.set(variable.name(), null);
                continue;
            }
            return solution;
        }

        return null;
    }

    private boolean arcConsistency(List<Variable<T>> variables, List<List<Csp.BinaryConstraint<T>>> binaryConstraints, List<T> assignments, int variable) {
        var queue = new ArrayDeque<Csp.BinaryConstraint<T>>();

        for (var constraint : binaryConstraints.get(variable)) {
            if (assignments.get(constraint.right()) != null) continue;
            var rightDomain = variables.get(constraint.right()).domain();
            if (rightDomain.removeIf(candidate -> !constraint.constraint().test(assignments.get(variable), candidate))) {
                if (rightDomain.isEmpty()) return false;
                for (var binaryConstraint : binaryConstraints.get(constraint.right())) {
                    if (binaryConstraint.right() == constraint.left()) continue;
                    queue.add(binaryConstraint);
                }
            }
        }

        while (!queue.isEmpty()) {
            var constraint = queue.poll();
            if (assignments.get(constraint.right()) != null) continue;
            var leftDomain = variables.get(constraint.left()).domain();
            var rightDomain = variables.get(constraint.right()).domain();
            var rightDomainSizeBefore = rightDomain.size();
            constraint.constraint().test(leftDomain, rightDomain);
            if (rightDomain.size() < rightDomainSizeBefore) {
                if (rightDomain.isEmpty()) return false;
                for (var binaryConstraint : binaryConstraints.get(constraint.right())) {
                    if (binaryConstraint.right() == constraint.left()) continue;
                    queue.add(binaryConstraint);
                }
            }
        }

        return true;
    }

    private Variable<T> selectVariable(Collection<Variable<T>> variables, List<List<Csp.BinaryConstraint<T>>> binaryConstraints) {
        return Collections.min(variables, Comparator.comparingDouble(v -> (double) v.domain().size() / binaryConstraints.get(v.name()).size()));
    }

    private record Variable<T>(int name, Domain<T> domain) {
    }
}
