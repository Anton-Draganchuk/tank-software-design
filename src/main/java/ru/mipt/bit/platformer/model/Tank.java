package ru.mipt.bit.platformer.model;

public final class Tank implements Entity {
    private Position position;
    private Direction direction;
    private final int maxHealth;
    private int health;

    public Tank(Position position, Direction direction, int health) {
        this.position = position;
        this.direction = direction;
        this.maxHealth = Math.max(1, health);
        this.health = this.maxHealth;
    }

    @Override public Position position() { return position; }
    @Override public boolean isBlocking() { return true; }
    @Override public void render(Renderer r) { r.drawTank(this); }

    public Direction direction() { return direction; }
    public void turnLeft()  { direction = direction.left(); }
    public void turnRight() { direction = direction.right(); }
    public int health() { return health; }
    public int maxHealth() { return maxHealth; }

    public void moveIfFree(Field field, Direction d) {
        Position next = position.add(d);
        if (field.isFree(next)) moveTo(next, d);
    }

    public void moveForward(Field field) {
        moveIfFree(field, direction);   // используем текущий direction
    }

    public void moveTo(Position newPosition, Direction newDirection) {
        this.position = newPosition;
        this.direction = newDirection;
    }

    public void setHealth(int newHealth) {
        health = Math.max(0, Math.min(maxHealth, newHealth));
    }

    public void damage(int amount) {
        setHealth(health - Math.max(0, amount));
    }
}
