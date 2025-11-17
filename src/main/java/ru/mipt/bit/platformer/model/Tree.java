package ru.mipt.bit.platformer.model;

public final class Tree implements Entity {
    private final Position position;
    public Tree(Position position) { this.position = position; }

    @Override public Position position() { return position; }
    @Override public boolean isBlocking() { return true; }
    @Override public void render(Renderer r) { r.drawTree(position); }
}