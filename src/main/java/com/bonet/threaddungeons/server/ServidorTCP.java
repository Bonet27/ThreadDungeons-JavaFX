package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.Tablero;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTCP {
    private static final int Puerto = 2000;
    private static AtomicBoolean running = new AtomicBoolean(true);
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            System.out.println("Servidor iniciado en el puerto " + Puerto);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                try {
                    serverSocket.close();
                    threadPool.shutdownNow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());
                    threadPool.submit(new ClientHandler(clientSocket));
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
        private final Socket clientSocket;
        private Tablero tablero;
        private Gson gson = new GsonBuilder().setPrettyPrinting().create();

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.tablero = new Tablero(1);
        }

        @Override
        public void run() {
            try (DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                 DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream())) {

                enviarEstadoJuego(out);

                boolean clientRunning = true;
                while (clientRunning && !clientSocket.isClosed()) {
                    try {
                        String message = in.readUTF();
                        clientRunning = procesarMensaje(message);
                        enviarEstadoJuego(out);

                        if (tablero.isPartidaAcabada()) {
                            clientRunning = false;
                        }
                    } catch (EOFException e) {
                        System.out.println("Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
                        break;
                    } catch (IOException e) {
                        if (!clientSocket.isClosed()) {
                            System.out.println("Error en la comunicación con el cliente: " + e.getMessage());
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.out.println("Error al cerrar la conexión del cliente: " + e.getMessage());
                }
            }
        }


        private boolean procesarMensaje(String mensaje) {
            System.out.println("Mensaje recibido del cliente: " + mensaje);
            switch (mensaje) {
                case "ATTACK":
                    tablero.atacar();
                    if (tablero.isPartidaAcabada()) {
                        return false;
                    }
                    break;
                case "2":
                    tablero.saltar();
                    break;
                case "3":
                    return false;
                default:
                    try {
                        Tablero updatedTablero = gson.fromJson(mensaje, Tablero.class);
                        if (updatedTablero != null) {
                            this.tablero = updatedTablero;
                        }
                    } catch (JsonSyntaxException e) {
                        System.out.println("Error al procesar datos del cliente: " + e.getMessage());
                    }
                    break;
            }
            guardarEstadoJuego(gson.toJson(tablero));
            return true;
        }

        private void enviarEstadoJuego(DataOutputStream out) throws IOException {
            String estadoJuego = gson.toJson(tablero);
            System.out.println("Enviando estado del juego al cliente: " + estadoJuego);
            out.writeUTF(estadoJuego);
        }

        private void guardarEstadoJuego(String estadoJuego) {
            try {
                File dir = new File(".saves");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                FileWriter writer = new FileWriter(new File(dir, clientSocket.getInetAddress().getHostAddress() + ".json"));
                writer.write(estadoJuego);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
