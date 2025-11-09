package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import ru.mipt.bit.platformer.input.GdxInputHandler;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.model.level.LevelLoader;
import ru.mipt.bit.platformer.model.level.RandomLevelLoader;
import ru.mipt.bit.platformer.model.level.TextLevelLoader;
import ru.mipt.bit.platformer.render.GdxRenderer;

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

        // карта (TMX)
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

        // ------- ДЗ4: выбираем стратегию загрузки уровня -------
        int w = ground.getWidth();
        int h = ground.getHeight();

        // по умолчанию читаем из файла; можно переопределить -Dlevel.mode=random
        String mode = System.getProperty("level.mode", "file");
        LevelLoader loader = "random".equalsIgnoreCase(mode)
                ? new RandomLevelLoader(w, h, Math.max((w * h) / 10, 6), System.currentTimeMillis())
                : new TextLevelLoader("levels/level1.txt");   // файл в resources

        LevelLoader.LevelData data = loader.load();
        // -------------------------------------------------------

        // модель по данным загрузчика
        field = new Field(data.width, data.height);
        tank  = new Tank(data.playerStart, Direction.UP);
        field.add(tank);

        world.clear();
        world.add(tank);
        for (Position p : data.trees) {
            Tree t = new Tree(p);
            field.add(t);
            world.add(t);
        }
    }

    @Override
    public void render() {
        // логика ввода → движение
        Direction d = input.readDirection();
        if (d != null) new ForwardMovement(d).move(tank, field);

        // сначала слой карты
        levelRenderer.render();

        // затем сущности
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