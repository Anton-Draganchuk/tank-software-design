package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import ru.mipt.bit.platformer.input.GdxInputHandler;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.render.GdxRenderer;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

import java.util.ArrayList;
import java.util.List;

public final class GameDesktopLauncher extends ApplicationAdapter {
    private Renderer renderer;
    private InputHandler input;

    private SpriteBatch batch;
    private Texture tankTexture, treeTexture;
    private TiledMap map;
    private TiledMapTileLayer ground;
    private MapRenderer levelRenderer;

    private Field field;
    private Tank tank;
    private final List<Entity> world = new ArrayList<>();

    @Override
    public void create() {
        batch = new SpriteBatch();

        // карта
        map = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(map, batch);
        ground = getSingleLayer(map);


        // текстуры
        tankTexture = new Texture("images/tank_blue.png");
        treeTexture = new Texture("images/greenTree.png");

        renderer = new GdxRenderer(
                batch,
                ground,
                new TextureRegion(tankTexture),
                new TextureRegion(treeTexture)
        );
        input = new GdxInputHandler();

        int w = ground.getWidth();
        int h = ground.getHeight();
        field = new Field(w, h);

        tank = new Tank(new Position(1, 1), Direction.UP);
        Tree t1 = new Tree(new Position(3, 1));
        Tree t2 = new Tree(new Position(4, 2));

        field.add(tank);
        field.add(t1);
        field.add(t2);

        world.add(tank);
        world.add(t1);
        world.add(t2);
    }

    @Override
    public void render() {
        // логика
        Direction d = input.readDirection();
        if (d != null) new ForwardMovement(d).move(tank, field);

        // сначала слой карты
        levelRenderer.render();

        // затем наши сущности — ОБЯЗАТЕЛЬНО в begin/end
        batch.begin();
        for (Entity e : world) e.render(renderer);
        batch.end();

        renderer.flush();
    }

    @Override
    public void dispose() {
        batch.dispose();
        tankTexture.dispose();
        treeTexture.dispose();
        map.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Platformer");
        cfg.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), cfg);
    }
}