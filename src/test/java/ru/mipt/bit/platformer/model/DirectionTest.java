package ru.mipt.bit.platformer.model;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DirectionTest {
    @Test void deltasAndRotation() {
        assertThat(Direction.RIGHT.dx()).isEqualTo(1);
        assertThat(Direction.RIGHT.dy()).isEqualTo(0);
        assertThat(Direction.UP.rotationDeg()).isIn(90f, 90.0f);
    }
}