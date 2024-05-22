package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.Tablero;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bonet.threaddungeons.server.ServidorTCP.SAVE_DIR;

class ClientHandler implements Runnable {
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
                        String mensaje = input.readUTF();
                        procesarMensaje(mensaje);
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void procesarMensaje(String mensaje) {
        System.out.println("Mensaje recibido del cliente: " + mensaje);
        switch (mensaje) {
            case "1": // Atacar
                tablero.atacar();
                if (tablero.isPartidaAcabada()) {
                    partidaAcabada.set(true);
                }
                break;
            case "2": // Saltar
                tablero.saltar();
                break;
            case "3": // Terminar juego
                partidaAcabada.set(true);
                break;
            default:
                System.out.println("Mensaje no válido");
        }
        tablero.actualizarProgresoJuego();
        enviarEstadoJuego(); // Enviar el estado del juego actualizado después de procesar el mensaje
    }

    private void enviarEstadoJuego() {
        try {
            String estadoJuego = gson.toJson(tablero);
            System.out.println("Enviando estado del juego al cliente: " + estadoJuego);
            output.writeUTF(estadoJuego);
        } catch (IOException e) {
            System.out.println("Error al enviar el estado del juego al cliente: " + e.getMessage());
        }
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
