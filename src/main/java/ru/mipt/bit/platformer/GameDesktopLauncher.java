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

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.mipt.bit.platformer.config.AiProperties;
import ru.mipt.bit.platformer.config.GameConfiguration;
import ru.mipt.bit.platformer.config.HealthProperties;
import ru.mipt.bit.platformer.config.LevelDimensions;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.model.command.Command;
import ru.mipt.bit.platformer.model.command.ToggleHealthDisplayCommand;
import ru.mipt.bit.platformer.model.control.PlayerTankController;
import ru.mipt.bit.platformer.model.control.RandomTankController;
import ru.mipt.bit.platformer.model.control.TankController;
import ru.mipt.bit.platformer.model.level.LevelLoader;
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
    private AnnotationConfigApplicationContext context;

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
    private HealthOverlayRenderer healthOverlayRenderer;
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

        initializeApplicationContext();

        healthOverlayRenderer = context.getBean(HealthOverlayRenderer.class);
        renderer = healthOverlayRenderer;
        toggleHealthCommand = context.getBean(ToggleHealthDisplayCommand.class);
        input = context.getBean(InputHandler.class);

        LevelLoader loader = context.getBean(LevelLoader.class);
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
        configureAiSettings();

        context.getBeanFactory().registerSingleton("field", field);
        weaponManager = context.getBean(WeaponManager.class);

        Tank playerTank = createTank(data.playerStart, Direction.UP);
        addTank(playerTank, new PlayerTankController(playerTank, input, weaponManager));

        for (Position p : data.trees) {
            field.add(new Tree(p));
        }

        int aiCount = Math.max(0, Integer.getInteger("level.ai.count", 3));
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
        if (context != null) {
            context.close();
        }
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
        HealthProperties properties = context.getBean(HealthProperties.class);
        minHealth = properties.getMinHealth();
        maxHealth = properties.getMaxHealth();
        healthRandom = context.getBean("healthRandom", Random.class);
    }

    private void configureAiSettings() {
        AiProperties properties = context.getBean(AiProperties.class);
        aiMoveInterval = properties.getMoveInterval();
        aiShootProbability = properties.getShootProbability();
        aiRandom = context.getBean("aiRandom", Random.class);
    }

    private void initializeApplicationContext() {
        context = new AnnotationConfigApplicationContext();
        context.register(GameConfiguration.class);
        context.getBeanFactory().registerSingleton("spriteBatch", batch);
        context.getBeanFactory().registerSingleton("ground", ground);
        context.getBeanFactory().registerSingleton("tankTextureRegion", new TextureRegion(tankTexture));
        context.getBeanFactory().registerSingleton("treeTextureRegion", new TextureRegion(treeTexture));
        context.getBeanFactory().registerSingleton("bulletTextureRegion", bulletRegion);
        context.getBeanFactory().registerSingleton("levelDimensions",
                new LevelDimensions(ground.getWidth(), ground.getHeight()));
        context.refresh();
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

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Platformer");
        cfg.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), cfg);
    }
}
