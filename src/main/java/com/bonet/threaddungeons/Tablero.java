package com.bonet.threaddungeons;

import java.io.DataOutputStream;
import java.io.IOException;

public class Tablero extends Etapa {
    private int clientID = 0;
    public boolean partidaAcabada = false;
    public int casillaActual = 0;
    private int etapaActual = 0;
    private final int numEtapas = 4;
    private Etapa[] etapas = new Etapa[numEtapas];
    Jugador jugador = new Jugador(100f, 100f, 100f, 100f, 100f);

    public Tablero(Integer clientID) {
        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa();
        }
        this.clientID = clientID;
    }

    public void avanzar() {
        System.out.println("Cliente " + clientID + ": ¡Cliente avanza!");
        if (casillaActual < casillas.length && etapaActual < etapas.length) {
            if (!partidaAcabada)
                atacar();
        }
        System.out.printf("Cliente " + clientID + " - Jugada actual:\n" + this);
    }

    public void saltar() {
        System.out.println("Cliente " + clientID + ": ¡Cliente salta!");
        if (casillaActual < casillas.length && etapaActual < etapas.length) {
            if (!partidaAcabada)
                atacar();

            if (!etapas[etapaActual].casillas[casillaActual].isAlive) {
                etapas[etapaActual].casillas[casillaActual].icon = '-';
                casillaActual++;
            }
        }
        System.out.printf("Cliente " + clientID + " - Jugada actual:\n" + this);
    }

    public void atacar() {
        System.out.println("Cliente " + clientID + ": ¡Cliente ataca!");
        etapas[etapaActual].casillas[casillaActual].health -= jugador.DMG;
        if(etapas[etapaActual].casillas[casillaActual].health <= 0){
            etapas[etapaActual].casillas[casillaActual].isAlive = false;
            etapas[etapaActual].casillas[casillaActual].icon = '-';
            casillaActual++;
        }
        jugadorRecibeDano();
    }

    public void jugadorRecibeDano() {
        jugador.HP -= etapas[etapaActual].casillas[casillaActual].damage;
        if (jugador.HP <= 0.0f) {
            jugador.isAlive = false;
        }
    }

    public void resetJuego() {
        casillaActual = 0;
        etapaActual = 0;
        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa();
        }
        partidaAcabada = false;
    }

    public void actualizarProgresoJuego() {
        for (Etapa etapa : etapas) {
            for (Casilla casilla : etapa.getCasillas()) {
                if (casilla != null) {
                    Casilla enemigo = (Casilla) casilla;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder tableroText = new StringBuilder();
        for (Etapa etapa : etapas) {
            for (Casilla casilla : etapa.getCasillas()) {
                if (casilla != null) {
                    tableroText.append(casilla);
                } else {
                    tableroText.append("[null]");
                }
            }
            tableroText.append("\n");
        }
        return tableroText.toString();
    }

    public boolean acabarPartida(Tablero tablero, DataOutputStream flujo_salida, String mensajeError) {
        try {
            flujo_salida.writeUTF("\n" + tablero.toString() + "\n" + mensajeError);
            System.out.println("Cliente " + clientID + ": Partida terminada.");
            tablero.resetJuego();
        } catch (IOException e) {
            System.out.println("Error enviando el mensaje de fin de juego: " + e.getMessage());
        }
        return false;
    }
}
