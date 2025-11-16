package ru.mipt.bit.platformer.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.model.*;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public final class GdxRenderer implements Renderer {
    private final Batch batch;
    private final TiledMapTileLayer ground;
    private final TextureRegion tankTex;
    private final TextureRegion treeTex;
    private final TextureRegion bulletTex;
    private final float tileWidth;
    private final float tileHeight;
    private final Rectangle tankRect = new Rectangle();
    private final Rectangle treeRect = new Rectangle();
    private final Rectangle bulletRect = new Rectangle();

    public GdxRenderer(Batch batch, TiledMapTileLayer ground, TextureRegion tankTex, TextureRegion treeTex, TextureRegion bulletTex){
        this.batch = batch;
        this.ground = ground;
        this.tankTex = tankTex;
        this.treeTex = treeTex;
        this.bulletTex = bulletTex;
        this.tileWidth = ground.getTileWidth();
        this.tileHeight = ground.getTileHeight();
    }

    @Override
    public void drawTank(Tank tank){
        tankRect.setSize(tileWidth, tileHeight);
        moveRectangleAtTileCenter(ground, tankRect, grid(tank.position()));
        float rotation = tank.direction().rotationDeg();
        float originX = tileWidth / 2f;
        float originY = tileHeight / 2f;
        batch.draw(tankTex, tankRect.x, tankRect.y, originX, originY, tileWidth, tileHeight, 1f, 1f, rotation);
    }

    @Override
    public void drawTree(Position p){
        treeRect.setSize(treeTex.getRegionWidth(), treeTex.getRegionHeight());
        moveRectangleAtTileCenter(ground, treeRect, grid(p));
        drawTextureRegionUnscaled(batch, treeTex, treeRect, 0f);
    }

    @Override
    public void drawBullet(Position position, Direction direction) {
        bulletRect.setSize(bulletTex.getRegionWidth(), bulletTex.getRegionHeight());
        float centerX = position.x() * tileWidth + tileWidth / 2f;
        float centerY = position.y() * tileHeight + tileHeight / 2f;
        float drawCenterX = centerX - direction.dx() * (tileWidth / 2f);
        float drawCenterY = centerY - direction.dy() * (tileHeight / 2f);
        float bulletHalfWidth = bulletRect.getWidth() / 2f;
        float bulletHalfHeight = bulletRect.getHeight() / 2f;
        bulletRect.setPosition(drawCenterX - bulletHalfWidth, drawCenterY - bulletHalfHeight);
        float rotation = direction.rotationDeg();
        drawTextureRegionUnscaled(batch, bulletTex, bulletRect, rotation);
    }

    @Override
    public void flush() { /* no-op */ }

    private static com.badlogic.gdx.math.GridPoint2 grid(Position p){
        return new com.badlogic.gdx.math.GridPoint2(p.x(), p.y());
    }
}
