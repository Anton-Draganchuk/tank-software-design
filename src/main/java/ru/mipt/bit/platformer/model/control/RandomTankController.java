package ru.mipt.bit.platformer.model.control;

import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementManager;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WeaponManager;
import ru.mipt.bit.platformer.model.command.Command;
import ru.mipt.bit.platformer.model.command.MoveTankCommand;
import ru.mipt.bit.platformer.model.command.ShootTankCommand;

import java.util.Random;

public final class RandomTankController implements TankController {
    private static final Direction[] DIRECTIONS = Direction.values();

    private final Tank tank;
    private final Random random;
    private final float moveInterval;
    private final float shootProbability;
    private float accumulated;
    private final WeaponManager weaponManager;

    public RandomTankController(Tank tank, Random random, float moveIntervalSeconds, WeaponManager weaponManager, float shootProbability) {
        this.tank = tank;
        this.random = random;
        this.moveInterval = Math.max(0.05f, moveIntervalSeconds);
        this.weaponManager = weaponManager;
        this.shootProbability = clampProbability(shootProbability);
    }

    @Override
    public Command nextCommand(MovementManager movementManager, float deltaTime) {
        accumulated += deltaTime;
        if (accumulated < moveInterval) return null;
        accumulated -= moveInterval;
        if (random.nextFloat() < shootProbability) {
            return new ShootTankCommand(tank, weaponManager);
        }
        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        return new MoveTankCommand(tank, direction, movementManager);
    }

    private static float clampProbability(float value) {
        if (Float.isNaN(value)) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, value));
    }
}
