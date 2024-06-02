package com.bonet.threaddungeons;

import java.util.Random;

public class Etapa {
    private static final int numCasillas = 5;
    private Casilla[] casillas;
    private int numeroEtapa = 0;

    public Etapa(int numeroEtapa, int dificultMultiplier) {
        this.casillas = new Casilla[numCasillas];
        this.numeroEtapa = numeroEtapa;

        Random random = new Random();
        casillas[0] = new Casilla(Casilla.Mode.NORMAL, dificultMultiplier);
        casillas[4] = new Casilla(Casilla.Mode.BOSS, dificultMultiplier);

        for (int i = 1; i < 4; i++) {
            casillas[i] = new Casilla(random.nextBoolean() ? Casilla.Mode.REWARD : Casilla.Mode.RANDOM, dificultMultiplier + numeroEtapa);
        }
    }

    public Casilla[] getCasillas() {
        return casillas;
    }
}
