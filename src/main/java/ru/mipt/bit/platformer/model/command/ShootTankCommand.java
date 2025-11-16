package ru.mipt.bit.platformer.model.command;

import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.WeaponManager;

public final class ShootTankCommand implements Command {
    private final Tank tank;
    private final WeaponManager weaponManager;

    public ShootTankCommand(Tank tank, WeaponManager weaponManager) {
        this.tank = tank;
        this.weaponManager = weaponManager;
    }

    @Override
    public void execute() {
        weaponManager.tryShoot(tank);
    }
}
