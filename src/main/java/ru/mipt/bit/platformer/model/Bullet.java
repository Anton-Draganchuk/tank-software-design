package ru.mipt.bit.platformer.model;

import java.util.List;

public final class Bullet implements LivingEntity {
    private static final float MIN_SPEED = 0.1f;

    private final Field field;
    private Position position;
    private final Direction direction;
    private final int damage;
    private final float moveInterval;
    private float accumulated;
    private boolean active = true;

    public Bullet(Field field, Position position, Direction direction, int damage, float tilesPerSecond) {
        this.field = field;
        this.position = position;
        this.direction = direction;
        this.damage = Math.max(0, damage);
        float speed = Math.max(MIN_SPEED, tilesPerSecond);
        this.moveInterval = 1f / speed;
    }

    @Override
    public Position position() {
        return position;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public void render(Renderer r) {
        r.drawBullet(position, direction);
    }

    @Override
    public void live(float deltaTime) {
        if (!active) {
            return;
        }
        accumulated += deltaTime;
        while (accumulated >= moveInterval && active) {
            accumulated -= moveInterval;
            advance();
        }
    }

    public void resolveImmediateCollision() {
        if (!active) {
            return;
        }
        checkCollision();
    }

    void destroyOnCollision() {
        if (!active) {
            return;
        }
        active = false;
        field.remove(this);
    }

    private void advance() {
        Position next = position.add(direction);
        if (!field.inBounds(next)) {
            destroyOnCollision();
            return;
        }
        position = next;
        checkCollision();
    }

    private void checkCollision() {
        List<Entity> entities = field.entitiesAt(position);
        for (Entity entity : entities) {
            if (entity == this || !active) {
                continue;
            }
            if (entity instanceof Bullet other) {
                other.destroyOnCollision();
                destroyOnCollision();
                return;
            }
            if (entity instanceof Tank tank) {
                if (damage > 0) {
                    tank.damage(damage);
                }
                if (tank.health() <= 0) {
                    field.remove(tank);
                }
                destroyOnCollision();
                return;
            }
            if (entity.isBlocking()) {
                destroyOnCollision();
                return;
            }
        }
    }
}
