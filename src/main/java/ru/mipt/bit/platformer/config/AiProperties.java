package ru.mipt.bit.platformer.config;

public final class AiProperties {
    private final float moveInterval;
    private final float shootProbability;

    public AiProperties(float moveInterval, float shootProbability) {
        this.moveInterval = moveInterval;
        this.shootProbability = Math.max(0f, Math.min(1f, shootProbability));
    }

    public float getMoveInterval() {
        return moveInterval;
    }

    public float getShootProbability() {
        return shootProbability;
    }
}
