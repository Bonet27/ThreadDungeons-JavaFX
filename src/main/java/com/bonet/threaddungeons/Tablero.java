package com.bonet.threaddungeons;

import java.util.Random;

public class Tablero {
    private final Etapa[] etapas;
    private Jugador jugador;
    private boolean partidaAcabada;

    public Tablero(String username) {
        this.etapas = new Etapa[2];
        this.jugador = new Jugador(username, 100, 100, 0, 1.0f, 10.0f, 0, 0);
        this.partidaAcabada = false;

        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa(i, i + 1);
        }
    }

    public boolean isPartidaAcabada() {
        return partidaAcabada;
    }

    public Etapa[] getEtapas() {
        return etapas;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setPartidaAcabada(boolean partidaAcabada) {
        this.partidaAcabada = partidaAcabada;
    }

    public void iniciarCombate(Casilla casillaActual) {
        casillaActual.setEstado(Casilla.Estado.EN_COMBATE);
    }

    public void saltar() {
        Random rdn = new Random();
        var num = rdn.nextFloat(0f,1f);
        if (num < 0.75f) {
            avanzar();
        } else {
            Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
            iniciarCombate(casillaActual);
        }
    }

    public void atacar() {
        Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
        if (casillaActual.isAlive()) {
            casillaActual.takeDamage(jugador.getDmg());
            if (casillaActual.getHealth() <= 0) {
                aplicarRecompensa(casillaActual);
                avanzar();
            }
        }
    }

    private void aplicarRecompensa(Casilla casilla) {
        switch (casilla.getMode()) {
            case NORMAL:
            case REWARD:
                jugador.setOro(jugador.getOro() + casilla.getReward());
                break;
            case RANDOM:
                if (casilla.getRewardText().contains("salud")) {
                    jugador.setSalud(Math.min(jugador.getSaludMaxima(), jugador.getSalud() + casilla.getReward()));
                } else if (casilla.getRewardText().contains("daño")) {
                    jugador.setDmg(jugador.getDmg() + casilla.getReward());
                }
                break;
            case BOSS:
                jugador.setDmg(jugador.getDmg() + casilla.getReward());
                jugador.setVelocidad(jugador.getVelocidad() + casilla.getReward1());
                break;
        }
    }

    public void comprobarFinPartida() {
        if (jugador.getSalud() <= 0) {
            partidaAcabada = true;
        }
    }

    private void avanzar() {
        if (jugador.getCasillaActual() < etapas[jugador.getEtapaActual()].getCasillas().length - 1) {
            jugador.setCasillaActual(jugador.getCasillaActual() + 1);
        } else if (jugador.getEtapaActual() < etapas.length - 1) {
            jugador.setEtapaActual(jugador.getEtapaActual() + 1);
            jugador.setCasillaActual(0);
        } else {
            partidaAcabada = true;
        }
    }
}
