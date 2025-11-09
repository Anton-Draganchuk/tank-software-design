package ru.mipt.bit.platformer.model;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private final int width, height;
    private final List<Entity> entities = new ArrayList<>();

    public Field(int width, int height){ this.width = width; this.height = height; }

    public void add(Entity e){ entities.add(e); }

    public boolean inBounds(Position p){
        return p.x() >= 0 && p.y() >= 0 && p.x() < width && p.y() < height;
    }

    public boolean isFree(Position p){
        if(!inBounds(p)) return false;
        for (Entity e : entities)
            if (e.isBlocking() && e.position().equals(p)) return false;
        return true;
    }

    public List<Entity> all(){ return List.copyOf(entities); }
}