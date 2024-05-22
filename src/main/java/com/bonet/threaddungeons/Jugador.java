package com.bonet.threaddungeons;

public class Jugador {
    private String nombre;
    private float salud;
    private int oro;
    private float velocidad;
    private float dmg;
    private int etapaActual;
    private int casillaActual;

    public Jugador(String nombre, float salud, int oro, float velocidad, float dmg) {
        this.nombre = nombre;
        this.salud = salud;
        this.oro = oro;
        this.velocidad = velocidad;
        this.dmg = dmg;
        this.etapaActual = 0;
        this.casillaActual = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getSalud() {
        return salud;
    }

    public void setSalud(float salud) {
        this.salud = salud;
    }

    public int getOro() {
        return oro;
    }

    public void setOro(int oro) {
        this.oro = oro;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public float getDmg() {
        return dmg;
    }

    public void setDmg(float dmg) {
        this.dmg = dmg;
    }

    public int getEtapaActual() {
        return etapaActual;
    }

    public void setEtapaActual(int etapaActual) {
        this.etapaActual = etapaActual;
    }

    public int getCasillaActual() {
        return casillaActual;
    }

    public void setCasillaActual(int casillaActual) {
        this.casillaActual = casillaActual;
    }

    public void takeDamage(float damage) {
        this.salud -= damage;
        if (this.salud < 0) {
            this.salud = 0;
        }
    }

    public boolean isAlive() {
        return this.salud > 0;
    }
}
