package ru.mipt.bit.platformer.model;

public enum Direction {
    UP(0, 1, 90f),
    RIGHT(1, 0, 0f),
    DOWN(0, -1, -90f),
    LEFT(-1, 0, 180f);

    private final int dx, dy;
    private final float rotationDeg;

    Direction(int dx, int dy, float rotationDeg) {
        this.dx = dx;
        this.dy = dy;
        this.rotationDeg = rotationDeg;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }
    public float rotationDeg() { return rotationDeg; }

    public Direction left() {
        switch (this) {
            case UP:    return LEFT;
            case LEFT:  return DOWN;
            case DOWN:  return RIGHT;
            case RIGHT: return UP;
        }
        throw new IllegalStateException("Unexpected: " + this);
    }

    public Direction right() {
        switch (this) {
            case UP:    return RIGHT;
            case RIGHT: return DOWN;
            case DOWN:  return LEFT;
            case LEFT:  return UP;
        }
        throw new IllegalStateException("Unexpected: " + this);
    }
}