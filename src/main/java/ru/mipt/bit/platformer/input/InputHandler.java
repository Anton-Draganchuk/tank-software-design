package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.Direction;

public interface InputHandler {
    Direction readDirection();
    boolean isHealthToggleRequested();
}
