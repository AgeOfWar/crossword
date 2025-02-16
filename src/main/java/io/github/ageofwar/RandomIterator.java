package io.github.ageofwar;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class RandomIterator {
    private RandomIterator() {
    }

    public static <T> Iterator<T> randomStartIterator(List<T> list, Random random) {
        return new Iterator<>() {
            final int start = list.isEmpty() ? -1 : random.nextInt(list.size());
            int i = start;

            @Override
            public boolean hasNext() {
                return i != -1;
            }

            @Override
            public T next() {
                if (i == -1) return null;
                var value = list.get(i);
                i = (i + 1) % list.size();
                if (i == start) i = -1;
                return value;
            }
        };
    }
}
