package com.bonet.threaddungeons;

import java.util.Random;

public class Tablero {
    private final Etapa[] etapas; // Array de etapas
    private Jugador jugador; // Información del jugador
    private boolean partidaAcabada; // Estado de la partida

    public Tablero(String username) {
        this.etapas = new Etapa[5]; // Inicializar con 5 etapas
        this.jugador = new Jugador(username, 100, 100, 0, 1.0f, 10.0f, 0, 0); // Inicializar jugador
        this.partidaAcabada = false; // La partida no ha acabado

        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa(i, i + 1); // Inicializar cada etapa
        }
    }

    public boolean isPartidaAcabada() { return partidaAcabada; }

    public Etapa[] getEtapas() { return etapas; }

    public Jugador getJugador() { return jugador; }

    public void setPartidaAcabada(boolean partidaAcabada) { this.partidaAcabada = partidaAcabada; }

    public void iniciarCombate(Casilla casillaActual) {
        casillaActual.setEstado(Casilla.Estado.EN_COMBATE); // Marcar la casilla como en combate
    }

    public boolean saltar() {
        Random rdn = new Random();
        var num = rdn.nextFloat(0f, 1f);
        if (num < 0.75f) {
            avanzar(); // Avanzar si el salto es exitoso
            return true;
        } else {
            Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
            iniciarCombate(casillaActual); // Iniciar combate si el salto falla
            return false;
        }
    }

    public void atacar() {
        Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
        if (casillaActual.isAlive()) {
            casillaActual.takeDamage(jugador.getDmg()); // Aplicar daño a la casilla
            if (casillaActual.getHealth() <= 0) {
                aplicarRecompensa(casillaActual); // Aplicar recompensa si la casilla muere
                avanzar(); // Avanzar a la siguiente casilla
            }
        }
    }

    private void aplicarRecompensa(Casilla casilla) {
        switch (casilla.getMode()) {
            case NORMAL:
            case REWARD:
                jugador.setOro(jugador.getOro() + casilla.getReward()); // Incrementar oro del jugador
                break;
            case RANDOM:
                if (casilla.getRewardText().contains("salud")) {
                    jugador.setSalud(Math.min(jugador.getSaludMaxima(), jugador.getSalud() + casilla.getReward())); // Incrementar salud del jugador
                } else if (casilla.getRewardText().contains("daño")) {
                    jugador.setDmg(jugador.getDmg() + casilla.getReward()); // Incrementar daño del jugador
                }
                break;
            case BOSS:
                jugador.setDmg(jugador.getDmg() + casilla.getReward()); // Incrementar daño del jugador
                jugador.setVelocidad(jugador.getVelocidad() + casilla.getReward1()); // Incrementar velocidad del jugador
                break;
        }
    }

    public void comprobarFinPartida() {
        if (jugador.getSalud() <= 0) {
            partidaAcabada = true; // Marcar partida como acabada si el jugador muere
        } else {
            boolean todasCasillasCompletadas = true;
            for (Etapa etapa : etapas) {
                for (Casilla casilla : etapa.getCasillas()) {
                    if (!casilla.isMuerto()) {
                        todasCasillasCompletadas = false;
                        break;
                    }
                }
            }
            if (todasCasillasCompletadas) {
                partidaAcabada = true; // Marcar partida como acabada si todas las casillas están completadas
            }
        }
    }

    private void avanzar() {
        if (jugador.getCasillaActual() < etapas[jugador.getEtapaActual()].getCasillas().length - 1) {
            jugador.setCasillaActual(jugador.getCasillaActual() + 1); // Avanzar a la siguiente casilla
        } else if (jugador.getEtapaActual() < etapas.length - 1) {
            jugador.setEtapaActual(jugador.getEtapaActual() + 1); // Avanzar a la siguiente etapa
            jugador.setCasillaActual(0); // Reiniciar a la primera casilla
        } else {
            partidaAcabada = true; // Marcar partida como acabada si se completan todas las etapas
        }
        comprobarFinPartida(); // Comprobar si la partida ha acabado
    }
}
