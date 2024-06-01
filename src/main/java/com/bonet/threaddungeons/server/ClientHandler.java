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

        this.tablero = DatabaseManager.getTableroByUserId(userId);
        if (this.tablero == null || this.tablero.isPartidaAcabada()) {
            this.tablero = new Tablero(userId);
            DatabaseManager.saveTablero(userId, this.tablero);
        }
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            enviarEstadoJuego();

            while (!partidaAcabada.get() && !clientSocket.isClosed()) {
                try {
                    if (input.available() > 0) {
                        String mensaje = input.readUTF();
                        logger.info("Mensaje recibido del cliente: " + mensaje);
                        System.out.println(mensaje);
                        procesarMensaje(mensaje);
                        enviarEstadoJuego();
                    }
                } catch (SocketException e) {
                    logger.warn("Client connection reset: " + e.getMessage());
                    System.out.println(e.getMessage());
                    break;
                } catch (EOFException e) {
                    logger.info("Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
                    System.out.println(e.getMessage());
                    break;
                } catch (IOException e) {
                    logger.error("Error en la comunicación con el cliente: " + e.getMessage());
                    System.out.println(e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Error setting up client handler: " + e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            logger.info("Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
            closeResources();
        }
    }

    private void procesarMensaje(String mensaje) {
        switch (mensaje) {
            case "1":
                tablero.iniciarCombate(tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()]);
                logger.info("Procesando mensaje de iniciar ataque");
                break;
            case "2":
                tablero.saltar();
                logger.info("Procesando mensaje de saltar");
                break;
            case "3":
                partidaAcabada.set(true);
                tablero.setPartidaAcabada(true);
                logger.info("Procesando mensaje de acabar la partida");
                break;
            case "4":
                logger.info("Procesando mensaje de ataque");
                tablero.atacar();
                break;
            default:
                logger.warn("Mensaje no válido: " + mensaje);
        }
        tablero.comprobarFinPartida();
        DatabaseManager.saveTablero(userId, tablero);
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
            System.out.println(e.getMessage());
        }
    }
}
