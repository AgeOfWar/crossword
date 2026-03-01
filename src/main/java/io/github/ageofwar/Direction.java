package io.github.ageofwar;

public enum Direction {
    ACROSS,
    DOWN;

    public Direction opposite() {
        return switch (this) {
            case ACROSS -> DOWN;
            case DOWN -> ACROSS;
        };
    }
}
