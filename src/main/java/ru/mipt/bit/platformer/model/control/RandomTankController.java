package ru.mipt.bit.platformer.model.control;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementManager;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.command.Command;
import ru.mipt.bit.platformer.model.command.MoveTankCommand;

import java.util.Random;

public final class RandomTankController implements TankController {
    private static final Direction[] DIRECTIONS = Direction.values();

    private final Tank tank;
    private final Random random;
    private final float moveInterval;
    private float accumulated;

    public RandomTankController(Tank tank, Random random, float moveIntervalSeconds) {
        this.tank = tank;
        this.random = random;
        this.moveInterval = Math.max(0.05f, moveIntervalSeconds);
    }

    @Override
    public Command nextCommand(MovementManager movementManager, float deltaTime) {
        accumulated += deltaTime;
        if (accumulated < moveInterval) return null;
        accumulated -= moveInterval;
        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        return new MoveTankCommand(tank, direction, movementManager);
    }
}
