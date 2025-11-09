package ru.mipt.bit.platformer.model.level;

import ru.mipt.bit.platformer.model.Position;
import java.util.*;

public final class RandomLevelLoader implements LevelLoader {
    private final int width, height, treeCount;
    private final Random rnd;

    public RandomLevelLoader(int width, int height, int treeCount, long seed) {
        this.width = width; this.height = height; this.treeCount = treeCount;
        this.rnd = new Random(seed);
    }

    @Override
    public LevelData load() {
        Set<Position> used = new HashSet<>();
        Position start = null;
        while (start == null) {
            Position p = new Position(rnd.nextInt(width), rnd.nextInt(height));
            if (used.add(p)) start = p;
        }
        List<Position> trees = new ArrayList<>();
        while (trees.size() < treeCount) {
            Position p = new Position(rnd.nextInt(width), rnd.nextInt(height));
            if (used.add(p)) trees.add(p);
        }
        return new LevelData(width, height, start, trees);
    }
}