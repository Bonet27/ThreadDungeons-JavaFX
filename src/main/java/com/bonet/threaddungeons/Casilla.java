package com.bonet.threaddungeons;

public class Casilla {
    protected String icon;
    protected boolean isAlive = true;
    protected enum Estado { SIN_ATACAR, EN_COMBATE, MUERTO }
    protected Estado estado = Estado.SIN_ATACAR;
    protected enum Mode { NORMAL, REWARD, RANDOM, BOSS }
    protected Mode mode = Mode.NORMAL;
    protected float health = 50f;
    protected float maxHealth = 50f;
    protected float dificultMultiplier = 1f;
    protected float damage = 10f;
    protected float speed = 12f;
    private int reward;

    public Casilla() {}

    public Casilla(Mode mode, float dificultMultiplier) {
        this.mode = mode;
        switch (this.mode) {
            case NORMAL:
                dificultMultiplier = 1;
                this.icon = "enemy6.png";
                this.damage *= dificultMultiplier;
                this.speed *= dificultMultiplier;
                break;
            case REWARD:
                dificultMultiplier = Math.abs(1.25f);
                this.icon = "enemy7.png";
                this.damage *= dificultMultiplier;
                this.health = health * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                break;
            case RANDOM:
                dificultMultiplier = Math.abs(1.1f);
                this.icon = "enemy8.png";
                this.damage *= dificultMultiplier;
                this.health = health * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                break;
            case BOSS:
                dificultMultiplier = Math.abs(1.5f);
                this.icon = "boss3.png";
                this.damage *= dificultMultiplier;
                this.health = health * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                break;
        }
        this.reward = calculateReward();
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isAlive() { return health > 0; }

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
        health -= damage;

        if (health <= 0) {
            health = 0;
            isAlive = false;
            setEstado(Estado.MUERTO);
        }
    }
}
