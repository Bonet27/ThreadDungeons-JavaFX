package com.bonet.threaddungeons;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Hilo extends Thread {
    protected int clientID = 0;
    private Socket sCliente;
    private DataInputStream flujo_entrada;
    private DataOutputStream flujo_salida;
    private String opciones = "Opciones:\n1) Avanzar\n2) Intentar saltar\n3) Salir de la partida\nIntroduce una opcion: ";

    public Hilo(Socket sCliente, DataInputStream flujo_entrada, DataOutputStream flujo_salida, Integer clientID) {
        this.sCliente = sCliente;
        this.flujo_entrada = flujo_entrada;
        this.flujo_salida = flujo_salida;
        this.clientID = clientID;
    }

    @Override
    public void run() {
        Tablero tablero = new Tablero(clientID);
        tablero.resetJuego();
        try {
            while (!tablero.partidaAcabada) {
                try {
                    flujo_salida.writeUTF("\n" + tablero + "\n" + tablero.jugador + "\n\n" + opciones);
                    Integer respuesta = Integer.parseInt(flujo_entrada.readUTF());
                    if (respuesta == 1) {
                        tablero.avanzar();
                    } else if (respuesta == 2) {
                        tablero.saltar();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Cliente " + clientID + ": ! Error en la respuesta recibida | Error: " + e.getMessage());
                    tablero.partidaAcabada = true;
                } catch (IOException e) {
                    System.out.println("Cliente " + clientID+ ": ! Error en la comunicación con el cliente | Error: " + e.getMessage());
                    tablero.partidaAcabada = true;
                } catch (Exception e) {
                    System.out.println("Cliente " + clientID + ": ! Error en el hilo | Error: " + e.getMessage());
                    tablero.partidaAcabada = true;
                }
            }
        } finally {
            try {
                if (sCliente != null && !sCliente.isClosed()) {
                    tablero.acabarPartida(tablero, flujo_salida, "Partida terminada.");
                    sCliente.close();
                    System.out.println("Cliente " + clientID + ": Cerrada la conexion.");
                }
            } catch (IOException e) {
                System.out.println("Cliente " + clientID + ": ! Error cerrando el socket | Error: " + e.getMessage());
            }
        }
    }
}
