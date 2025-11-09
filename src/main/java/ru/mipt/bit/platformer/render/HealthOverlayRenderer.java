package ru.mipt.bit.platformer.render;

import ru.mipt.bit.platformer.model.Position;
import ru.mipt.bit.platformer.model.Renderer;
import ru.mipt.bit.platformer.model.Tank;

public final class HealthOverlayRenderer implements Renderer {
    private final Renderer delegate;
    private final HealthBarDrawer drawer;
    private boolean visible;

    public HealthOverlayRenderer(Renderer delegate, HealthBarDrawer drawer) {
        this.delegate = delegate;
        this.drawer = drawer;
    }

    @Override
    public void drawTank(Tank tank) {
        delegate.drawTank(tank);
        if (visible) drawer.draw(tank);
    }

    @Override
    public void drawTree(Position p) {
        delegate.drawTree(p);
    }

    @Override
    public void flush() {
        delegate.flush();
    }

    public void toggle() {
        visible = !visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void dispose() {
        drawer.dispose();
    }
}
