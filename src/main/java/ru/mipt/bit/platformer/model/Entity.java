package ru.mipt.bit.platformer.model;

public interface Entity {
    Position position();
    boolean isBlocking();
    void render(Renderer r);
}