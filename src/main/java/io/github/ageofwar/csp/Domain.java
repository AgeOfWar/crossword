package io.github.ageofwar.csp;

import java.util.*;
import java.util.function.Predicate;

public class Domain<T> extends AbstractCollection<T> {
    private final T[] elements;
    private final int[] nexts;
    private final int[] previous;
    private final boolean[] removed;
    private int startIndex;
    private int size;

    private final HashMap<T, Integer> indices;
    private final ArrayDeque<List<Integer>> checkpoints;

    public Domain(T... elements) {
        this.elements = elements;
        indices = new HashMap<>();
        checkpoints = new ArrayDeque<>();
        nexts = new int[elements.length];
        previous = new int[elements.length];
        removed = new boolean[elements.length];
        startIndex = 0;
        size = elements.length;
        for (var i = 0; i < elements.length; i++) {
            indices.put(elements[i], i);
            nexts[i] = i + 1;
            previous[i] = i - 1;
        }
    }

    @Override
    public boolean remove(Object element) {
        var index = indices.get(element);
        if (index == null) return false;
        return remove((int) index);
    }

    @Override
    public boolean removeIf(Predicate<? super T> element) {
        var removed = false;
        for (var i = startIndex; i < elements.length; i = nexts[i]) {
            if (element.test(elements[i])) {
                remove(i);
                removed = true;
            }
        }
        return removed;
    }

    private boolean remove(int index) {
        if (removed[index]) return false;
        checkpoints.peek().add(index);
        if (previous[index] != -1) nexts[previous[index]] = nexts[index];
        else startIndex = nexts[index];
        if (nexts[index] != elements.length) previous[nexts[index]] = previous[index];
        removed[index] = true;
        size--;
        return true;
    }

    public void checkpoint() {
        checkpoints.push(new ArrayList<>());
    }

    public void reset() {
        var checkpoint = checkpoints.pop();
        for (var index : checkpoint) {
            if (previous[index] != -1) nexts[previous[index]] = index;
            else startIndex = index;
            if (nexts[index] != elements.length) previous[nexts[index]] = index;
            removed[index] = false;
        }
        size += checkpoint.size();
    }

    @Override
    public boolean contains(Object o) {
        var index = indices.get(o);
        if (index == null) return false;
        return !removed[index];
    }

    public T first() {
        if (startIndex == elements.length) return null;
        return elements[startIndex];
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int index = startIndex;

            @Override
            public boolean hasNext() {
                return index < elements.length;
            }

            @Override
            public T next() {
                var element = elements[index];
                index = nexts[index];
                return element;
            }
        };
    }
}
