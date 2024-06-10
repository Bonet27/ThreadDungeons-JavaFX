package com.bonet.threaddungeons;

import java.util.Random;

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
    private String rewardIconUrl;
    private String rewardIconUrl1;
    private String rewardText;
    private String rewardText1;

    public Casilla() {}

    public Casilla(Mode mode, float dificultMultiplier, int etapa, int casilla) {
        this.mode = mode;
        float rewardMultiplier = 1 + (etapa * 0.05f) + (casilla * 0.01f); // Incremento progresivo
        switch (this.mode) {
            case NORMAL:
                dificultMultiplier = 1;
                this.icon = "enemy1.png";
                this.damage *= dificultMultiplier;
                this.speed *= dificultMultiplier;
                this.reward = (int) (30 * rewardMultiplier);
                this.rewardIconUrl = "gold.png";
                this.rewardText = "+" + this.reward + " oro";
                break;
            case REWARD:
                dificultMultiplier = 1.25f;
                this.icon = "enemy2.png";
                this.damage *= dificultMultiplier;
                this.health = health * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                this.reward = (int) ((new Random().nextInt(31) + 30) * rewardMultiplier); // 30 to 60
                this.rewardIconUrl = "chest.png";
                this.rewardText = "+" + this.reward + " oro";
                break;
            case RANDOM:
                dificultMultiplier = 1.1f;
                this.icon = "enemy3.png";
                this.damage *= dificultMultiplier;
                this.health = health * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                if (new Random().nextBoolean()) {
                    this.reward = (int) (10 * rewardMultiplier);
                    this.rewardIconUrl = "heart.png";
                    this.rewardText = "+" + this.reward + " salud";
                } else {
                    this.reward = (int) (3 * rewardMultiplier);
                    this.rewardIconUrl = "sword.png";
                    this.rewardText = "+" + this.reward + " daño";
                }
                break;
            case BOSS:
                dificultMultiplier = 1.5f;
                this.icon = "boss.png";
                this.damage *= dificultMultiplier;
                this.health = health * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed *= dificultMultiplier;
                this.reward = (int) (5 * rewardMultiplier);
                this.rewardIconUrl = "sword.png";
                this.rewardIconUrl1 = "lightning.png";
                this.rewardText = "+" + this.reward + " daño";
                this.rewardText1 = "+" + this.reward + " velocidad";
                break;
        }
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

    public String getRewardIconUrl() {
        return rewardIconUrl;
    }

    public String getRewardIconUrl1() {
        return rewardIconUrl1;
    }

    public String getRewardText() {
        return rewardText;
    }

    public String getRewardText1() {
        return rewardText1;
    }

    public void takeDamage(float damage) {
        health -= damage;

        if (health <= 0) {
            health = 0;
            isAlive = false;
            setEstado(Estado.MUERTO);
        }
    }
}
