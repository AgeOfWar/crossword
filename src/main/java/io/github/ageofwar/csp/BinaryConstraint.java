package io.github.ageofwar.csp;

public interface BinaryConstraint<T> {
    boolean test(T left, T right);
    default void test(Domain<T> left, Domain<T> right) {
    }

    default BinaryConstraint<T> swap() {
        return new BinaryConstraint<>() {
            @Override
            public boolean test(T left, T right) {
                return BinaryConstraint.this.test(right, left);
            }

            @Override
            public void test(Domain<T> left, Domain<T> right) {
                BinaryConstraint.this.test(right, left);
            }
        };
    }
}
