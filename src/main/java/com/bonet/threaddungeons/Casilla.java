package com.bonet.threaddungeons;

import java.util.Random;

public class Casilla {
    protected char icon;
    protected boolean isAlive = true;

    protected enum modeEnum {NORMAL, REWARD, RANDOM}

    protected modeEnum mode = modeEnum.NORMAL;

    protected float damage = 4.5f;

    public Casilla() {}

    public Casilla(modeEnum mode) {
        this.mode = mode;
        if (this.mode == modeEnum.NORMAL)
            this.icon = 'O';
        else if (this.mode == modeEnum.REWARD)
            this.icon = 'Ô';
        else
            this.icon = 'R';

        Random rnd = new Random();
        this.damage = rnd.nextInt(1,5);
    }

    @Override
    public String toString() {
        return icon + " ";
        //return "\nCasilla:" + " Icon: " + icon + " - isAlive:" + isAlive + " - mode:" + mode;
    }
}
