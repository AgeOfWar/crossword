package io.github.ageofwar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class DictionaryTest {

    @Test
    void testInsertAndCount() {
        var dictionary = new Dictionary();
        dictionary.insert("APPLE");
        dictionary.insert("ORANGE");

        assertEquals(1, dictionary.wordCountByLength(5));
        assertEquals(1, dictionary.wordCountByLength(6));
        assertEquals(0, dictionary.wordCountByLength(4));
    }

    @Test
    void testFromPattern() {
        var dictionary = new Dictionary();
        dictionary.insert("APPLE");
        dictionary.insert("APPLY");
        dictionary.insert("APART");

        var pattern = Pattern.fromString("A..LE");
        List<byte[]> results = dictionary.fromPattern(pattern);

        assertEquals(1, results.size());
        assertEquals("APPLE", Pattern.toString(results.getFirst()));
    }

    @Test
    void testFromPatternCount() {
        var dictionary = new Dictionary();
        dictionary.insert("APPLE");
        dictionary.insert("APPLY");
        dictionary.insert("APART");

        var pattern = new byte[]{0, 15, Trie.WILDCARD, Trie.WILDCARD, 4};

        assertEquals(1, dictionary.fromPatternCount(pattern));
    }

    @Test
    void testEmptyDictionary() {
        var dictionary = new Dictionary();

        assertEquals(0, dictionary.wordCountByLength(5));

        var pattern = new byte[]{Trie.WILDCARD, Trie.WILDCARD, Trie.WILDCARD};
        assertEquals(0, dictionary.fromPatternCount(pattern));
    }

    @Test
    void testFromFile() throws IOException {
        var tempFile = Files.createTempFile("test-dictionary", ".txt");
        Files.writeString(tempFile, "APPLE\nORANGE\nBANANA\n");

        var dictionary = Dictionary.fromFile(tempFile.toString());

        assertEquals(1, dictionary.wordCountByLength(5));
        assertEquals(2, dictionary.wordCountByLength(6));

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testIgnoreInvalidCharacters() {
        var dictionary = new Dictionary();
        assertThrows(IllegalArgumentException.class, () -> dictionary.insert("APPL3!"));
    }

    @Test
    void testCaseInsensitiveInsert() {
        var dictionary = new Dictionary();
        dictionary.insert("apple");
        dictionary.insert("APPLE");

        assertEquals(1, dictionary.wordCountByLength(5));
    }
}
