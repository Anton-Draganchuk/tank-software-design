package ru.mipt.bit.platformer.model.level;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Position;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class TextLevelLoaderTest {

    @Test
    void preservesVisualLayoutFromTopRow() throws Exception {
        List<String> lines = readLines("levels/level1.txt");
        TextLevelLoader loader = new TextLevelLoader("levels/level1.txt");
        LevelLoader.LevelData data = loader.load();

        char[][] reconstructed = new char[data.height][data.width];
        for (int y = 0; y < data.height; y++) {
            for (int x = 0; x < data.width; x++) {
                reconstructed[y][x] = '_';
            }
        }
        for (Position tree : data.trees) {
            reconstructed[data.height - tree.y() - 1][tree.x()] = 'T';
        }
        reconstructed[data.height - data.playerStart.y() - 1][data.playerStart.x()] = 'X';

        for (int y = 0; y < data.height; y++) {
            assertThat(new String(reconstructed[y])).isEqualTo(lines.get(y));
        }
    }

    private static List<String> readLines(String resource) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(TextLevelLoaderTest.class.getClassLoader().getResourceAsStream(resource))))) {
            List<String> lines = new ArrayList<>();
            String s;
            while ((s = br.readLine()) != null) {
                if (!s.trim().isEmpty()) lines.add(s.trim());
            }
            return lines;
        }
    }
}
