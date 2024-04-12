package com.bonet.threaddungeons;

import java.util.Random;

public class Etapa {
    protected boolean isAlive = true;
    protected int id;
    protected int counter = 0;
    protected final int numCasillas = 5;
    protected Casilla[] casillas = new Casilla[numCasillas];
    Random rnd = new Random();

    public Etapa() {
        id = counter++;
        casillas[0] = new Casilla(Casilla.modeEnum.NORMAL);
        for (int i = 1; i < casillas.length-1; i++) {
            int numAleatorio = rnd.nextInt(3);
            if (numAleatorio == 0 || numAleatorio == 1)
                casillas[i] = new Casilla(Casilla.modeEnum.NORMAL);
            else
                casillas[i] = new Casilla(Casilla.modeEnum.REWARD);
        }
        casillas[casillas.length - 1] = new Casilla(Casilla.modeEnum.RANDOM);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public Casilla[] getCasillas() {
        return casillas;
    }
}
