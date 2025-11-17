package ru.mipt.bit.platformer.model.command;

import ru.mipt.bit.platformer.render.HealthOverlayRenderer;

public final class ToggleHealthDisplayCommand implements Command {
    private final HealthOverlayRenderer renderer;

    public ToggleHealthDisplayCommand(HealthOverlayRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void execute() {
        renderer.toggle();
    }
}
