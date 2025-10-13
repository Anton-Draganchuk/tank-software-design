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
    public void drawTank(Position p, Direction d){
        moveRectangleAtTileCenter(ground, tankRect, grid(p));
        float rotation = switch (d){
            case UP -> 90f; case RIGHT -> 0f; case DOWN -> -90f; case LEFT -> 180f;
        };
        drawTextureRegionUnscaled(batch, tankTex, tankRect, rotation);
    }

    @Override
    public void drawTree(Position p){
        moveRectangleAtTileCenter(ground, treeRect, grid(p));
        drawTextureRegionUnscaled(batch, treeTex, treeRect, 0f);
    }

    @Override
    public void flush() { /* тут ничего не нужно */ }

    private static com.badlogic.gdx.math.GridPoint2 grid(Position p){
        return new com.badlogic.gdx.math.GridPoint2(p.x(), p.y());
    }
}