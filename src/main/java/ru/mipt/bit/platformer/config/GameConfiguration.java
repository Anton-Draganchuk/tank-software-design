package ru.mipt.bit.platformer.config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import ru.mipt.bit.platformer.input.GdxInputHandler;
import ru.mipt.bit.platformer.input.InputHandler;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Renderer;
import ru.mipt.bit.platformer.model.WeaponManager;
import ru.mipt.bit.platformer.model.command.ToggleHealthDisplayCommand;
import ru.mipt.bit.platformer.model.level.LevelLoader;
import ru.mipt.bit.platformer.model.level.RandomLevelLoader;
import ru.mipt.bit.platformer.model.level.TextLevelLoader;
import ru.mipt.bit.platformer.render.GdxHealthBarDrawer;
import ru.mipt.bit.platformer.render.GdxRenderer;
import ru.mipt.bit.platformer.render.HealthOverlayRenderer;

@Configuration
public class GameConfiguration {

    @Bean(name = "baseRenderer")
    public Renderer baseRenderer(SpriteBatch batch,
                                 TiledMapTileLayer ground,
                                 @Qualifier("tankTextureRegion") TextureRegion tankRegion,
                                 @Qualifier("treeTextureRegion") TextureRegion treeRegion,
                                 @Qualifier("bulletTextureRegion") TextureRegion bulletRegion) {
        return new GdxRenderer(batch, ground, tankRegion, treeRegion, bulletRegion);
    }

    @Bean
    public GdxHealthBarDrawer gdxHealthBarDrawer(SpriteBatch batch, TiledMapTileLayer ground) {
        return new GdxHealthBarDrawer(batch, ground);
    }

    @Bean
    public HealthOverlayRenderer healthOverlayRenderer(@Qualifier("baseRenderer") Renderer baseRenderer,
                                                       GdxHealthBarDrawer drawer) {
        return new HealthOverlayRenderer(baseRenderer, drawer);
    }

    @Bean
    public ToggleHealthDisplayCommand toggleHealthDisplayCommand(HealthOverlayRenderer overlayRenderer) {
        return new ToggleHealthDisplayCommand(overlayRenderer);
    }

    @Bean
    public InputHandler inputHandler() {
        return new GdxInputHandler();
    }

    @Bean(name = "aiRandom")
    public Random aiRandom(@Value("${level.ai.seed:#{T(java.lang.System).currentTimeMillis()}}") long seed) {
        return new Random(seed);
    }

    @Bean(name = "healthRandom")
    public Random healthRandom(@Value("${level.health.seed:#{T(java.lang.System).currentTimeMillis()}}") long seed) {
        return new Random(seed);
    }

    @Bean
    public AiProperties aiProperties(@Value("${level.ai.interval:1.0}") float moveInterval,
                                     @Value("${level.ai.shootProbability:0.4}") float shootProbability) {
        return new AiProperties(moveInterval, shootProbability);
    }

    @Bean
    public HealthProperties healthProperties(@Value("${level.health.min:80}") int minHealth,
                                             @Value("${level.health.max:100}") int maxHealth) {
        return new HealthProperties(minHealth, maxHealth);
    }

    @Bean
    public WeaponProperties weaponProperties(@Value("${weapon.bullet.damage:20}") int bulletDamage,
                                             @Value("${weapon.bullet.speed:10}") float bulletSpeed,
                                             @Value("${weapon.bullet.reload:0.5}") float bulletReload) {
        return new WeaponProperties(bulletDamage, bulletSpeed, bulletReload);
    }

    @Bean
    public LevelLoader levelLoader(LevelDimensions dimensions,
                                   @Value("${level.mode:file}") String mode) {
        if ("random".equalsIgnoreCase(mode)) {
            int treesCount = Math.max((dimensions.getWidth() * dimensions.getHeight()) / 10, 6);
            return new RandomLevelLoader(dimensions.getWidth(), dimensions.getHeight(), treesCount,
                    System.currentTimeMillis());
        }
        return new TextLevelLoader("levels/level1.txt");
    }

    @Bean
    @Lazy
    public WeaponManager weaponManager(Field field, WeaponProperties properties) {
        return new WeaponManager(field, properties.getBulletDamage(),
                properties.getBulletSpeed(), properties.getBulletReload());
    }
}
