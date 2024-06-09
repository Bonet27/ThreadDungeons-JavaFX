package com.bonet.threaddungeons;

public class Score {
    private int userId;
    private String username;
    private int etapaActual;
    private int casillaActual;
    private double dmg;
    private double speed;
    private int oro;

    public Score(int userId, String username, int etapaActual, int casillaActual, double dmg, double speed, int oro) {
        this.userId = userId;
        this.username = username;
        this.etapaActual = etapaActual;
        this.casillaActual = casillaActual;
        this.dmg = dmg;
        this.speed = speed;
        this.oro = oro;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getEtapaActual() {
        return etapaActual;
    }

    public int getCasillaActual() {
        return casillaActual;
    }

    public double getDmg() {
        return dmg;
    }

    public double getSpeed() {
        return speed;
    }

    public int getOro() {
        return oro;
    }
}
