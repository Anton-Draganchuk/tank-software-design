package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
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
import ru.mipt.bit.platformer.model.command.ToggleHealthDisplayCommand;
import ru.mipt.bit.platformer.model.control.PlayerTankController;
import ru.mipt.bit.platformer.model.control.RandomTankController;
import ru.mipt.bit.platformer.model.control.TankController;
import ru.mipt.bit.platformer.model.level.LevelLoader;
import ru.mipt.bit.platformer.model.level.RandomLevelLoader;
import ru.mipt.bit.platformer.model.level.TextLevelLoader;
import ru.mipt.bit.platformer.render.GdxHealthBarDrawer;
import ru.mipt.bit.platformer.render.GdxRenderer;
import ru.mipt.bit.platformer.render.HealthOverlayRenderer;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class GameDesktopLauncher extends ApplicationAdapter {
    private Renderer renderer;
    private InputHandler input;

    private SpriteBatch batch;
    private Texture tankTexture, treeTexture, bulletTexture;
    private TextureRegion bulletRegion;
    private TiledMap map;
    private TiledMapTileLayer ground;
    private MapRenderer levelRenderer;

    private Field field;
    private final List<Entity> world = new ArrayList<>();
    private final List<TankController> controllers = new ArrayList<>();
    private final Map<Tank, TankController> controllerByTank = new HashMap<>();
    private final Set<TankController> controllersToRemove = new HashSet<>();
    private Random aiRandom;
    private Random healthRandom;
    private float aiMoveInterval;
    private float aiShootProbability = 0.4f;
    private int minHealth = 80;
    private int maxHealth = 100;
    private int bulletDamage = 20;
    private float bulletSpeed = 10f;
    private float bulletReload = 0.5f;
    private HealthOverlayRenderer healthOverlayRenderer;
    private GdxHealthBarDrawer healthBarDrawer;
    private ToggleHealthDisplayCommand toggleHealthCommand;
    private WeaponManager weaponManager;

    @Override
    public void create() {
        batch = new SpriteBatch();


        map = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(map, batch);
        ground = getSingleLayer(map);


        tankTexture = new Texture("images/tank_blue.png");
        treeTexture = new Texture("images/greenTree.png");
        bulletTexture = createBulletTexture();
        bulletRegion = new TextureRegion(bulletTexture);

        Renderer baseRenderer = new GdxRenderer(
                batch,
                ground,
                new TextureRegion(tankTexture),
                new TextureRegion(treeTexture),
                bulletRegion
        );
        healthBarDrawer = new GdxHealthBarDrawer(batch, ground);
        healthOverlayRenderer = new HealthOverlayRenderer(baseRenderer, healthBarDrawer);
        renderer = healthOverlayRenderer;
        toggleHealthCommand = new ToggleHealthDisplayCommand(healthOverlayRenderer);
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
        controllerByTank.clear();
        controllersToRemove.clear();
        field.addObserver(new FieldObserver() {
            @Override
            public void entityAdded(Entity entity) {
                world.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                world.remove(entity);
                if (entity instanceof Tank tank) {
                    scheduleControllerRemoval(tank);
                }
            }
        });

        configureHealthRandom();
        configureWeapons();

        weaponManager = new WeaponManager(field, bulletDamage, bulletSpeed, bulletReload);

        Tank playerTank = createTank(data.playerStart, Direction.UP);
        addTank(playerTank, new PlayerTankController(playerTank, input, weaponManager));

        for (Position p : data.trees) {
            field.add(new Tree(p));
        }

        int aiCount = Math.max(0, Integer.getInteger("level.ai.count", 3));
        long aiSeed = Long.getLong("level.ai.seed", System.currentTimeMillis());
        aiMoveInterval = parseFloatProperty("level.ai.interval", 1.0f);
        aiShootProbability = clamp01(parseFloatProperty("level.ai.shootProbability", aiShootProbability));
        aiRandom = new Random(aiSeed);
        spawnAiTanks(aiCount, data.width, data.height);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        weaponManager.update(delta);
        MovementManager movement = new MovementManager(field);
        List<TankController> activeControllers = List.copyOf(controllers);
        for (TankController controller : activeControllers) {
            if (controllersToRemove.contains(controller)) {
                continue;
            }
            Command command = controller.nextCommand(movement, delta);
            if (command != null) {
                command.execute();
            }
        }
        if (input.isHealthToggleRequested()) {
            toggleHealthCommand.execute();
        }

        tickWorld(delta);
        cleanupControllers();

        levelRenderer.render();

        batch.begin();
        for (Entity e : world) {
            e.render(renderer);
        }
        batch.end();

        renderer.flush();
    }

    @Override
    public void dispose() {
        batch.dispose();
        tankTexture.dispose();
        treeTexture.dispose();
        if (bulletTexture != null) {
            bulletTexture.dispose();
        }
        map.dispose();
        if (healthOverlayRenderer != null) {
            healthOverlayRenderer.dispose();
        }
    }

    private void addTank(Tank tank, TankController controller) {
        controllers.add(controller);
        controllerByTank.put(tank, controller);
        weaponManager.registerTank(tank);
        field.add(tank);
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
            Direction dir = randomDirection();
            Tank aiTank = createTank(candidate, dir);
            addTank(aiTank, new RandomTankController(aiTank, aiRandom, aiMoveInterval, weaponManager, aiShootProbability));
            spawned++;
        }
    }

    private void tickWorld(float deltaTime) {
        for (Entity entity : field.all()) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.live(deltaTime);
            }
        }
    }

    private void cleanupControllers() {
        if (controllersToRemove.isEmpty()) {
            return;
        }
        controllers.removeAll(controllersToRemove);
        controllersToRemove.clear();
    }

    private void scheduleControllerRemoval(Tank tank) {
        TankController controller = controllerByTank.remove(tank);
        if (controller != null) {
            controllersToRemove.add(controller);
            weaponManager.unregisterTank(tank);
        }
    }

    private Direction randomDirection() {
        Direction[] dirs = Direction.values();
        return dirs[aiRandom.nextInt(dirs.length)];
    }

    private void configureHealthRandom() {
        minHealth = Math.max(1, Integer.getInteger("level.health.min", 80));
        maxHealth = Math.max(minHealth, Integer.getInteger("level.health.max", 100));
        long healthSeed = Long.getLong("level.health.seed", System.currentTimeMillis());
        healthRandom = new Random(healthSeed);
    }

    private void configureWeapons() {
        bulletDamage = Math.max(1, Integer.getInteger("weapon.bullet.damage", bulletDamage));
        bulletSpeed = Math.max(0.1f, parseFloatProperty("weapon.bullet.speed", bulletSpeed));
        bulletReload = Math.max(0.1f, parseFloatProperty("weapon.bullet.reload", bulletReload));
    }

    private Tank createTank(Position position, Direction direction) {
        return new Tank(position, direction, randomHealth());
    }

    private int randomHealth() {
        int range = Math.max(1, maxHealth - minHealth + 1);
        return minHealth + healthRandom.nextInt(range);
    }

    private Texture createBulletTexture() {
        Pixmap pixmap = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(1f, 0.8f, 0.1f, 1f);
        int radius = Math.max(4, pixmap.getWidth() / 3);
        pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, radius);
        pixmap.setColor(1f, 0.4f, 0.05f, 1f);
        pixmap.drawCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
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

    private static float clamp01(float value) {
        if (Float.isNaN(value)) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, value));
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Platformer");
        cfg.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), cfg);
    }
}
