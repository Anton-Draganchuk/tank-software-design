package ru.mipt.bit.platformer.config;

public final class WeaponProperties {
    private final int bulletDamage;
    private final float bulletSpeed;
    private final float bulletReload;

    public WeaponProperties(int bulletDamage, float bulletSpeed, float bulletReload) {
        this.bulletDamage = Math.max(1, bulletDamage);
        this.bulletSpeed = Math.max(0.1f, bulletSpeed);
        this.bulletReload = Math.max(0.1f, bulletReload);
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public float getBulletSpeed() {
        return bulletSpeed;
    }

    public float getBulletReload() {
        return bulletReload;
    }
}
