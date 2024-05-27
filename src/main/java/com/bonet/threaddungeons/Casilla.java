package com.bonet.threaddungeons;

public class Casilla {
    protected String icon;
    protected boolean isAlive = true;
    protected enum Estado { SIN_ATACAR, EN_COMBATE, MUERTO }
    protected Estado estado = Estado.SIN_ATACAR;
    protected enum Mode { NORMAL, REWARD, RANDOM, BOSS }
    protected Mode mode = Mode.NORMAL;
    protected float health = 100f, maxHealth = 100f, dificultMultiplier = 1f, damage = 10f, speed = 12f;
    private int reward;
    public Casilla() {}

    public Casilla(Mode mode) {
        this.mode = mode;
        switch (this.mode) {
            case NORMAL:
                this.icon = "enemy6.png";
                this.damage *= dificultMultiplier;
                this.speed *= dificultMultiplier;
                break;
            case REWARD:
                this.icon = "enemy7.png";
                this.damage *= dificultMultiplier;
                this.health = 150f * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                break;
            case RANDOM:
                this.icon = "enemy8.png";
                this.damage *= dificultMultiplier;
                this.health = 250f * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                break;
            case BOSS:
                this.icon = "boss3.png";
                this.damage *= dificultMultiplier;
                this.health = 500f * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                break;
        }
        this.reward = calculateReward();
    }
    public Mode getMode() {
        return mode;
    }

    public boolean isAlive() { return isAlive; }

    public float getDamage() { return damage; }

    public String getIcon() { return icon; }

    public float getHealth() { return health; }

    public void setHealth(float health) { this.health = health; }

    public int getReward() { return reward; }

    public float getMaxHealth() { return maxHealth; }

    public Estado getEstado() { return estado; }

    public void setEstado(Estado estado) { this.estado = estado; }

    private int calculateReward() { return (int) (100 * dificultMultiplier); }

    public void takeDamage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            this.isAlive = false;
            setEstado(Estado.MUERTO);
        }
    }
}
