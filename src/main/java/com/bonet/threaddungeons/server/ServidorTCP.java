package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.Tablero;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorTCP {
    private static AtomicInteger counter = new AtomicInteger(0);
    private static final int Puerto = 2000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            System.out.println("Servidor iniciado en el puerto " + Puerto);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, counter.incrementAndGet());
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private int clientID;
    private DataInputStream input;
    private DataOutputStream output;
    private Tablero tablero;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ClientHandler(Socket clientSocket, int clientID) {
        this.clientSocket = clientSocket;
        this.clientID = clientID;
        this.tablero = new Tablero(clientID);
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            // Enviar estado inicial del juego
            enviarEstadoJuego();

            while (!tablero.partidaAcabada) {
                try {
                    int opcion = Integer.parseInt(input.readUTF());
                    procesarOpcion(opcion);
                    enviarEstadoJuego();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1: // Atacar
                tablero.atacar();
                if (!tablero.partidaAcabada) {
                    tablero.avanzar(); // Avanza automáticamente después de atacar
                }
                break;
            case 2: // Saltar
                tablero.saltar();
                break;
            case 3: // Salir de la partida
                tablero.partidaAcabada = true;
                break;
            default:
                System.out.println("Opción no válida");
        }
        tablero.actualizarProgresoJuego();
    }

    private void enviarEstadoJuego() throws IOException {
        String estadoJuego = tablero.toJson();
        output.writeUTF(estadoJuego);
        // Imprimir el estado del tablero en JSON estilizado
        System.out.println(gson.toJson(tablero));
    }
}
