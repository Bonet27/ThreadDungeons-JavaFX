package com.bonet.threaddungeons;

import java.util.Random;

public class Casilla {
    protected String icon;
    protected boolean isAlive = true;
    public float getDificultMultiplier() {
        return dificultMultiplier;
    }
    public enum Estado { SIN_ATACAR, EN_COMBATE, MUERTO }
    public Estado estado = Estado.SIN_ATACAR;
    protected enum Mode { NORMAL, REWARD, RANDOM, BOSS }
    protected Mode mode = Mode.NORMAL;
    protected float health;
    protected float maxHealth;
    protected float dificultMultiplier;
    protected float damage;
    protected float speed;
    private int reward;
    private int reward1;
    private String rewardIconUrl;
    private String rewardIconUrl1;
    private String rewardText;
    private String rewardText1;

    public Casilla() {}

    public Casilla(Mode mode, int etapa, int casilla) {
        this.mode = mode;
        this.dificultMultiplier = 1 + (etapa * 0.05f) + (casilla * 0.01f); // Incremento progresivo
        switch (this.mode) {
            case NORMAL:
                this.icon = "enemy1.png";
                this.damage = 10 * dificultMultiplier;
                this.health = 50 * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed = Math.round(12 * dificultMultiplier);
                this.reward = (int) (30 * dificultMultiplier);
                this.rewardIconUrl = "gold.png";
                this.rewardText = "+" + this.reward + " oro";
                break;
            case REWARD:
                this.icon = "enemy2.png";
                this.damage = 15 * dificultMultiplier;
                this.health = 50 * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed = Math.round(12 * dificultMultiplier);
                this.reward = (int) ((new Random().nextInt(31) + 30) * dificultMultiplier); // 30 to 60
                this.rewardIconUrl = "chest.png";
                this.rewardText = "+" + this.reward + " oro";
                break;
            case RANDOM:
                this.icon = "enemy3.png";
                this.damage = 15 * dificultMultiplier;
                this.health = 75 * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed = Math.round(12 * dificultMultiplier);
                if (new Random().nextBoolean()) {
                    this.reward = (int) (10 * dificultMultiplier);
                    this.rewardIconUrl = "heart.png";
                    this.rewardText = "+" + this.reward + " salud";
                } else {
                    this.reward = (int) (3 * dificultMultiplier);
                    this.rewardIconUrl = "sword.png";
                    this.rewardText = "+" + this.reward + " daño";
                }
                break;
            case BOSS:
                this.icon = "boss.png";
                this.damage = 20 * dificultMultiplier;
                this.health = 100 * dificultMultiplier;
                this.maxHealth = this.health;
                this.speed = Math.round(10 * dificultMultiplier);
                this.reward = (int) (5 * dificultMultiplier); // Aquí, la recompensa real es el valor de daño y velocidad, no oro
                this.reward1 = (int) (2 * dificultMultiplier); // Aquí, la recompensa real es el valor de daño y velocidad, no oro
                this.rewardIconUrl = "sword.png";
                this.rewardIconUrl1 = "lightning.png";
                this.rewardText = "+" + this.reward + " daño";
                this.rewardText1 = "+" + this.reward1 + " velocidad";
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
    public int getReward1() { return reward1; }

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
            estado = Estado.MUERTO;
        }
    }

    public boolean isSinAtacar() {
        return estado == Estado.SIN_ATACAR;
    }

    public void iniciarCombate() {
        estado = Estado.EN_COMBATE;
    }

    public boolean isEnCombate() {
        return estado == Estado.EN_COMBATE;
    }

    public boolean isMuerto() {
        return estado == Estado.MUERTO;
    }
}
