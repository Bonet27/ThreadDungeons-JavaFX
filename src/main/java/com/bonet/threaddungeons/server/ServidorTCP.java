package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.Tablero;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTCP {
    private static final int Puerto = 2000;
    private static AtomicBoolean running = new AtomicBoolean(true);
    private static ExecutorService threadPool = Executors.newCachedThreadPool(); // Use a thread pool

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            System.out.println("Servidor iniciado en el puerto " + Puerto);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                try {
                    serverSocket.close();
                    threadPool.shutdownNow(); // Shutdown the thread pool
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
                    threadPool.submit(new ClientHandler(clientSocket)); // Submit client handler to thread pool
                } catch (IOException e) {
                    if (running.get()) {
                        System.err.println("Error en el servidor: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Tablero tablero;
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.tablero = new Tablero(1); // Asignar un ID de cliente (por simplicidad, siempre 1 en este ejemplo)
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                out.writeUTF(gson.toJson(tablero));

                boolean clientRunning = true;
                while (clientRunning) {
                    String message = in.readUTF();
                    switch (message) {
                        case "1": // Iniciar combate
                            tablero.getJugador().takeDamage(10); // Ejemplo de lógica, ajustar según sea necesario
                            break;
                        case "ATTACK":
                            tablero.atacar();
                            break;
                        case "2": // Saltar casilla
                            tablero.saltar();
                            break;
                        case "3": // Fin de juego
                            clientRunning = false;
                            break;
                        default:
                            break;
                    }
                    out.writeUTF(gson.toJson(tablero));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
