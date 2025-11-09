package ru.mipt.bit.platformer.model.command;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementManager;
import ru.mipt.bit.platformer.model.Tank;

public final class MoveTankCommand implements Command {
    private final Tank tank;
    private final Direction direction;
    private final MovementManager movementManager;

    public MoveTankCommand(Tank tank, Direction direction, MovementManager movementManager) {
        this.tank = tank;
        this.direction = direction;
        this.movementManager = movementManager;
    }

    @Override
    public void execute() {
        movementManager.tryMove(tank, direction);
    }
}
