package ru.mipt.bit.platformer.model;

public record Position(int x, int y) {
    public Position add(Direction d){ return new Position(x + d.dx(), y + d.dy()); }
}