package io.github.ageofwar;


import java.util.HashMap;

public class ScoreHeap<T> {
    private Entry<T>[] values;
    private int size;

    private final HashMap<T, Integer> indexMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public ScoreHeap(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Capacity must be positive");
        values = new Entry[capacity];
        size = 0;
    }

    public ScoreHeap() {
        this(10);
    }

    public double getScore(T value) {
        var index = indexMap.get(value);
        if (index == null) return Double.NaN;
        return values[index].score;
    }

    public void set(T value, double score) {
        var index = indexMap.get(value);
        if (index == null) {
            add(value, score);
        } else {
            var oldScore = values[index].score;
            values[index].score = score;
            if (score < oldScore) {
                heapifyUp(index);
            } else if (score > oldScore) {
                heapifyDown(index);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void add(T value, double score) {
        if (size >= values.length) {
            var newValues = new Entry[values.length * 2];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
        values[size] = new Entry<>(score, value);
        indexMap.put(value, size);
        size++;
        heapifyUp(size - 1);
    }

    public Entry<T> poll() {
        if (size == 0) return null;
        var value = values[0];
        indexMap.remove(value.value);
        size--;
        if (size > 0) {
            var movedValue = values[size];
            values[0] = movedValue;
            indexMap.put(movedValue.value, 0);
            heapifyDown(0);
        }
        values[size] = null;
        return value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            var parentIndex = (index - 1) / 2;
            if (values[index].score < values[parentIndex].score) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        while (true) {
            var leftIndex = 2 * index + 1;
            var rightIndex = 2 * index + 2;
            if (leftIndex >= size) break;
            var minIndex = leftIndex;
            if (rightIndex < size && values[rightIndex].score < values[leftIndex].score) {
                minIndex = rightIndex;
            }
            if (values[index].score > values[minIndex].score) {
                swap(index, minIndex);
                index = minIndex;
            } else {
                break;
            }
        }
    }

    public ScoreHeap<T> copy() {
        var clone = new ScoreHeap<T>(size);
        clone.size = size;
        System.arraycopy(values, 0, clone.values, 0, size);
        clone.indexMap.putAll(indexMap);
        return clone;
    }

    private void swap(int i, int j) {
        var temp = values[i];
        values[i] = values[j];
        values[j] = temp;
        indexMap.put(values[i].value, i);
        indexMap.put(values[j].value, j);
    }

    public static class Entry<E> {
        private double score;
        private E value;

        public Entry(double score, E value) {
            this.score = score;
            this.value = value;
        }

        public double score() {
            return score;
        }

        public E value() {
            return value;
        }
    }
}
