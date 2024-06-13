package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private static Logger logger;
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private Tablero tablero;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private int userId;
    private Usuario user;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());

            while (true) {
                String action = input.readUTF();
                if (action.equals("register")) {
                    handleRegister();
                } else if (action.equals("login")) {
                    handleLogin();
                } else if (action.equals("topScores")) {
                    handleTopScores();
                }
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void handleTopScores() {
        try {
            List<Score> topScores = DatabaseManager.getTopScores(15);
            String jsonScores = gson.toJson(topScores); // Usar Gson para convertir a JSON
            synchronized (output) {
                output.writeUTF("TOP_SCORES");
                output.writeUTF(jsonScores);
                output.flush();
            }
        } catch (IOException e) {
            logger.error("Error al enviar los top scores al cliente: " + e.getMessage());
        }
    }

    private void handleRegister() {
        try {
            String login = input.readUTF();
            String password = input.readUTF();
            String email = input.readUTF();

            boolean registered = DatabaseManager.registerUser(login, password, email);
            synchronized (output) {
                output.writeUTF("REGISTER_RESPONSE");
                output.writeBoolean(registered);
                output.flush();
            }

            if (registered) {
                logger.info("Usuario registrado: " + login);
            } else {
                logger.warn("Fallo en el registro del usuario: " + login);
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente durante el registro: " + e.getMessage());
        }
    }

    private void handleLogin() {
        try {
            String login = input.readUTF();
            String password = input.readUTF();

            boolean authenticated = DatabaseManager.authenticateUser(login, password);
            user = DatabaseManager.getUserByLogin(login);

            synchronized (output) {
                output.writeUTF("LOGIN_RESPONSE");
                output.writeBoolean(authenticated);
                output.flush();
            }

            if (authenticated && user != null) {
                userId = user.getId();
                logger = LoggerUtility.getLogger(ClientHandler.class, user.getLogin() + "_" + userId);

                tablero = DatabaseManager.getTableroByUserId(userId);
                if (tablero == null || tablero.isPartidaAcabada()) {
                    tablero = new Tablero(user.getLogin());
                    DatabaseManager.saveTablero(userId, tablero);
                }

                logger.info("Usuario autenticado: " + user.getLogin());
                enviarEstadoJuego();

                while (!tablero.isPartidaAcabada() && !clientSocket.isClosed()) {
                    if (input.available() > 0) {
                        String mensaje = input.readUTF();
                        logger.info("Mensaje recibido del cliente: " + mensaje);
                        procesarMensaje(mensaje);
                        enviarEstadoJuego();
                    }
                }
            } else {
                logger.info("Autenticación fallida para el cliente: " + clientSocket.getInetAddress().getHostAddress());
                clientSocket.close();
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente durante el login: " + e.getMessage());
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
                tablero.setPartidaAcabada(true);
                logger.info("Procesando mensaje de acabar la partida");
                DatabaseManager.saveScore(user, tablero);
                break;
            case "4":
                logger.info("Procesando mensaje de ataque");
                tablero.atacar();
                break;
            case "5":
                logger.info("Procesando mensaje de daño recibido");
                Casilla casillaActual = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()];
                tablero.getJugador().takeDamage(casillaActual.getDamage());
                break;
            case "topScores":
                enviarTopScores();
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
            synchronized (output) {
                output.writeUTF("GAME_STATE");
                output.writeUTF(estadoJuego);
                output.flush();
            }
        } catch (IOException e) {
            logger.error("Error al enviar el estado del juego al cliente: " + e.getMessage());
        }
    }

    private void enviarTopScores() {
        try {
            List<Score> topScores = DatabaseManager.getTopScores(15);
            String jsonScores = gson.toJson(topScores);
            synchronized (output) {
                output.writeUTF("TOP_SCORES");
                output.writeUTF(jsonScores);
                output.flush();
            }
        } catch (IOException e) {
            logger.error("Error al enviar los top scores al cliente: " + e.getMessage());
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
        } catch (IOException e) {
            logger.error("Error al cerrar recursos: " + e.getMessage());
        }
    }
}
