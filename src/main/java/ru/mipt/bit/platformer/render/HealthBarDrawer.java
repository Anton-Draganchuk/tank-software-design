package ru.mipt.bit.platformer.render;

import ru.mipt.bit.platformer.model.Tank;

public interface HealthBarDrawer {
    void draw(Tank tank);
    void dispose();
}
