package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.DatabaseManager;
import com.bonet.threaddungeons.LoggerUtility;
import com.bonet.threaddungeons.Tablero;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private static Logger logger;
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private Tablero tablero;
    private AtomicBoolean partidaAcabada = new AtomicBoolean(false);
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private int userId;

    public ClientHandler(Socket clientSocket, int userId) {
        this.clientSocket = clientSocket;
        this.userId = userId;
        logger = LoggerUtility.getLogger(ClientHandler.class, "usuario" + userId);

        // Verificar si el usuario tiene un tablero en la base de datos
        this.tablero = DatabaseManager.getTableroByUserId(userId);
        if (this.tablero == null) {
            // Si no tiene un tablero, crear uno por defecto
            this.tablero = new Tablero(userId);
            DatabaseManager.saveTablero(userId, this.tablero); // Guardar el nuevo tablero en la base de datos
        }
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            // Enviar estado inicial del juego
            enviarEstadoJuego();

            while (!partidaAcabada.get() && !clientSocket.isClosed()) {
                try {
                    if (input.available() > 0) {
                        String mensaje = input.readUTF();
                        logger.info("Mensaje recibido del cliente: " + mensaje);
                        procesarMensaje(mensaje);
                        enviarEstadoJuego();
                    }
                } catch (SocketException e) {
                    logger.warn("Client connection reset: " + e.getMessage());
                    break;
                } catch (EOFException e) {
                    logger.info("Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
                    break;
                } catch (IOException e) {
                    logger.error("Error en la comunicación con el cliente: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Error setting up client handler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private void procesarMensaje(String mensaje) {
        switch (mensaje) {
            case "1": // Hacer daño durante el combate
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
            case "inicio_combate": // Iniciar el combate
                tablero.iniciarCombate();
                break;
            default:
                logger.warn("Mensaje no válido: " + mensaje);
        }
        tablero.actualizarProgresoJuego();
        DatabaseManager.saveTablero(userId, tablero); // Save the tablero to the database after processing the message
    }

    private void enviarEstadoJuego() {
        try {
            String estadoJuego = gson.toJson(tablero);
            logger.info("Enviando estado del juego al cliente: " + estadoJuego);
            output.writeUTF(estadoJuego);
        } catch (IOException e) {
            logger.error("Error al enviar el estado del juego al cliente: " + e.getMessage());
        }
    }

    private void closeResources() {
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
            logger.error("Error closing resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
