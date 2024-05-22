package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tablero {
    private Etapa[] etapas;
    private Jugador jugador;
    private boolean partidaAcabada;

    public Tablero(int clientID) {
        // Inicializa el tablero y el jugador
        this.etapas = new Etapa[2]; // Cambiado a 2 etapas para pruebas
        this.jugador = new Jugador("Jugador" + clientID, 100, 0, 1.0f, 10.0f); // Ejemplo con velocidad y daño predeterminados
        this.partidaAcabada = false;
        inicializarTablero();
    }

    private void inicializarTablero() {
        for (int i = 0; i < etapas.length; i++) {
            etapas[i] = new Etapa(i + 1);
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
        Casilla casillaActual = etapas[jugador.getEtapaActual()].getCasillas()[jugador.getCasillaActual()];
        if (casillaActual.getEstado() == Casilla.Estado.EN_COMBATE && casillaActual.isAlive()) {
            casillaActual.takeDamage(jugador.getDmg());
            if (casillaActual.getHealth() <= 0) {
                casillaActual.setEstado(Casilla.Estado.MUERTO);
                jugador.setOro(jugador.getOro() + casillaActual.getReward());
                avanzar(); // Avanzar a la siguiente casilla o etapa si el enemigo es derrotado
            }
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    // Guardar el estado de la partida en un archivo JSON
    public void guardarEstadoJuego() {
        String nombreArchivo = jugador.getNombre() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".json";
        String rutaArchivo = Paths.get("_saves", nombreArchivo).toString();

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(this.toJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPartidaAcabada() {
        return partidaAcabada;
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
