package ru.mipt.bit.platformer.model.control;

import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementManager;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WeaponManager;
import ru.mipt.bit.platformer.model.command.Command;
import ru.mipt.bit.platformer.model.command.MoveTankCommand;
import ru.mipt.bit.platformer.model.command.ShootTankCommand;

public final class PlayerTankController implements TankController {
    private final Tank tank;
    private final InputHandler input;
    private final WeaponManager weaponManager;

    public PlayerTankController(Tank tank, InputHandler input, WeaponManager weaponManager) {
        this.tank = tank;
        this.input = input;
        this.weaponManager = weaponManager;
    }

    @Override
    public Command nextCommand(MovementManager movementManager, float deltaTime) {
        if (input.isShootingRequested()) {
            return new ShootTankCommand(tank, weaponManager);
        }
        Direction direction = input.readDirection();
        if (direction == null) return null;
        return new MoveTankCommand(tank, direction, movementManager);
    }
}
