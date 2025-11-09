package ru.mipt.bit.platformer.model.control;

import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.model.MovementManager;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.command.Command;
import ru.mipt.bit.platformer.model.command.MoveTankCommand;

public final class PlayerTankController implements TankController {
    private final Tank tank;
    private final InputHandler input;

    public PlayerTankController(Tank tank, InputHandler input) {
        this.tank = tank;
        this.input = input;
    }

    @Override
    public Command nextCommand(MovementManager movementManager, float deltaTime) {
        Direction direction = input.readDirection();
        if (direction == null) return null;
        return new MoveTankCommand(tank, direction, movementManager);
    }
}
