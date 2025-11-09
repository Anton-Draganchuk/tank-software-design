package ru.mipt.bit.platformer.model;

public abstract class StaticEntity implements Entity {
    protected final Position pos;
    protected StaticEntity(Position pos){ this.pos = pos; }
    @Override public Position position(){ return pos; }
    @Override public boolean isBlocking(){ return true; }
}