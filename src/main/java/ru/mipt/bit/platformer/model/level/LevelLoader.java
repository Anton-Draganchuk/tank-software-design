package ru.mipt.bit.platformer.model.level;

import ru.mipt.bit.platformer.model.Position;
import java.util.List;

public interface LevelLoader {
    LevelData load();

    final class LevelData {
        public final int width, height;
        public final Position playerStart;
        public final List<Position> trees;

        public LevelData(int width, int height, Position playerStart, List<Position> trees) {
            this.width = width; this.height = height;
            this.playerStart = playerStart; this.trees = trees;
        }
    }
}