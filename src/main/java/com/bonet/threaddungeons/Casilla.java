package com.bonet.threaddungeons;

public class Casilla {
    protected String icon;
    protected boolean isAlive = true;
    protected enum Mode {NORMAL, REWARD, RANDOM}
    protected Mode mode = Mode.NORMAL;
        protected float health = 100f;
    protected float dificultMultiplier = 1f;
    protected float damage = 10f;
    private int reward;
    protected float speed = 12f;

    public Casilla() {
    }

    public Casilla(Mode mode) {
        this.mode = mode;
        switch (this.mode) {
            case NORMAL:
                this.icon = "enemy1.png";
                this.damage *= dificultMultiplier;
                this.speed *= dificultMultiplier;
                break;
            case REWARD:
                this.icon = "enemy2.png";
                this.damage *= dificultMultiplier;
                this.health = 150f * dificultMultiplier;
                this.speed *= dificultMultiplier;
                break;
            case RANDOM:
                this.icon = "enemy3.png";
                this.damage *= dificultMultiplier;
                this.health = 250f * dificultMultiplier;
                this.speed *= dificultMultiplier;
                break;
        }
        this.reward = calculateReward();
    }

    private int calculateReward() {
        // Lógica para calcular la recompensa basada en el tipo de casilla y otros factores
        return (int) (100 * dificultMultiplier);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.isAlive = false;
        }
    }

    public int getReward() {
        return reward;
    }

    @Override
    public String toString() {
        return icon + " ";
    }
}
