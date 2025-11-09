package ru.mipt.bit.platformer.model;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class TankTest {
    @Test void movesOnlyIfFree_andTurns() {
        Field f = new Field(5,5);
        Tank t = new Tank(new Position(1,1), Direction.RIGHT);
        f.add(t);
        f.add(new Tree(new Position(2,1)));            // препятствие впереди
        t.moveForward(f);                               // не двинется
        assertThat(t.position()).isEqualTo(new Position(1,1));
        t.turnLeft();                                   // теперь вверх
        t.moveForward(f);
        assertThat(t.position()).isEqualTo(new Position(1,2));
        assertThat(t.direction()).isEqualTo(Direction.UP);
    }
}