package io.github.ageofwar;

import java.util.*;

public class Crossword {
    public static final byte EMPTY = -1;
    public static final byte BLACK = -2;

    private final int width;
    private final int height;
    private final byte[][] grid;

    public static Crossword fromString(String string) {
        string = string.trim();
        var width = -1;
        var grid = new ArrayList<ArrayList<Byte>>();
        grid.add(new ArrayList<>());
        var x = 0;
        for (var c : string.toCharArray()) {
            switch (c) {
                case '.' -> {
                    grid.getLast().add(EMPTY);
                    x++;
                }
                case '#' -> {
                    grid.getLast().add(BLACK);
                    x++;
                }
                case '\n' -> {
                    if (width != -1) {
                        if (x != width) {
                            throw new IllegalArgumentException("Invalid pattern: inconsistent width");
                        }
                    } else {
                        width = x;
                    }
                    x = 0;
                    grid.add(new ArrayList<>());
                }
                default -> {
                    if (c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z') {
                        grid.getLast().add(Pattern.fromChar(c));
                        x++;
                    } else if (!Character.isWhitespace(c)) {
                        throw new IllegalArgumentException("Invalid character: " + c);
                    }
                }
            }
        }

        return new Crossword(grid.stream().map(Crossword::toArray).toArray(byte[][]::new));
    }

    private static byte[] toArray(List<Byte> list) {
        var array = new byte[list.size()];
        for (var i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private Crossword(byte[][] grid) {
        this.grid = grid;
        width = grid[0].length;
        height = grid.length;
    }

    public byte[] getAcross(Position pos) {
        var x = pos.x;
        var y = pos.y;
        while (x > 0 && grid[y][x - 1] != BLACK) {
            x--;
        }
        var length = 0;
        while (x + length < width && grid[y][x + length] != BLACK) {
            length++;
        }
        var result = new byte[length];
        System.arraycopy(grid[y], x, result, 0, length);
        return result;
    }

    public byte[] getDown(Position pos) {
        var x = pos.x;
        var y = pos.y;
        while (y > 0 && grid[y - 1][x] != BLACK) {
            y--;
        }
        var length = 0;
        while (y + length < height && grid[y + length][x] != BLACK) {
            length++;
        }
        var result = new byte[length];
        for (var i = 0; i < length; i++) {
            result[i] = grid[y + i][x];
        }
        return result;
    }

    public byte[] get(Position pos, Direction direction) {
        return switch (direction) {
            case ACROSS -> getAcross(pos);
            case DOWN -> getDown(pos);
        };
    }

    public byte[] get(PositionDirectionLength pos) {
        return get(pos.position(), pos.direction());
    }

    public byte get(Position pos) {
        return grid[pos.y()][pos.x()];
    }

    public void set(Position pos, byte value) {
        grid[pos.y()][pos.x()] = value;
    }

    public void setAcross(Position pos, byte[] pattern) {
        var x = pos.x;
        var y = pos.y;
        while (x > 0 && grid[y][x - 1] != BLACK) {
            x--;
        }
        System.arraycopy(pattern, 0, grid[y], x, pattern.length);
    }

    public void setDown(Position pos, byte[] pattern) {
        var x = pos.x;
        var y = pos.y;
        while (y > 0 && grid[y - 1][x] != BLACK) {
            y--;
        }
        for (var i = 0; i < pattern.length; i++) {
            grid[y + i][x] = pattern[i];
        }
    }

    public void set(Position pos, Direction direction, byte[] pattern) {
        switch (direction) {
            case ACROSS -> setAcross(pos, pattern);
            case DOWN -> setDown(pos, pattern);
        }
    }

    public void set(PositionDirectionLength pos, byte[] pattern) {
        set(pos.position(), pos.direction(), pattern);
    }

    public List<PositionLength> getAcrossPositions(int minLength) {
        var positions = new ArrayList<PositionLength>();
        for (var y = 0; y < height; y++) {
            var x = 0;
            while (x < width) {
                if (grid[y][x] != BLACK) {
                    var length = 0;
                    while (x + length < width && grid[y][x + length] != BLACK) {
                        length++;
                    }
                    if (length >= minLength) {
                        positions.add(new PositionLength(new Position(x, y), length));
                    }
                    x += length;
                }
                x++;
            }
        }
        return positions;
    }

    public List<PositionLength> getDownPositions(int minLength) {
        var positions = new ArrayList<PositionLength>();
        for (var x = 0; x < width; x++) {
            var y = 0;
            while (y < height) {
                if (grid[y][x] != BLACK) {
                    var length = 0;
                    while (y + length < height && grid[y + length][x] != BLACK) {
                        length++;
                    }
                    if (length >= minLength) {
                        positions.add(new PositionLength(new Position(x, y), length));
                    }
                    y += length;
                }
                y++;
            }
        }
        return positions;
    }

    public List<PositionDirectionLength> getPositions(int minLength) {
        var positions = new ArrayList<PositionDirectionLength>();
        for (var position : getAcrossPositions(minLength)) {
            positions.add(new PositionDirectionLength(position.position(), Direction.ACROSS, position.length()));
        }
        for (var position : getDownPositions(minLength)) {
            positions.add(new PositionDirectionLength(position.position(), Direction.DOWN, position.length()));
        }
        return positions;
    }

    public PositionDirectionLength getPosition(Position position, Direction direction) {
        if (direction == Direction.ACROSS) {
            var start = position.x;
            while (start > 0 && grid[position.y][start - 1] != BLACK) {
                start--;
            }
            var end = position.x;
            while (end < width && grid[position.y][end] != BLACK) {
                end++;
            }
            return new PositionDirectionLength(new Position(start, position.y), direction, end - start);
        } else {
            var start = position.y;
            while (start > 0 && grid[start - 1][position.x] != BLACK) {
                start--;
            }
            var end = position.y;
            while (end < height && grid[end][position.x] != BLACK) {
                end++;
            }
            return new PositionDirectionLength(new Position(position.x, start), direction, end - start);
        }
    }

    public List<PositionDirectionLength> getIntersectingPositions(PositionDirectionLength pos) {
        var positions = new ArrayList<PositionDirectionLength>();
        for (var i = 0; i < pos.length(); i++) {
            var position = switch (pos.direction()) {
                case ACROSS -> new Position(pos.position().x() + i, pos.position().y());
                case DOWN -> new Position(pos.position().x(), pos.position().y() + i);
            };
            var intersectingPosition = getPosition(position, pos.direction().opposite());
            if (intersectingPosition.length() > 1) {
                positions.add(intersectingPosition);
            }
        }
        return positions;
    }

    public List<PositionDirectionLength> setAndGetAffected(PositionDirectionLength pos, byte[] pattern, int minLength) {
        var affected = new ArrayList<PositionDirectionLength>();
        var oppositeDirection = pos.direction().opposite();
        for (var i = 0; i < pos.length(); i++) {
            var position = switch (pos.direction()) {
                case ACROSS -> new Position(pos.position().x() + i, pos.position().y());
                case DOWN -> new Position(pos.position().x(), pos.position().y() + i);
            };
            if (get(position) != pattern[i]) {
                set(position, pattern[i]);
                var affectedPosition = getPosition(position, oppositeDirection);
                if (affectedPosition.length() >= minLength) affected.add(affectedPosition);
            }
        }
        return affected;
    }

    public List<Position> emptyCells() {
        var positions = new ArrayList<Position>();
        for (var y = 0; y < height; y++) {
            for (var x = 0; x < width; x++) {
                if (grid[y][x] == EMPTY) {
                    positions.add(new Position(x, y));
                }
            }
        }
        return positions;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                stringBuilder.append(Pattern.toChar(grid[y][x])).append(' ');
            }
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

    public Crossword copy() {
        return new Crossword(Arrays.stream(grid).map(byte[]::clone).toArray(byte[][]::new));
    }

    public record Position(int x, int y) {}
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
    public record PositionDirectionLength(Position position, Direction direction, int length) {
        public Position getPosition(int offset) {
            return switch (direction) {
                case ACROSS -> new Position(position.x() + offset, position.y());
                case DOWN -> new Position(position.x(), position.y() + offset);
            };
        }
    }
    public record PositionLength(Position position, int length) {}
}
