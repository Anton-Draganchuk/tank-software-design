package ru.mipt.bit.platformer.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.model.Direction;

public final class GdxInputHandler implements InputHandler {
    @Override
    public Direction readDirection() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP))    return Direction.UP;
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))  return Direction.DOWN;
        if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))  return Direction.LEFT;
        if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) return Direction.RIGHT;
        return null;
    }

    @Override
    public boolean isHealthToggleRequested() {
        return Gdx.input.isKeyJustPressed(Input.Keys.L);
    }
}
