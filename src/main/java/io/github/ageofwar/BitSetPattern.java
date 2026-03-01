package io.github.ageofwar;

import java.util.BitSet;
import java.util.List;

public final class BitSetPattern {
    private BitSetPattern() {
    }

    public static String toString(BitSet... pattern) {
        var builder = new StringBuilder();
        for (var i : pattern) {
            builder.append(toChar(i));
        }
        return builder.toString();
    }

    public static BitSet[] fromString(String string) {
        var pattern = new BitSet[string.length()];
        for (var i = 0; i < string.length(); i++) {
            pattern[i] = fromChar(string.charAt(i));
        }
        return pattern;
    }

    public static BitSet fromChar(char c) {
        if (c == '.') {
            var bitSet = new BitSet(26);
            bitSet.set(0, 26);
            return bitSet;
        } else if (c == '#') {
            return DictionaryConstrainedCrossword.BLACK;
        } else if (c >= 'A' && c <= 'Z') {
            var bitSet = new BitSet(26);
            bitSet.set(c - 'A');
            return bitSet;
        } else if (c >= 'a' && c <= 'z') {
            var bitSet = new BitSet(26);
            bitSet.set(c - 'a');
            return bitSet;
        } else {
            throw new IllegalArgumentException("Invalid character: " + c);
        }
    }

    public static char toChar(BitSet mask) {
        if (mask == DictionaryConstrainedCrossword.BLACK) {
            return '#';
        }

        var firstSetBit = mask.nextSetBit(0);
        var nextSetBit = mask.nextSetBit(firstSetBit + 1);

        if (firstSetBit == -1 || nextSetBit != -1) {
            return '.';
        } else {
            return (char) (firstSetBit + 'A');
        }
    }

    public static BitSet[] fromWords(int length, Iterable<byte[]> words) {
        BitSet[] pattern = new BitSet[length];
        for (int i = 0; i < length; i++) {
            pattern[i] = new BitSet(26);
        }

        for (var word : words) {
            for (int i = 0; i < length; i++) {
                pattern[i].set(word[i]);
            }
        }
        return pattern;
    }

    public static BitSet[] fromPattern(byte[] pattern) {
        BitSet[] result = new BitSet[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            result[i] = fromPattern(pattern[i]);
        }
        return result;
    }

    public static BitSet fromPattern(byte pattern) {
        var bitSet = new BitSet(26);
        if (pattern == Trie.WILDCARD) {
            bitSet.set(0, 26);
        } else {
            bitSet.set(pattern);
        }
        return bitSet;
    }
}
