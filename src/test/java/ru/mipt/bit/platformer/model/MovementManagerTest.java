package ru.mipt.bit.platformer.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovementManagerTest {
    @Test
    void locksStartAndDestinationWithinFrame() {
        Field field = new Field(5, 5);
        Tank moving = new Tank(new Position(1, 1), Direction.UP, 100);
        Tank neighbor = new Tank(new Position(1, 0), Direction.UP, 100);
        field.add(moving);
        field.add(neighbor);

        MovementManager manager = new MovementManager(field);
        assertThat(manager.tryMove(moving, Direction.UP)).isTrue();
        assertThat(moving.position()).isEqualTo(new Position(1, 2));

        // сосед не может поехать в клетку, которую танк только что покинул
        assertThat(manager.tryMove(neighbor, Direction.UP)).isFalse();
        assertThat(neighbor.position()).isEqualTo(new Position(1, 0));

        // тот же танк не сможет поехать второй раз в этот кадр
        assertThat(manager.tryMove(moving, Direction.UP)).isFalse();
        assertThat(moving.position()).isEqualTo(new Position(1, 2));
    }

    @Test
    void preventsMovingOutOfBounds() {
        Field field = new Field(2, 2);
        Tank tank = new Tank(new Position(0, 0), Direction.LEFT, 100);
        field.add(tank);

        MovementManager manager = new MovementManager(field);
        assertThat(manager.tryMove(tank, Direction.LEFT)).isFalse();
        assertThat(tank.position()).isEqualTo(new Position(0, 0));
    }

    @Test
    void stopsBeforeBlockingTrees() {
        Field field = new Field(3, 3);
        Tank tank = new Tank(new Position(0, 0), Direction.RIGHT, 100);
        field.add(tank);
        field.add(new Tree(new Position(1, 0)));

        MovementManager manager = new MovementManager(field);
        assertThat(manager.tryMove(tank, Direction.RIGHT)).isFalse();
        assertThat(tank.position()).isEqualTo(new Position(0, 0));
    }
}
