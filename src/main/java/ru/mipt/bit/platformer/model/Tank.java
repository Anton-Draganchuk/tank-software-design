package ru.mipt.bit.platformer.model;

public final class Tank implements Entity {
    private Position pos;
    private Direction dir;

    public Tank(Position start, Direction dir){ this.pos = start; this.dir = dir; }

    @Override public Position position(){ return pos; }
    @Override public boolean isBlocking(){ return true; }
    public Direction direction(){ return dir; }

    public void turnLeft(){ dir = dir.left(); }
    public void turnRight(){ dir = dir.right(); }
    public void moveForward(Field field){
        Position next = pos.add(dir);
        if(field.isFree(next)) pos = next;
    }

    @Override public void render(Renderer r){ r.drawTank(pos, dir); }
}