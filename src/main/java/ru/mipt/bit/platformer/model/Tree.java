package ru.mipt.bit.platformer.model;

public final class Tree extends StaticEntity {
    public Tree(Position pos){ super(pos); }
    @Override public void render(Renderer r){ r.drawTree(pos); }
}