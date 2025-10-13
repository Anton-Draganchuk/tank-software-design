package ru.mipt.bit.platformer;

import com.badlogic.gdx.ApplicationListener;
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
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.*;
import ru.mipt.bit.platformer.render.GdxRenderer;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

public class GameDesktopLauncher implements ApplicationListener {

    private Batch batch;
    private TiledMap level;
    private MapRenderer levelRenderer;
    private TiledMapTileLayer ground;

    // ресурсы
    private Texture tankTexture;
    private Texture treeTexture;
    private TextureRegion tankRegion;
    private TextureRegion treeRegion;

    // модель
    private Field field;
    private Tank tank;
    private Renderer renderer;
    private InputHandler input;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // карта
        level = new TmxMapLoader().load("level.tmx");
        levelRenderer = createSingleLayerMapRenderer(level, batch);
        ground = getSingleLayer(level);

        // текстуры
        tankTexture = new Texture("images/tank_blue.png");
        treeTexture = new Texture("images/greenTree.png");
        tankRegion = new TextureRegion(tankTexture);
        treeRegion = new TextureRegion(treeTexture);

        // модель уровня 10x8 (как на слайде)
        field = new Field(10, 8);
        tank = new Tank(new Position(1, 1), Direction.RIGHT);
        field.add(tank);
        field.add(new Tree(new Position(1, 3))); // как было в изначальном коде

        // рендерер-адаптер
        renderer = new GdxRenderer(batch, ground, tankRegion, treeRegion);

        // обработчик ввода без копипаста
        input = new InputHandler(tank, field);
    }

    @Override
    public void render() {
        // фон
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        // вход (стреляем только по событию нажатия)
        if (Gdx.input.isKeyJustPressed(LEFT) || Gdx.input.isKeyJustPressed(A))  input.onKey(37);
        if (Gdx.input.isKeyJustPressed(RIGHT) || Gdx.input.isKeyJustPressed(D)) input.onKey(39);
        if (Gdx.input.isKeyJustPressed(UP) || Gdx.input.isKeyJustPressed(W))    input.onKey(38);
        if (Gdx.input.isKeyJustPressed(DOWN) || Gdx.input.isKeyJustPressed(S))  input.onKey(40);

        // отрисовка
        levelRenderer.render();
        batch.begin();
        for (Entity e : field.all()) e.render(renderer);
        batch.end();
    }

    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }

    @Override
    public void dispose() {
        treeTexture.dispose();
        tankTexture.dispose();
        level.dispose();
        batch.dispose();
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(1280, 1024);
        new Lwjgl3Application(new GameDesktopLauncher(), config);
    }
}