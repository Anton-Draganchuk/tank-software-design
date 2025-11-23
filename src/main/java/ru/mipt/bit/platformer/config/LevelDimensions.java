package ru.mipt.bit.platformer.config;

public final class LevelDimensions {
    private final int width;
    private final int height;

    public LevelDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
