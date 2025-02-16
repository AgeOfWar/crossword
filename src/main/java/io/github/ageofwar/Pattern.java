package io.github.ageofwar;

public final class Pattern {
    private Pattern() {
    }

    public static String toString(byte... pattern) {
        var builder = new StringBuilder();
        for (var i : pattern) {
            builder.append(toChar(i));
        }
        return builder.toString();
    }

    public static byte[] fromString(String string) {
        var pattern = new byte[string.length()];
        for (var i = 0; i < string.length(); i++) {
            pattern[i] = fromChar(string.charAt(i));
        }
        return pattern;
    }

    public static byte fromChar(char c) {
        if (c == '.') {
            return Crossword.EMPTY;
        } else if (c == '#') {
            return Crossword.BLACK;
        } else if (c >= 'A' && c <= 'Z') {
            return (byte) (c - 'A');
        } else if (c >= 'a' && c <= 'z') {
            return (byte) (c - 'a');
        } else {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
    }

    public static char toChar(byte i) {
        if (i == Crossword.EMPTY) {
            return '.';
        } else if (i == Crossword.BLACK) {
            return '#';
        } else {
            return (char) (i + 'A');
        }
    }
}
