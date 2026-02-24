package io.github.ageofwar;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var dictionary = Dictionary.fromFile("words.italian.txt");
        var crosswordFiller = new NaiveCrosswordFiller(dictionary);

        // var crossword = Crossword.fromString("""
        //     .........########
        //     ....#....########
        //     ...#.....########
        //     ..#......########
        //     .#......#########
        //     #......#.########
        //     ......#..########
        //     .....#...########
        //     ....#....########
        //     ...#.....########
        //     ..#..............
        //     .#......#......#.
        //     .......#......#..
        //     ......#......#...
        //     .....#......#....
        //     ....#......#.....
        //     ...#......#......
        // """);
        var crossword = Crossword.fromString("""
            .....
            .....
            .....
            .....
            .....
            .....
            .....
        """);
        System.out.println(crossword);
        var start = System.nanoTime();
        var filled = crosswordFiller.fill(crossword);
        var elapsed = System.nanoTime() - start;
        System.out.println(filled);
        System.out.println(elapsed / 1000000000 + "s" + (elapsed % 1000000000) / 1000000 + "ms");
    }
}