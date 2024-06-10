package com.bonet.threaddungeons;

import java.util.Random;

public class Etapa {
    private static final int numCasillas = 5;
    private Casilla[] casillas;
    private int numeroEtapa;

    public Etapa(int numeroEtapa, int dificultMultiplier) {
        this.casillas = new Casilla[numCasillas];
        this.numeroEtapa = numeroEtapa;

        // Ajuste del constructor de Casilla para incluir la etapa y el índice de la casilla
        casillas[0] = new Casilla(Casilla.Mode.NORMAL, dificultMultiplier, numeroEtapa, 0);
        casillas[4] = new Casilla(Casilla.Mode.BOSS, dificultMultiplier, numeroEtapa, 4);

        for (int i = 1; i < 4; i++) {
            casillas[i] = new Casilla(Casilla.Mode.values()[new Random().nextInt(3)], dificultMultiplier, numeroEtapa, i);
        }
    }

    public Casilla[] getCasillas() {
        return casillas;
    }
}
