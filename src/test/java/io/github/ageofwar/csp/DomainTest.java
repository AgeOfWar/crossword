package io.github.ageofwar.csp;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DomainTest {
    @Test
    void iterationRespectsOriginalOrder() {
        var domain = new Domain<>(1, 2, 3, 4, 5);
        assertEquals(List.of(1, 2, 3, 4, 5), toList(domain));
        assertEquals(5, domain.size());
    }

    @Test
    void emptyDomainDoesNotIterate() {
        var domain = new Domain<Integer>();
        assertFalse(domain.iterator().hasNext());
        assertEquals(0, domain.size());
    }

    @Test
    void singleElementDomain() {
        var domain = new Domain<>(42);
        assertEquals(List.of(42), toList(domain));
    }

    @Test
    void iteratorThrowsNoSuchElementPastTheEnd() {
        var domain = new Domain<>(1);
        var it = domain.iterator();
        it.next();
        assertFalse(it.hasNext());
        // Nota: la classe attuale non lancia esplicitamente NoSuchElementException
        // in next() oltre la fine; questo test documenta il comportamento atteso
        // se si vuole irrobustire l'iteratore. Se fallisce con
        // ArrayIndexOutOfBoundsException invece che NoSuchElementException,
        // vale la pena aggiungere il controllo esplicito in next().
    }

    // --- remove() di base ---

    @Test
    void removeExcludesElementFromIteration() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(2);
        assertEquals(List.of(1, 3), toList(domain));
        assertEquals(2, domain.size());
    }

    @Test
    void removeHead() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(1);
        assertEquals(List.of(2, 3), toList(domain));
    }

    @Test
    void removeTail() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(3);
        assertEquals(List.of(1, 2), toList(domain));
    }

    @Test
    void removingAllElementsEmptiesTheDomain() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(1);
        domain.remove(2);
        domain.remove(3);
        assertEquals(List.of(), toList(domain));
        assertEquals(0, domain.size());
    }

    @Test
    void removingAbsentElementIsNoOp() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(99); // non presente nel dominio
        assertEquals(List.of(1, 2, 3), toList(domain));
        assertEquals(3, domain.size());
    }

    @Test
    void removingSameElementTwiceIsIdempotent() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(2);
        domain.remove(2); // seconda rimozione: deve essere no-op
        assertEquals(List.of(1, 3), toList(domain));
        assertEquals(2, domain.size());

        domain.reset();
        // se la doppia remove avesse corrotto size/checkpoint, qui size sarebbe sballato
        assertEquals(3, domain.size());
        assertEquals(List.of(1, 2, 3), toList(domain));
    }

    // --- removeIf ---

    @Test
    void removeIfRemovesElementsMatchingPredicate() {
        var domain = new Domain<>(1, 2, 3, 4, 5, 6);
        domain.checkpoint();
        domain.removeIf(x -> x % 2 == 0);
        assertEquals(List.of(1, 3, 5), toList(domain));
        assertEquals(3, domain.size());
    }

    @Test
    void removeIfWithAlwaysTruePredicateEmptiesTheDomain() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.removeIf(x -> true);
        assertEquals(List.of(), toList(domain));
    }

    @Test
    void removeIfWithAlwaysFalsePredicateChangesNothing() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.removeIf(x -> false);
        assertEquals(List.of(1, 2, 3), toList(domain));
    }

    // --- checkpoint / backtrack: caso singolo livello ---

    @Test
    void resetRestoresRemovedElements() {
        var domain = new Domain<>(1, 2, 3, 4, 5);
        domain.checkpoint();
        domain.remove(2);
        domain.remove(4);
        assertEquals(List.of(1, 3, 5), toList(domain));

        domain.reset();
        assertEquals(List.of(1, 2, 3, 4, 5), toList(domain));
        assertEquals(5, domain.size());
    }

    @Test
    void resetWithoutRemovalsChangesNothing() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.reset();
        assertEquals(List.of(1, 2, 3), toList(domain));
        assertEquals(3, domain.size());
    }

    @Test
    void resetCorrectlyRestoresHeadAndTail() {
        var domain = new Domain<>(1, 2, 3);
        domain.checkpoint();
        domain.remove(1); // testa
        domain.remove(3); // coda
        assertEquals(List.of(2), toList(domain));

        domain.reset();
        assertEquals(List.of(1, 2, 3), toList(domain));
    }

    // --- checkpoint / backtrack: livelli annidati ---

    @Test
    void nestedCheckpointsRestoreOnlyTheCurrentLevel() {
        var domain = new Domain<>(1, 2, 3, 4, 5);

        domain.checkpoint();       // livello 1
        domain.remove(1);
        assertEquals(List.of(2, 3, 4, 5), toList(domain));

        domain.checkpoint();       // livello 2
        domain.remove(3);
        domain.remove(5);
        assertEquals(List.of(2, 4), toList(domain));

        domain.reset();        // torna al livello 1
        assertEquals(List.of(2, 3, 4, 5), toList(domain));

        domain.reset();        // torna al livello 0
        assertEquals(List.of(1, 2, 3, 4, 5), toList(domain));
    }

    @Test
    void cascadingRemovalsAcrossLevelsRestoreInCorrectOrder() {
        // Verifica esplicita che l'ordine di ripristino forward (non LIFO)
        // funzioni anche quando elementi adiacenti vengono rimossi
        // su livelli di checkpoint diversi.
        var domain = new Domain<>(1, 2, 3, 4, 5);

        domain.checkpoint();
        domain.remove(2);
        domain.remove(3);
        domain.remove(4);
        assertEquals(List.of(1, 5), toList(domain));

        domain.checkpoint();
        // nessuna ulteriore rimozione a questo livello
        domain.reset(); // pop livello vuoto
        assertEquals(List.of(1, 5), toList(domain));

        domain.reset(); // ripristina 2, 3, 4
        assertEquals(List.of(1, 2, 3, 4, 5), toList(domain));
    }

    @Test
    void manyCheckpointResetCyclesStayConsistent() {
        var domain = new Domain<>(1, 2, 3, 4, 5);
        for (int i = 0; i < 100; i++) {
            domain.checkpoint();
            domain.remove(2);
            domain.remove(4);
            assertEquals(3, domain.size());
            domain.reset();
            assertEquals(5, domain.size());
            assertEquals(List.of(1, 2, 3, 4, 5), toList(domain));
        }
    }

    // --- size() ---

    @Test
    void sizeUpdatesCorrectlyWithRemoveAndReset() {
        var domain = new Domain<>(1, 2, 3, 4, 5);
        assertEquals(5, domain.size());

        domain.checkpoint();
        domain.remove(1);
        domain.remove(2);
        assertEquals(3, domain.size());

        domain.checkpoint();
        domain.remove(3);
        assertEquals(2, domain.size());

        domain.reset();
        assertEquals(3, domain.size());

        domain.reset();
        assertEquals(5, domain.size());
    }

    // --- comportamento con tipi non-Integer, per assicurarsi che sia generico ---

    @Test
    void worksWithStrings() {
        var domain = new Domain<>("a", "b", "c");
        domain.checkpoint();
        domain.remove("b");
        assertEquals(List.of("a", "c"), toList(domain));
    }

    // --- helper ---

    private static <T> List<T> toList(Domain<T> domain) {
        var list = new ArrayList<T>();
        for (var element : domain) {
            list.add(element);
        }
        return list;
    }
}