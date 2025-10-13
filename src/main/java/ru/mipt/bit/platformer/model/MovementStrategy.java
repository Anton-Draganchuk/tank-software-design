package ru.mipt.bit.platformer.model;

public interface MovementStrategy {
    void move(Tank tank, Field field);
}