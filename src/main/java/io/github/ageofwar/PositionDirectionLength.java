package io.github.ageofwar;

import java.util.ArrayList;

public record PositionDirectionLength(Position position, Direction direction, int length) {
    public Position getPosition(int offset) {
        return switch (direction) {
            case ACROSS -> new Position(position.x() + offset, position.y());
            case DOWN -> new Position(position.x(), position.y() + offset);
        };
    }
}
