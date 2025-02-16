package io.github.ageofwar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    private final Trie trie;
    private final List<Integer> wordCountByLength;

    private static byte[] toBytes(String s) {
        var bytes = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            var c = Character.toUpperCase(s.charAt(i));
            if (c == '.') {
                bytes[i] = Trie.WILDCARD;
            } else {
                bytes[i] = (byte) (c - 'A');
            }
        }
        return bytes;
    }

    private static String toString(byte[] bytes) {
        var s = new StringBuilder();
        for (byte b : bytes) {
            if (b == Trie.WILDCARD) {
                s.append('.');
            } else {
                s.append((char) (b + 'A'));
            }
        }
        return s.toString();
    }

    public static Dictionary fromFile(String path) throws IOException {
        var dictionary = new Dictionary();
        try (var lines = Files.lines(Path.of(path))) {
            lines.forEach(line -> {
                if (!line.isBlank()) {
                    dictionary.insert(line.replaceAll("[^A-Za-z]", ""));
                }
            });
        }
        return dictionary;
    }

    public Dictionary() {
        wordCountByLength = new ArrayList<>();
        trie = new Trie((byte) 26);
    }

    public void insert(String word) {
        if (!word.matches("[A-Za-z]*")) throw new IllegalArgumentException("Invalid word: " + word);
        if (trie.insert(toBytes(word))) {
            for (int i = wordCountByLength.size(); i <= word.length(); i++) {
                wordCountByLength.add(0);
            }
            wordCountByLength.set(word.length(), wordCountByLength.get(word.length()) + 1);
        }
    }

    public List<byte[]> fromPattern(byte[] pattern) {
        return trie.fromPattern(pattern);
    }

    public int fromPatternCount(byte[] pattern) {
        return trie.fromPatternCount(pattern);
    }

    public int wordCountByLength(int length) {
        return length < wordCountByLength.size() ? wordCountByLength.get(length) : 0;
    }
}
