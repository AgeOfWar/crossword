package io.github.ageofwar;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws IOException {
        var dictionary = Dictionary.fromFile("words.italian.txt");
        var crossword = Crossword.fromString("""
            #.##.##.#
            .........
            #.....#..
            #..#.#...
            #.....#..
            .........
        """);
        //System.out.println(crossword);
        //var start = System.nanoTime();
        //crossword.fill(dictionary);
        //var elapsed = System.nanoTime() - start;
        //System.out.println(crossword);
        //System.out.println(elapsed / 1000000000 + "s" + (elapsed % 1000000000) / 1000000 + "ms");

        var mainThread = Thread.currentThread();
        var threads = new Thread[12];
        var count = new AtomicInteger(0);
        for (int i = 0; i < 12; i++) {
            threads[i] = new Thread(() -> {
                var copy = crossword.copy();
                copy.fill(dictionary);
                System.out.println(copy);
                if (count.incrementAndGet() == 12) {
                    mainThread.interrupt();
                }
            });
            threads[i].start();
        }
        try {
            Thread.sleep(60000L);
        } catch (InterruptedException e) {
            System.out.println("Done");
        }
        System.out.println(count.get() + " crosswords filled / 12");
        for (var thread : threads) {
            thread.interrupt();
        }
    }
}