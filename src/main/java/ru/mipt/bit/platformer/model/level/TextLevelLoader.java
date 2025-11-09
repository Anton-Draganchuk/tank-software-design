package ru.mipt.bit.platformer.model.level;

import ru.mipt.bit.platformer.model.Position;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/** Читает карту из resources по относительному пути, например "levels/level1.txt". */
public final class TextLevelLoader implements LevelLoader {
    private final String resourcePath;

    public TextLevelLoader(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public LevelData load() {
        List<String> lines = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String s;
                while ((s = br.readLine()) != null) {
                    s = s.trim();
                    if (!s.isEmpty()) lines.add(s);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load level: " + resourcePath, e);
        }
        if (lines.isEmpty()) throw new IllegalStateException("Empty level file: " + resourcePath);

        int height = lines.size();
        int width = lines.get(0).length();
        for (String l : lines) if (l.length() != width)
            throw new IllegalStateException("Non-rectangular map in " + resourcePath);

        Position player = null;
        List<Position> trees = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            int realY = height - y - 1;
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                switch (c) {
                    case 'T': trees.add(new Position(x, realY)); break;
                    case 'X':
                        if (player != null) throw new IllegalStateException("Multiple X in " + resourcePath);
                        player = new Position(x, realY);
                        break;
                    case '_': break;
                    default: throw new IllegalStateException("Unexpected char '" + c + "' at ("+x+","+y+")");
                }
            }
        }
        if (player == null) throw new IllegalStateException("No player start (X) in " + resourcePath);
        return new LevelData(width, height, player, trees);
    }
}
