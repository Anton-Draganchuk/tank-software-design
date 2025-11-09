package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.Position;
import ru.mipt.bit.platformer.model.Tank;

import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

public final class GdxHealthBarDrawer implements HealthBarDrawer {
    private final Batch batch;
    private final TiledMapTileLayer ground;
    private final TextureRegion pixel;
    private final Rectangle tileRect = new Rectangle();

    public GdxHealthBarDrawer(Batch batch, TiledMapTileLayer ground) {
        this.batch = batch;
        this.ground = ground;
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.pixel = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
    }

    @Override
    public void draw(Tank tank) {
        if (tank.maxHealth() <= 0) return;
        float tileWidth = ground.getTileWidth();
        float tileHeight = ground.getTileHeight();
        tileRect.setSize(tileWidth, tileHeight);
        moveRectangleAtTileCenter(ground, tileRect, grid(tank.position()));
        float width = tileWidth * 0.7f;
        float height = tileHeight * 0.08f;
        float x = tileRect.x + tileRect.width / 2f;
        float centerY = tileRect.y + tileRect.height * 3/4f;
        float y = centerY + height;
        drawBackground(x, y, width, height);
        drawForeground(x, y, width, height, tank.health(), tank.maxHealth());
    }

    private void drawBackground(float x, float y, float width, float height) {
        batch.setColor(0f, 0f, 0f, 0.6f);
        batch.draw(pixel, x, y, width, height);
    }

    private void drawForeground(float x, float y, float width, float height, int health, int maxHealth) {
        float ratio = Math.max(0f, Math.min(1f, (float) health / maxHealth));
        batch.setColor(Color.FOREST);
        batch.draw(pixel, x, y, width * ratio, height);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        Texture texture = pixel.getTexture();
        if (texture != null) texture.dispose();
    }

    private static GridPoint2 grid(Position p) {
        return new GridPoint2(p.x(), p.y());
    }
}
