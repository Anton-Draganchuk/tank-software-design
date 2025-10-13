package ru.mipt.bit.platformer.model;

public final class Game {
    private final Field field;
    private final Tank player;

    public Game(){
        field = new Field(10,10);
        player = new Tank(new Position(1,1), Direction.RIGHT);
        field.add(player);
        field.add(new Tree(new Position(3,1)));
        field.add(new Tree(new Position(5,5)));
    }

    public void render(Renderer r){
        for (var e : field.all()) e.render(r);
        r.flush();
    }

    public Tank player(){ return player; }
    public Field field(){ return field; }
}