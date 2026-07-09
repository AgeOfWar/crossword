package io.github.ageofwar.csp;

import java.util.List;

public record Csp<T>(T[][] domains, List<BinaryConstraint<T>> binaryConstraints) {
    public Csp(T[][] domains, BinaryConstraint<T>... binaryConstraints) {
        this(domains, List.of(binaryConstraints));
    }

    public record BinaryConstraint<T>(int left, int right, io.github.ageofwar.csp.BinaryConstraint<T> constraint) {
        public BinaryConstraint<T> swap() {
            return new BinaryConstraint<>(right, left, constraint.swap());
        }
    }
}
