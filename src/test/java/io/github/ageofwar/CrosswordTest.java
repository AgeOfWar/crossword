package io.github.ageofwar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CrosswordTest {
    @Test
    void testFromString() {
        var input = "..#\n#..\n..#";
        var crossword = Crossword.fromString(input);

        assertEquals(3, crossword.width());
        assertEquals(3, crossword.height());

        assertEquals(Crossword.EMPTY, crossword.get(new Crossword.Position(0, 0)));
        assertEquals(Crossword.BLACK, crossword.get(new Crossword.Position(2, 0)));
        assertEquals(Crossword.EMPTY, crossword.get(new Crossword.Position(1, 2)));
    }
}
