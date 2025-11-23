package ru.mipt.bit.platformer.model;

import java.util.HashMap;
import java.util.Map;

public final class WeaponManager {
    private static final float MIN_RELOAD = 0.01f;

    private final Field field;
    private final int bulletDamage;
    private final float bulletSpeed;
    private final float reloadTime;
    private final Map<Tank, Float> cooldowns = new HashMap<>();

    public WeaponManager(Field field, int bulletDamage, float bulletSpeed, float reloadTime) {
        this.field = field;
        this.bulletDamage = Math.max(0, bulletDamage);
        this.bulletSpeed = Math.max(0.1f, bulletSpeed);
        this.reloadTime = Math.max(MIN_RELOAD, reloadTime);
    }

    public void registerTank(Tank tank) {
        cooldowns.putIfAbsent(tank, 0f);
    }

    public void unregisterTank(Tank tank) {
        cooldowns.remove(tank);
    }

    public void update(float deltaTime) {
        if (cooldowns.isEmpty()) {
            return;
        }
        for (Map.Entry<Tank, Float> entry : cooldowns.entrySet()) {
            float updated = Math.max(0f, entry.getValue() - deltaTime);
            entry.setValue(updated);
        }
    }

    public void tryShoot(Tank tank) {
        Float cooldown = cooldowns.get(tank);
        if (cooldown == null) {
            registerTank(tank);
            cooldown = 0f;
        }
        if (cooldown > 0f) {
            return;
        }
        Position spawn = tank.position().add(tank.direction());
        if (!field.inBounds(spawn)) {
            return;
        }
        Bullet bullet = new Bullet(field, spawn, tank.direction(), bulletDamage, bulletSpeed);
        field.add(bullet);
        bullet.resolveImmediateCollision();
        cooldowns.put(tank, reloadTime);
    }
}
