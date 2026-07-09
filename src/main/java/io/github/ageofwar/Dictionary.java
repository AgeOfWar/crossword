package io.github.ageofwar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class Dictionary {
    private final Trie trie;
    private final List<Integer> wordCountByLength;

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
        if (trie.insert(Pattern.fromString(word))) {
            for (int i = wordCountByLength.size(); i <= word.length(); i++) {
                wordCountByLength.add(0);
            }
            wordCountByLength.set(word.length(), wordCountByLength.get(word.length()) + 1);
        }
    }

    public Trie getTrie() {
        return trie;
    }

    public List<byte[]> fromPattern(byte[] pattern) {
        return trie.fromPattern(pattern);
    }

    public List<byte[]> fromPattern(BitSet[] pattern) {
        return trie.fromPattern(pattern);
    }

    public int fromPatternCount(byte[] pattern) {
        return trie.fromPatternCount(pattern);
    }

    public int wordCountByLength(int length) {
        return length < wordCountByLength.size() ? wordCountByLength.get(length) : 0;
    }
}
