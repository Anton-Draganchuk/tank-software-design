package ru.mipt.bit.platformer.model.control;

import ru.mipt.bit.platformer.model.MovementManager;
import ru.mipt.bit.platformer.model.command.Command;

public interface TankController {
    Command nextCommand(MovementManager movementManager, float deltaTime);
}
