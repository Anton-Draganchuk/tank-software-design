package ru.mipt.bit.platformer.model;

public interface Renderer {
    void drawTank(Tank tank);
    void drawTree(Position p);
    void drawBullet(Position position, Direction direction);
    void flush();
}
