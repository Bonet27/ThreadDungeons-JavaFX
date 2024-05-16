package com.bonet.threaddungeons;

public class Jugador {
    private String nombre;
    private int salud;
    private int oro;
    private int etapaActual;
    private int casillaActual;
    private float velocidad;
    private float dmg;

    public Jugador(String nombre, int salud, int oro, float velocidad, float dmg) {
        this.nombre = nombre;
        this.salud = salud;
        this.oro = oro;
        this.etapaActual = 0;
        this.casillaActual = 0;
        this.velocidad = velocidad;
        this.dmg = dmg;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getSalud() {
        return salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getOro() {
        return oro;
    }

    public void setOro(int oro) {
        this.oro = oro;
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
}
