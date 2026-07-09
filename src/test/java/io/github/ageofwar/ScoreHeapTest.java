package io.github.ageofwar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScoreHeapTest {
    @Test
    void testAddAndPoll() {
        var heap = new ScoreHeap<String>();
        heap.set("A", 3);
        heap.set("B", 1);
        heap.set("C", 2);

        assertEquals("B", heap.poll().value());
        assertEquals("C", heap.poll().value());
        assertEquals("A", heap.poll().value());
        assertNull(heap.poll());
    }

    @Test
    void testIsEmpty() {
        var heap = new ScoreHeap<Integer>();
        assertTrue(heap.isEmpty());
        heap.set(5, 1);
        assertFalse(heap.isEmpty());
        heap.poll();
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSize() {
        var heap = new ScoreHeap<String>();
        assertEquals(0, heap.size());
        heap.set("X", 5);
        heap.set("Y", 3);
        assertEquals(2, heap.size());
        heap.poll();
        assertEquals(1, heap.size());
    }

    @Test
    void testCopy() {
        var heap = new ScoreHeap<String>();
        heap.set("A", 3);
        heap.set("B", 1);

        var clone = heap.copy();

        assertEquals(heap.poll().value(), clone.poll().value());
        assertEquals(heap.poll().value(), clone.poll().value());

        assertTrue(heap.isEmpty());
        assertTrue(clone.isEmpty());
    }

    @Test
    void testAddBeyondCapacity() {
        var heap = new ScoreHeap<Integer>(2);
        heap.set(1, 10);
        heap.set(2, 5);
        heap.set(3, 1);

        assertEquals(3, heap.poll().value());
        assertEquals(2, heap.poll().value());
        assertEquals(1, heap.poll().value());
    }

    @Test
    void testComplexOperations() {
        var heap = new ScoreHeap<Character>();
        heap.set('A', 50);
        heap.set('B', 30);
        heap.set('C', 40);
        heap.set('D', 20);
        heap.set('E', 10);

        assertEquals('E', heap.poll().value());
        assertEquals('D', heap.poll().value());
        assertEquals('B', heap.poll().value());
        assertEquals('C', heap.poll().value());
        assertEquals('A', heap.poll().value());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testNegativeScores() {
        var heap = new ScoreHeap<String>();
        heap.set("Low", -10);
        heap.set("High", -1);

        assertEquals("Low", heap.poll().value());
        assertEquals("High", heap.poll().value());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSingleElement() {
        var heap = new ScoreHeap<String>();
        heap.set("Solo", 100);
        assertEquals("Solo", heap.poll().value());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSet() {
        var heap = new ScoreHeap<String>();
        heap.set("A", 3);
        heap.set("B", 1);
        heap.set("C", 2);

        heap.set("B", 4);
        heap.set("C", 0);
        heap.set("A", 5);

        assertEquals("C", heap.poll().value());
        assertEquals("B", heap.poll().value());
        assertEquals("A", heap.poll().value());
        assertTrue(heap.isEmpty());
    }
}
