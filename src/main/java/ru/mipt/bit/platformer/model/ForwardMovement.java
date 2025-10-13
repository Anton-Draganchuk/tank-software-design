package ru.mipt.bit.platformer.model;

public final class ForwardMovement implements MovementStrategy {
    private final Direction direction;

    public ForwardMovement(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void move(Tank tank, Field field) {
        tank.moveIfFree(field, direction);
    }
}