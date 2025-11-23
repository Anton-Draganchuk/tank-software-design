package ru.mipt.bit.platformer.config;

public final class HealthProperties {
    private final int minHealth;
    private final int maxHealth;

    public HealthProperties(int minHealth, int maxHealth) {
        int normalizedMin = Math.max(1, minHealth);
        this.minHealth = normalizedMin;
        this.maxHealth = Math.max(normalizedMin, maxHealth);
    }

    public int getMinHealth() {
        return minHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
