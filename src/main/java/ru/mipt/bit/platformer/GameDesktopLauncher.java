package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.Gdx;
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
import ru.mipt.bit.platformer.model.command.Command;
import ru.mipt.bit.platformer.model.control.PlayerTankController;
import ru.mipt.bit.platformer.model.control.RandomTankController;
import ru.mipt.bit.platformer.model.control.TankController;
import ru.mipt.bit.platformer.model.level.LevelLoader;
import ru.mipt.bit.platformer.model.level.RandomLevelLoader;
import ru.mipt.bit.platformer.model.level.TextLevelLoader;
import ru.mipt.bit.platformer.render.GdxRenderer;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GameDesktopLauncher extends ApplicationAdapter {
    private Renderer renderer;
    private InputHandler input;

    private SpriteBatch batch;
    private Texture tankTexture, treeTexture;
    private TiledMap map;
    private TiledMapTileLayer ground;
    private MapRenderer levelRenderer;

    private Field field;
    private final List<Entity> world = new ArrayList<>();
    private final List<TankController> controllers = new ArrayList<>();
    private Random aiRandom;
    private float aiMoveInterval;

    @Override
    public void create() {
        batch = new SpriteBatch();


        map = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(map, batch);
        ground = getSingleLayer(map);


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

        // по умолчанию читаем из файла; можно переопределить -Dlevel.mode=random
        String mode = System.getProperty("level.mode", "file");
        LevelLoader loader = "random".equalsIgnoreCase(mode)
                ? new RandomLevelLoader(w, h, Math.max((w * h) / 10, 6), System.currentTimeMillis())
                : new TextLevelLoader("levels/level1.txt");

        LevelLoader.LevelData data = loader.load();


        field = new Field(data.width, data.height);
        world.clear();
        controllers.clear();

        Tank playerTank = new Tank(data.playerStart, Direction.UP);
        addTank(playerTank, new PlayerTankController(playerTank, input));

        List<Tree> treeEntities = new ArrayList<>();
        for (Position p : data.trees) {
            Tree t = new Tree(p);
            field.add(t);
            treeEntities.add(t);
        }

        int aiCount = Math.max(0, Integer.getInteger("level.ai.count", 3));
        long aiSeed = Long.getLong("level.ai.seed", System.currentTimeMillis());
        aiMoveInterval = parseFloatProperty("level.ai.interval", 1.0f);
        aiRandom = new Random(aiSeed);
        spawnAiTanks(aiCount, data.width, data.height);

        world.addAll(treeEntities);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        MovementManager movement = new MovementManager(field);
        for (TankController controller : controllers) {
            Command command = controller.nextCommand(movement, delta);
            if (command != null) command.execute();
        }


        levelRenderer.render();


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

    private void addTank(Tank tank, TankController controller) {
        field.add(tank);
        world.add(tank);
        controllers.add(controller);
    }

    private void spawnAiTanks(int count, int width, int height) {
        if (count == 0) return;
        int spawned = 0;
        int maxAttempts = Math.max(width * height * 2, count * 5);
        int attempts = 0;
        while (spawned < count && attempts < maxAttempts) {
            attempts++;
            Position candidate = new Position(aiRandom.nextInt(width), aiRandom.nextInt(height));
            if (!field.isFree(candidate)) continue;
            Tank aiTank = new Tank(candidate, randomDirection());
            addTank(aiTank, new RandomTankController(aiTank, aiRandom, aiMoveInterval));
            spawned++;
        }
    }

    private Direction randomDirection() {
        Direction[] dirs = Direction.values();
        return dirs[aiRandom.nextInt(dirs.length)];
    }

    private static float parseFloatProperty(String key, float defaultValue) {
        String value = System.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Platformer");
        cfg.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), cfg);
    }
}
