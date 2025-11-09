package ru.mipt.bit.platformer.model.control;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.model.command.Command;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class TankControllerTest {

    @Test
    void playerControllerExecutesInputCommand() {
        Field field = new Field(3, 3);
        Tank tank = new Tank(new Position(1, 1), Direction.UP, 100);
        field.add(tank);

        MovementManager manager = new MovementManager(field);
        InputHandler handler = new InputHandler() {
            @Override public Direction readDirection() { return Direction.RIGHT; }
            @Override public boolean isHealthToggleRequested() { return false; }
        };
        PlayerTankController controller = new PlayerTankController(tank, handler);

        Command command = controller.nextCommand(manager, 0.016f);
        assertThat(command).isNotNull();
        command.execute();
        assertThat(tank.position()).isEqualTo(new Position(2, 1));
    }

    @Test
    void playerControllerSkipsWhenNoInput() {
        Field field = new Field(3, 3);
        Tank tank = new Tank(new Position(1, 1), Direction.UP, 100);
        field.add(tank);

        MovementManager manager = new MovementManager(field);
        InputHandler handler = new InputHandler() {
            @Override public Direction readDirection() { return null; }
            @Override public boolean isHealthToggleRequested() { return false; }
        };
        PlayerTankController controller = new PlayerTankController(tank, handler);

        assertThat(controller.nextCommand(manager, 0.016f)).isNull();
        assertThat(tank.position()).isEqualTo(new Position(1, 1));
    }

    @Test
    void randomControllerUsesRandomDirection() {
        Field field = new Field(3, 3);
        Tank tank = new Tank(new Position(1, 1), Direction.UP, 100);
        field.add(tank);

        MovementManager manager = new MovementManager(field);
        RandomTankController controller = new RandomTankController(tank, new StubRandom(1), 0.5f);

        assertThat(controller.nextCommand(manager, 0.1f)).isNull();

        Command command = controller.nextCommand(manager, 0.4f);
        assertThat(command).isNotNull();
        command.execute();

        assertThat(tank.position()).isEqualTo(new Position(2, 1));
    }

    private static final class StubRandom extends Random {
        private final int value;

        StubRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            if (value >= bound) {
                throw new IllegalArgumentException("Stub value >= bound");
            }
            return value;
        }
    }
}
