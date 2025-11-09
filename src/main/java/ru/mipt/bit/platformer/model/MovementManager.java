package ru.mipt.bit.platformer.model;

import java.util.HashSet;
import java.util.Set;

public final class MovementManager {
    private final Field field;
    private final Set<Position> lockedPositions = new HashSet<>();

    public MovementManager(Field field) {
        this.field = field;
    }

    public boolean tryMove(Tank tank, Direction direction) {
        Position from = tank.position();
        if (lockedPositions.contains(from)) return false;

        Position to = from.add(direction);
        if (lockedPositions.contains(to)) return false;

        if (!field.isFree(to)) return false;

        tank.moveTo(to, direction);
        lockedPositions.add(from);
        lockedPositions.add(to);
        return true;
    }
}
