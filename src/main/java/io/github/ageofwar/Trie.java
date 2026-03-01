package io.github.ageofwar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Trie {
    public static final byte WILDCARD = -1;

    private final Trie[] children;

    private boolean endOfWord = false;

    public Trie(byte alphabetSize) {
        children = new Trie[alphabetSize];
    }

    public Trie getChild(byte letter) {
        return children[letter];
    }

    public boolean isEndOfWord() {
        return endOfWord;
    }

    public boolean insert(byte... word) {
        Trie current = this;
        for (byte letter : word) {
            if (current.children[letter] == null) {
                current.children[letter] = new Trie((byte) children.length);
            }
            current = current.children[letter];
        }
        if (!current.endOfWord) {
            current.endOfWord = true;
            return true;
        }
        return false;
    }

    public List<byte[]> fromPattern(byte... pattern) {
        var result = new ArrayList<byte[]>();
        fromPattern(pattern, new byte[pattern.length], 0, result);
        return result;
    }

    private void fromPattern(byte[] pattern, byte[] currentWord, int depth, List<byte[]> result) {
        if (depth == pattern.length) {
            if (endOfWord) {
                result.add(currentWord.clone());
            }
            return;
        }

        byte letter = pattern[depth];
        if (letter == WILDCARD) {
            for (byte i = 0; i < children.length; i++) {
                if (children[i] != null) {
                    currentWord[depth] = i;
                    children[i].fromPattern(pattern, currentWord, depth + 1, result);
                }
            }
        } else if (children[letter] != null) {
            currentWord[depth] = letter;
            children[letter].fromPattern(pattern, currentWord, depth + 1, result);
        }
    }

    public int fromPatternCount(byte... pattern) {
        return fromPatternCount(pattern, 0);
    }

    public int fromPatternCount(byte[] pattern, int depth) {
        if (depth == pattern.length) {
            return endOfWord ? 1 : 0;
        }

        byte letter = pattern[depth];
        if (letter == WILDCARD) {
            int count = 0;
            for (Trie child : children) {
                if (child != null) {
                    count += child.fromPatternCount(pattern, depth + 1);
                }
            }
            return count;
        } else if (children[letter] != null) {
            return children[letter].fromPatternCount(pattern, depth + 1);
        } else {
            return 0;
        }
    }

    public List<byte[]> fromPattern(BitSet[] pattern) {
        var result = new ArrayList<byte[]>();
        fromPattern(pattern, new byte[pattern.length], 0, result);
        return result;
    }

    private void fromPattern(BitSet[] pattern, byte[] currentWord, int depth, List<byte[]> result) {
        if (depth == pattern.length) {
            if (endOfWord) {
                result.add(currentWord.clone());
            }
            return;
        }

        var letters = pattern[depth];
        for (byte i = 0; i < children.length; i++) {
            if (letters.get(i) && children[i] != null) {
                currentWord[depth] = i;
                children[i].fromPattern(pattern, currentWord, depth + 1, result);
            }
        }
    }
}
