package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.Tablero;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bonet.threaddungeons.server.ServidorTCP.SAVE_DIR;

public class ServidorTCP {
    private static final int Puerto = 2000;
    protected static final String SAVE_DIR = "_saves";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            System.out.println("Servidor iniciado en el puerto " + Puerto);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private Tablero tablero;
    private AtomicBoolean partidaAcabada = new AtomicBoolean(false);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.tablero = loadOrCreateTablero(clientSocket.getInetAddress().getHostAddress());
    }

    private Tablero loadOrCreateTablero(String clientIp) {
        File saveFile = new File(SAVE_DIR, clientIp + ".json");
        if (saveFile.exists()) {
            try (FileReader reader = new FileReader(saveFile)) {
                return gson.fromJson(reader, Tablero.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Tablero(1); // Inicializar con un ID de cliente predeterminado
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            // Enviar estado inicial del juego
            enviarEstadoJuego();

            while (!partidaAcabada.get()) {
                try {
                    if (input.available() > 0) {
                        int opcion = Integer.parseInt(input.readUTF());
                        procesarOpcion(opcion);
                        enviarEstadoJuego();
                    }
                } catch (EOFException e) {
                    System.out.println("Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
                    break;
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
                if (tablero.isPartidaAcabada()) {
                    partidaAcabada.set(true);
                }
                break;
            case 2: // Saltar
                tablero.saltar();
                break;
            case 3: // Salir de la partida
                partidaAcabada.set(true);
                break;
            default:
                System.out.println("Opción no válida");
        }
        tablero.actualizarProgresoJuego();
    }

    private void enviarEstadoJuego() throws IOException {
        String estadoJuego = gson.toJson(tablero);
        output.writeUTF(estadoJuego);
        guardarEstadoJuego(estadoJuego);
    }

    private void guardarEstadoJuego(String estadoJuego) {
        try {
            File dir = new File(SAVE_DIR);
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
