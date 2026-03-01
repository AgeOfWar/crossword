package io.github.ageofwar;

import java.util.*;

public class DictionaryConstrainedCrossword {
    public static final BitSet BLACK = null;

    private final Crossword crossword;
    private final Dictionary dictionary;

    private BitSet[][] hints;
    private Map<PositionDirectionLength, List<byte[]>> candidatesCache = new HashMap<>();

    private final Stack<Placement> placements = new Stack<>();

    public static DictionaryConstrainedCrossword fromCrossword(Dictionary dictionary, Crossword crossword) {
        var hints = new BitSet[crossword.height()][crossword.width()];
        for (int i = 0; i < hints.length; i++) {
            for (int j = 0; j < hints[i].length; j++) {
                switch (crossword.get(new Position(j, i))) {
                    case Crossword.BLACK -> hints[i][j] = BLACK;
                    case Crossword.EMPTY -> {
                        hints[i][j] = new BitSet(26);
                        hints[i][j].set(0, 26);
                    }
                    default -> {
                        hints[i][j] = new BitSet(26);
                        hints[i][j].set(crossword.get(new Position(j, i)));
                    }
                }
            }
        }
        var dcc = new DictionaryConstrainedCrossword(dictionary, crossword, hints);
        var queue = new ArrayDeque<>(crossword.getPositions(2));
        dcc.propagate(queue);
        return dcc;
    }

    private DictionaryConstrainedCrossword(Dictionary dictionary, Crossword crossword, BitSet[][] hints) {
        this.dictionary = dictionary;
        this.crossword = crossword;
        this.hints = hints;
    }

    public BitSet getHint(Position position) {
        return hints[position.y()][position.x()];
    }

    public BitSet[] getHints(PositionDirectionLength position) {
        var result = new BitSet[position.length()];
        for (var i = 0; i < position.length(); i++) {
            result[i] = getHint(position.getPosition(i));
        }
        return result;
    }

    public byte[] get(PositionDirectionLength position) {
        return crossword.get(position);
    }

    public List<PositionDirectionLength> set(PositionDirectionLength position, byte[] pattern) {
        placements.add(new Placement(position, crossword.get(position), clone(hints), new HashMap<>(candidatesCache)));
        candidatesCache.put(position, List.of(pattern));
        var affected = setHints(position, BitSetPattern.fromPattern(pattern));
        crossword.setAndGetAffected(position, pattern, 2);
        return propagate(new ArrayDeque<>(affected));
    }

    private BitSet[][] clone(BitSet[][] hints) {
        var clone = new BitSet[hints.length][];
        for (int i = 0; i < hints.length; i++) {
            clone[i] = new BitSet[hints[i].length];
            for (int j = 0; j < hints[i].length; j++) {
                if (hints[i][j] != null) {
                    clone[i][j] = hints[i][j];
                }
            }
        }
        return clone;
    }

    public void undo() {
        if (placements.isEmpty()) return;
        var placement = placements.pop();
        crossword.set(placement.position, placement.pattern);
        hints = placement.hints;
        candidatesCache = placement.candidatesCache;
    }

    private List<PositionDirectionLength> propagate(Queue<PositionDirectionLength> queue) {
        var affectedPositions = new ArrayList<PositionDirectionLength>();
        while (!queue.isEmpty()) {
            var position = queue.poll();
            var hints = getHints(position);

            var candidates = dictionary.fromPattern(hints);
            candidatesCache.put(position, candidates);
            var newPattern = BitSetPattern.fromWords(hints.length, candidates);

            if (Arrays.equals(hints, newPattern)) continue;
            affectedPositions.add(position);
            var affected = setHints(position, newPattern);
            queue.addAll(affected);
        }
        return affectedPositions;
    }

    private double score(DictionaryConstrainedCrossword crossword, PositionDirectionLength position) {
        return (double) crossword.getCandidates(position).size() / position.length();
    }

    private List<PositionDirectionLength> setHints(PositionDirectionLength position, BitSet[] pattern) {
        var affectedPositions = new ArrayList<PositionDirectionLength>();
        for (var i = 0; i < position.length(); i++) {
            var pos = position.getPosition(i);
            if (crossword.get(pos) != Crossword.EMPTY) continue;
            var oldPattern = getHint(pos);
            if (!oldPattern.equals(pattern[i])) {
                hints[pos.y()][pos.x()] = pattern[i];
                var affected = crossword.getPosition(pos, position.direction().opposite());
                if (affected.length() > 1) affectedPositions.add(affected);
            }
        }
        return affectedPositions;
    }

    public List<byte[]> getCandidates(PositionDirectionLength position) {
        return candidatesCache.get(position);
    }

    public PositionDirectionLength mostConstrainedPosition() {
        return candidatesCache.entrySet().stream()
                .filter(e -> {
                    for (var cell : crossword.get(e.getKey())) {
                        if (cell == Crossword.EMPTY) return true;
                    }
                    return false;
                })
                .min(Comparator.comparingDouble(e -> (double) e.getValue().size() / e.getKey().length()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Crossword crossword() {
        return crossword;
    }

    public int width() {
        return crossword.width();
    }

    public int height() {
        return crossword.height();
    }

    @Override
    public String toString() {
        return crossword().toString();
    }

    private record Placement(PositionDirectionLength position, byte[] pattern, BitSet[][] hints, Map<PositionDirectionLength, List<byte[]>> candidatesCache) {
    }
}
