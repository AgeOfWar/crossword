package io.github.ageofwar;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ScoreHeapTest {
    @Test
    void testAddAndPoll() {
        var heap = new ScoreHeap<String>();
        heap.add("A", 3);
        heap.add("B", 1);
        heap.add("C", 2);

        assertEquals("B", heap.poll().value());
        assertEquals("C", heap.poll().value());
        assertEquals("A", heap.poll().value());
        assertNull(heap.poll());
    }

    @Test
    void testIsEmpty() {
        var heap = new ScoreHeap<Integer>();
        assertTrue(heap.isEmpty());
        heap.add(5, 1);
        assertFalse(heap.isEmpty());
        heap.poll();
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSize() {
        var heap = new ScoreHeap<String>();
        assertEquals(0, heap.size());
        heap.add("X", 5);
        heap.add("Y", 3);
        assertEquals(2, heap.size());
        heap.poll();
        assertEquals(1, heap.size());
    }

    @Test
    void testCopy() {
        var heap = new ScoreHeap<String>();
        heap.add("A", 3);
        heap.add("B", 1);

        var clone = heap.copy();

        assertEquals(heap.poll().value(), clone.poll().value());
        assertEquals(heap.poll().value(), clone.poll().value());

        assertTrue(heap.isEmpty());
        assertTrue(clone.isEmpty());
    }

    @Test
    void testAddBeyondCapacity() {
        var heap = new ScoreHeap<Integer>(2);
        heap.add(1, 10);
        heap.add(2, 5);
        heap.add(3, 1);

        assertEquals(3, heap.poll().value());
        assertEquals(2, heap.poll().value());
        assertEquals(1, heap.poll().value());
    }

    @Test
    void testComplexOperations() {
        var heap = new ScoreHeap<Character>();
        heap.add('A', 50);
        heap.add('B', 30);
        heap.add('C', 40);
        heap.add('D', 20);
        heap.add('E', 10);

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
        heap.add("Low", -10);
        heap.add("High", -1);

        assertEquals("Low", heap.poll().value());
        assertEquals("High", heap.poll().value());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSingleElement() {
        var heap = new ScoreHeap<String>();
        heap.add("Solo", 100);
        assertEquals("Solo", heap.poll().value());
        assertTrue(heap.isEmpty());
    }

    @Test
    void testRemove() {
        var heap = new ScoreHeap<String>();
        heap.add("A", 3);
        heap.add("B", 1);
        heap.add("C", 2);

        assertTrue(heap.remove("B"));
        assertFalse(heap.remove("B"));
        assertTrue(heap.remove("A"));
        assertTrue(heap.remove("C"));
        assertFalse(heap.remove("C"));
        assertTrue(heap.isEmpty());
    }

    @Test
    void testSet() {
        var heap = new ScoreHeap<String>();
        heap.add("A", 3);
        heap.add("B", 1);
        heap.add("C", 2);

        heap.set("B", 4);
        heap.set("C", 0);
        heap.set("A", 5);

        assertEquals("C", heap.poll().value());
        assertEquals("B", heap.poll().value());
        assertEquals("A", heap.poll().value());
        assertTrue(heap.isEmpty());
    }
}
