package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.model.*;

import java.util.Map;

public final class InputHandler {
    private final Map<Integer, Runnable> keymap;

    public InputHandler(Tank tank, Field field){
        keymap = Map.of(
                37, tank::turnLeft,                   // LEFT
                39, tank::turnRight,                  // RIGHT
                38, () -> tank.moveForward(field),    // UP
                40, () -> {                           // DOWN = развернуться и шаг вперёд
                    tank.turnRight(); tank.turnRight();
                    tank.moveForward(field);
                }
        );
    }
    public void onKey(int keyCode){
        var a = keymap.get(keyCode);
        if (a != null) a.run();
    }
}