package com.bonet.threaddungeons;

public class Tablero {
    private final Etapa[] etapas;
    private Jugador jugador;
    private boolean partidaAcabada;

    public Tablero(int clientID) {
        this.etapas = new Etapa[2];
        this.jugador = new Jugador("Jugador" + clientID, 100, 100, 0, 1.0f, 10.0f, 0, 0);
        this.partidaAcabada = false;

        // Inicializar tablero
        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa(i);
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

    public void avanzar() {
        if (jugador.getCasillaActual() < etapas[jugador.getEtapaActual()].getCasillas().length - 1) {
            jugador.setCasillaActual(jugador.getCasillaActual() + 1);
        } else if (jugador.getEtapaActual() < etapas.length - 1) {
            jugador.setEtapaActual(jugador.getEtapaActual() + 1);
            jugador.setCasillaActual(0);
        } else {
            partidaAcabada = true;
        }
    }

    public void saltar() {
        if (jugador.getCasillaActual() < etapas[jugador.getEtapaActual()].getCasillas().length - 2) {
            jugador.setCasillaActual(jugador.getCasillaActual() + 2);
        } else if (jugador.getEtapaActual() < etapas.length - 1) {
            jugador.setEtapaActual(jugador.getEtapaActual() + 1);
            jugador.setCasillaActual(0);
        } else {
            partidaAcabada = true;
        }
    }

    public void atacar() {
        Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
        if (casillaActual.getEstado() == Casilla.Estado.EN_COMBATE && casillaActual.isAlive()) {
            casillaActual.takeDamage(jugador.getDmg());
            if (casillaActual.getHealth() <= 0) {
                casillaActual.setEstado(Casilla.Estado.MUERTO);
                jugador.setOro(jugador.getOro() + casillaActual.getReward());
                avanzar();
            }
        }
    }

    public void actualizarProgresoJuego() {
        if (jugador.getSalud() <= 0) {
            partidaAcabada = true;
        }
    }
}
