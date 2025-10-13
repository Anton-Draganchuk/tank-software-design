package ru.mipt.bit.platformer.model;

public interface Renderer {
    void drawTank(Position p, Direction d);
    void drawTree(Position p);
    void flush();
}