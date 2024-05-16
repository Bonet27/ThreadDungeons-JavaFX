package com.bonet.threaddungeons;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;

public class Tablero {
    public Etapa[] etapas; // Cambiado a público para acceso a Gson
    public Jugador jugador; // Cambiado a público para acceso a Gson
    public boolean partidaAcabada;

    public Tablero(int clientID) {
        // Inicializa el tablero y el jugador
        this.etapas = new Etapa[5];
        this.jugador = new Jugador("Jugador" + clientID, 100, 0, 1.0f, 10.0f); // Ejemplo con velocidad y daño predeterminados
        this.partidaAcabada = false;
        inicializarTablero();
    }

    public void resetJuego() {
        this.partidaAcabada = false;
        inicializarTablero();
    }

    private void inicializarTablero() {
        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa(); // Inicializa cada etapa
        }
    }

    public void avanzar() {
        // Lógica para avanzar a la siguiente casilla o etapa
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
        // Lógica para saltar una casilla
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
        // Lógica para atacar la casilla actual
        Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
        casillaActual.takeDamage(jugador.getDaño());
        if (!casillaActual.isAlive) {
            jugador.setOro(jugador.getOro() + casillaActual.getReward());
        }
    }

    public void actualizarProgresoJuego() {
        // Lógica para actualizar el progreso del juego
        if (jugador.getSalud() <= 0) {
            partidaAcabada = true;
        }
    }

    public void acabarPartida(Tablero tablero, DataOutputStream flujo_salida, String mensaje) {
        try {
            flujo_salida.writeUTF(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para convertir el tablero a JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Getters y Setters para las propiedades necesarias
    public Etapa[] getEtapas() {
        return etapas;
    }

    public void setEtapas(Etapa[] etapas) {
        this.etapas = etapas;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
}
