package ru.mipt.bit.platformer.model;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FieldTest {
    @Test void inBoundsAndBlocking() {
        Field f = new Field(3,3);
        f.add(new Tree(new Position(1,1)));
        assertThat(f.inBounds(new Position(0,0))).isTrue();
        assertThat(f.isFree(new Position(1,1))).isFalse();   // дерево
        assertThat(f.isFree(new Position(2,2))).isTrue();
        assertThat(f.isFree(new Position(5,5))).isFalse();   // вне поля
    }
}