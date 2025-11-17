package ru.mipt.bit.platformer.model;

public interface FieldObserver {
    void entityAdded(Entity entity);
    void entityRemoved(Entity entity);
}
