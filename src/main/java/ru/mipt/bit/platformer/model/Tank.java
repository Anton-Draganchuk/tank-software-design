package ru.mipt.bit.platformer.model;

public final class Tank implements Entity {
    private Position position;
    private Direction direction;

    public Tank(Position position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    @Override public Position position() { return position; }
    @Override public boolean isBlocking() { return false; }
    @Override public void render(Renderer r) { r.drawTank(position, direction); }

    public Direction direction() { return direction; }
    public void turnLeft()  { direction = direction.left(); }
    public void turnRight() { direction = direction.right(); }

    public void moveIfFree(Field field, Direction d) {
        Position next = new Position(position.x() + d.dx(), position.y() + d.dy());
        if (field.isFree(next)) { position = next; direction = d; }
    }

    public void moveForward(Field field) {
        moveIfFree(field, direction);   // используем текущий direction
    }
}