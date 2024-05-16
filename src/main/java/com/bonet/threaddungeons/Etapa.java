package com.bonet.threaddungeons;

import java.util.Random;

public class Etapa {
    private static int counter = 0;
    private boolean isAlive = true;
    private int id;
    private final int numCasillas = 5;
    private Casilla[] casillas = new Casilla[numCasillas];
    private transient Random rnd = new Random(); // Marcar como transient para excluirlo de la serialización

    public Etapa() {
        id = counter++;
        casillas[0] = new Casilla(Casilla.Mode.NORMAL);
        for (int i = 1; i < casillas.length - 1; i++) {
            int numAleatorio = rnd.nextInt(3);
            if (numAleatorio == 0 || numAleatorio == 1) {
                casillas[i] = new Casilla(Casilla.Mode.NORMAL);
            } else {
                casillas[i] = new Casilla(Casilla.Mode.REWARD);
            }
        }
        casillas[casillas.length - 1] = new Casilla(Casilla.Mode.RANDOM);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Casilla[] getCasillas() {
        return casillas;
    }

    public int getId() {
        return id;
    }
}