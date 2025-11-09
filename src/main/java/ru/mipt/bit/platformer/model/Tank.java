package ru.mipt.bit.platformer.model;

public final class Tank implements Entity {
    private Position position;
    private Direction direction;

    public Tank(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    @Override public Position position() { return position; }
    @Override public boolean isBlocking() { return true; }
    @Override public void render(Renderer r) { r.drawTank(position, direction); }

    public Direction direction() { return direction; }
    public void turnLeft()  { direction = direction.left(); }
    public void turnRight() { direction = direction.right(); }

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
}
