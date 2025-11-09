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
    private final Rectangle tankRect = new Rectangle();
    private final Rectangle treeRect = new Rectangle();

    public GdxRenderer(Batch batch, TiledMapTileLayer ground, TextureRegion tankTex, TextureRegion treeTex){
        this.batch = batch;
        this.ground = ground;
        this.tankTex = tankTex;
        this.treeTex = treeTex;
    }

    @Override
    public void drawTank(Tank tank){
        moveRectangleAtTileCenter(ground, tankRect, grid(tank.position()));
        float rotation = tank.direction().rotationDeg();
        drawTextureRegionUnscaled(batch, tankTex, tankRect, rotation);
    }

    @Override
    public void drawTree(Position p){
        moveRectangleAtTileCenter(ground, treeRect, grid(p));
        drawTextureRegionUnscaled(batch, treeTex, treeRect, 0f);
    }

    @Override
    public void flush() { /* no-op */ }

    private static com.badlogic.gdx.math.GridPoint2 grid(Position p){
        return new com.badlogic.gdx.math.GridPoint2(p.x(), p.y());
    }
}
