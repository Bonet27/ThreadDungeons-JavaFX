package com.bonet.threaddungeons;

public class Casilla {
    protected String icon;
    public boolean isAlive = true;

    public boolean isAlive() {
        return isAlive;
    }

    public enum Estado { SIN_ATACAR, EN_COMBATE, MUERTO }
    private Estado estado = Estado.SIN_ATACAR;

    protected enum Mode { NORMAL, REWARD, RANDOM, BOSS }

    public Mode getMode() {
        return mode;
    }

    protected Mode mode = Mode.NORMAL;
    protected float health = 100f;
    protected float maxHealth = 100f; // Añadido campo maxHealth
    protected float dificultMultiplier = 1f;

    public float getDamage() {
        return damage;
    }

    protected float damage = 10f;
    private int reward;
    protected float speed = 12f;

    public Casilla() {
    }

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
                this.maxHealth = this.health; // Establecer maxHealth
                this.speed *= dificultMultiplier;
                break;
            case RANDOM:
                this.icon = "enemy8.png";
                this.damage *= dificultMultiplier;
                this.health = 250f * dificultMultiplier;
                this.maxHealth = this.health; // Establecer maxHealth
                this.speed *= dificultMultiplier;
                break;
            case BOSS:
                this.icon = "boss3.png";
                this.damage *= dificultMultiplier;
                this.health = 500f * dificultMultiplier;
                this.maxHealth = this.health; // Establecer maxHealth
                this.speed *= dificultMultiplier;
                break;
        }
        this.reward = calculateReward();
    }

    public String getIcon() {
        return icon;
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
            this.health = 0;
            this.isAlive = false;
            setEstado(Estado.MUERTO);
        }
    }

    public int getReward() {
        return reward;
    }

    public float getMaxHealth() {
        return maxHealth; // Getter para maxHealth
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    private int calculateReward() {
        // Lógica para calcular la recompensa basada en el tipo de casilla y otros factores
        return (int) (100 * dificultMultiplier);
    }

    @Override
    public String toString() {
        return icon + " ";
    }
}
