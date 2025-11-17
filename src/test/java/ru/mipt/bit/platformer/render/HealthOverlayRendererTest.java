package ru.mipt.bit.platformer.render;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.model.*;

import static org.assertj.core.api.Assertions.assertThat;

class HealthOverlayRendererTest {

    @Test
    void togglesHealthDrawingViaDecorator() {
        StubRenderer delegate = new StubRenderer();
        StubDrawer drawer = new StubDrawer();
        HealthOverlayRenderer renderer = new HealthOverlayRenderer(delegate, drawer);

        Tank tank = new Tank(new Position(0, 0), Direction.UP, 100);
        renderer.drawTank(tank);
        assertThat(delegate.tankDrawn).isTrue();
        assertThat(drawer.drawCalls).isEqualTo(0);

        renderer.toggle();
        renderer.drawTank(tank);
        assertThat(drawer.drawCalls).isEqualTo(1);
    }

    private static final class StubRenderer implements Renderer {
        boolean tankDrawn;

        @Override
        public void drawTank(Tank tank) {
            tankDrawn = true;
        }

        @Override
        public void drawTree(Position p) {
        }

        @Override
        public void drawBullet(Position position, Direction direction) {
        }

        @Override
        public void flush() {
        }
    }

    private static final class StubDrawer implements HealthBarDrawer {
        int drawCalls;

        @Override
        public void draw(Tank tank) {
            drawCalls++;
        }

        @Override
        public void dispose() {
        }
    }
}
