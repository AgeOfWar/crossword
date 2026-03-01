package io.github.ageofwar;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        var dictionary = Dictionary.fromFile("words.italian.txt");
        var seed = System.currentTimeMillis();//-74039664346287448L;
        System.out.println("Seed: " + seed);
        var random = new Random(seed);
        var crosswordFiller = new NaiveCrosswordFiller(dictionary, random);

         var crossword = Crossword.fromString("""
                     .........########
                     ....#....########
                     ...#.....########
                     ..#......########
                     .#......#########
                     #......#.########
                     ......#..########
                     .....#...########
                     ....#....########
                     ...#.....########
                     ..#..............
                     .#......#......#.
                     .......#......#..
                     ......#......#...
                     .....#......#....
                     ....#......#.....
                     ...#......#......
                 """);
        // var crossword = Crossword.fromString("""
        //             ..........###.#.......
        //             ......#....#..........
        //             ...##..##........#...#
        //             ..#......#......#.....
        //             ..#...............#...
        //             .#.............##.....
        //             #...................#.
        //             .................#....
        //             .#.....#....#........#
        //             ...#...#...#.##.......
        //             .....#..#.#..........#
        //             ....#..........##.#...
        //         """);
        System.out.println(crossword);
        var start = System.nanoTime();
        var filled = crosswordFiller.fill(crossword);
        var elapsed = System.nanoTime() - start;
        System.out.println(filled);
        System.out.println(elapsed / 1000000000 + "s" + (elapsed % 1000000000) / 1000000 + "ms");

        //while (true) {
        //    seed = new Random().nextLong();
        //    System.out.println("Seed: " + seed);
        //    random = new Random(seed);
        //    crosswordFiller = new CspCrosswordFiller(dictionary, random);
        //    System.out.println("started at " + ZonedDateTime.now());
        //    filled = crosswordFiller.fill(crossword);
        //    System.out.println(filled);
        //}
    }
}