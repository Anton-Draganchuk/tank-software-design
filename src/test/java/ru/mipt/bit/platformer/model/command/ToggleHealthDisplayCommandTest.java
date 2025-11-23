package ru.mipt.bit.platformer.model.command;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.Position;
import ru.mipt.bit.platformer.model.Renderer;
import ru.mipt.bit.platformer.model.Tank;
import ru.mipt.bit.platformer.model.Direction;
import ru.mipt.bit.platformer.render.HealthBarDrawer;
import ru.mipt.bit.platformer.render.HealthOverlayRenderer;

import static org.assertj.core.api.Assertions.assertThat;

class ToggleHealthDisplayCommandTest {

    @Test
    void togglesVisibility() {
        HealthOverlayRenderer renderer = new HealthOverlayRenderer(new NoOpRenderer(), new NoOpDrawer());
        ToggleHealthDisplayCommand command = new ToggleHealthDisplayCommand(renderer);
        assertThat(renderer.isVisible()).isFalse();

        command.execute();
        assertThat(renderer.isVisible()).isTrue();

        command.execute();
        assertThat(renderer.isVisible()).isFalse();
    }

    private static final class NoOpRenderer implements Renderer {
        @Override public void drawTank(Tank tank) {}
        @Override public void drawTree(Position p) {}
        @Override public void drawBullet(Position position, Direction direction) {}
        @Override public void flush() {}
    }

    private static final class NoOpDrawer implements HealthBarDrawer {
        @Override public void draw(Tank tank) {}
        @Override public void dispose() {}
    }
}
