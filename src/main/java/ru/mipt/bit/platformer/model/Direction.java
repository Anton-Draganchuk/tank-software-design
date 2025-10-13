package ru.mipt.bit.platformer.model;

public enum Direction {
    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() { return dx; }
    public int dy() { return dy; }

    public Direction left() {
        switch (this) {
            case UP:    return LEFT;
            case LEFT:  return DOWN;
            case DOWN:  return RIGHT;
            case RIGHT: return UP;
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

    public Direction right() {
        switch (this) {
            case UP:    return RIGHT;
            case RIGHT: return DOWN;
            case DOWN:  return LEFT;
            case LEFT:  return UP;
        }
        throw new IllegalStateException("Unexpected value: " + this);
    }

    public float rotationDeg() {
        switch (this) {
            case UP:    return 90f;
            case RIGHT: return 0f;
            case DOWN:  return 270f;
            case LEFT:  return 180f;
            default:    throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}