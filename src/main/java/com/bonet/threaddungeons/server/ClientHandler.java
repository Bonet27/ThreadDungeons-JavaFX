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
    private static Logger logger; // Logger para registrar eventos
    private Socket clientSocket; // Socket del cliente
    private DataInputStream input; // Flujo de entrada de datos
    private DataOutputStream output; // Flujo de salida de datos
    private Tablero tablero; // Estado del juego del usuario
    private Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Instancia Gson para convertir objetos a JSON
    private int userId; // ID del usuario
    private Usuario user; // Información del usuario

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            input = new DataInputStream(clientSocket.getInputStream()); // Inicializar flujo de entrada
            output = new DataOutputStream(clientSocket.getOutputStream()); // Inicializar flujo de salida

            while (true) {
                String action = input.readUTF(); // Leer acción del cliente
                switch (action) {
                    case "register":
                        handleRegister(); // Manejar registro
                        break;
                    case "login":
                        handleLogin(); // Manejar inicio de sesión
                        break;
                    case "topScores":
                        handleTopScores(); // Manejar solicitud de top scores
                        break;
                    default:
                        logger.warn("Acción no válida: " + action); // Advertencia de acción no válida
                        break;
                }
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente: " + e.getMessage()); // Registrar error
        } finally {
            closeResources(); // Cerrar recursos
        }
    }

    // Manejar solicitud de top scores
    private void handleTopScores() {
        try {
            List<Score> topScores = DatabaseManager.getTopScores(15); // Obtener top scores
            String jsonScores = gson.toJson(topScores); // Convertir a JSON
            synchronized (output) {
                output.writeUTF("TOP_SCORES"); // Escribir tipo de mensaje
                output.writeUTF(jsonScores); // Escribir datos
                output.flush(); // Asegurar que los datos se envíen
            }
        } catch (IOException e) {
            logger.error("Error al enviar los top scores al cliente: " + e.getMessage()); // Registrar error
        }
    }

    // Manejar registro de usuario
    private void handleRegister() {
        try {
            String login = input.readUTF(); // Leer login
            String password = input.readUTF(); // Leer contraseña
            String email = input.readUTF(); // Leer email

            boolean registered = DatabaseManager.registerUser(login, password, email); // Registrar usuario
            synchronized (output) {
                output.writeUTF("REGISTER_RESPONSE"); // Escribir tipo de respuesta
                output.writeBoolean(registered); // Escribir estado de registro
                output.flush(); // Asegurar que los datos se envíen
            }

            if (registered) {
                logger.info("Usuario registrado: " + login); // Registrar éxito
            } else {
                logger.warn("Fallo en el registro del usuario: " + login); // Registrar fallo
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente durante el registro: " + e.getMessage()); // Registrar error
        }
    }

    // Manejar inicio de sesión
    private void handleLogin() {
        try {
            String login = input.readUTF(); // Leer login
            String password = input.readUTF(); // Leer contraseña

            boolean authenticated = DatabaseManager.authenticateUser(login, password); // Autenticar usuario
            user = DatabaseManager.getUserByLogin(login); // Obtener usuario

            synchronized (output) {
                output.writeUTF("LOGIN_RESPONSE"); // Escribir tipo de respuesta
                output.writeBoolean(authenticated); // Escribir estado de autenticación
                output.flush(); // Asegurar que los datos se envíen
            }

            if (authenticated && user != null) {
                userId = user.getId(); // Asignar ID del usuario
                logger = LoggerUtility.getLogger(ClientHandler.class, user.getLogin() + "_" + userId); // Inicializar logger para el usuario

                tablero = DatabaseManager.getTableroByUserId(userId); // Obtener tablero del usuario
                if (tablero == null || tablero.isPartidaAcabada()) {
                    tablero = new Tablero(user.getLogin()); // Crear nuevo tablero si no existe o la partida está acabada
                    DatabaseManager.saveTablero(userId, tablero); // Guardar nuevo tablero
                }

                logger.info("Usuario autenticado: " + user.getLogin()); // Registrar autenticación
                enviarEstadoJuego(); // Enviar estado del juego al cliente

                // Bucle para manejar mensajes del cliente mientras la partida no esté acabada
                while (!tablero.isPartidaAcabada() && !clientSocket.isClosed()) {
                    if (input.available() > 0) {
                        String mensaje = input.readUTF(); // Leer mensaje
                        logger.info("Mensaje recibido del cliente: " + mensaje); // Registrar mensaje
                        procesarMensaje(mensaje); // Procesar mensaje
                        enviarEstadoJuego(); // Enviar estado del juego al cliente
                    }
                }

                if (tablero.isPartidaAcabada()) {
                    DatabaseManager.saveScore(user, tablero); // Guardar puntaje al finalizar la partida
                    enviarTopScores(); // Enviar top scores al cliente
                }
            } else {
                logger.info("Autenticación fallida para el cliente: " + clientSocket.getInetAddress().getHostAddress()); // Registrar fallo de autenticación
                clientSocket.close(); // Cerrar socket del cliente
            }
        } catch (IOException e) {
            logger.error("Error en la comunicación con el cliente durante el login: " + e.getMessage()); // Registrar error
        }
    }

    // Procesar mensajes recibidos del cliente
    private void procesarMensaje(String mensaje) {
        switch (mensaje) {
            case "1":
                iniciarCombate(); // Iniciar combate
                logger.info("Procesando mensaje de iniciar ataque");
                break;
            case "2":
                boolean saltoExitoso = tablero.saltar(); // Intentar saltar
                if (!saltoExitoso) {
                    iniciarCombate(); // Iniciar combate si no se puede saltar
                }
                logger.info("Procesando mensaje de saltar");
                break;
            case "3":
                tablero.setPartidaAcabada(true); // Marcar partida como acabada
                logger.info("Procesando mensaje de acabar la partida");
                DatabaseManager.saveScore(user, tablero); // Guardar puntaje
                break;
            case "4":
                logger.info("Procesando mensaje de ataque");
                tablero.atacar(); // Atacar
                break;
            case "5":
                logger.info("Procesando mensaje de daño recibido");
                Casilla casillaActual = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()];
                tablero.getJugador().takeDamage(casillaActual.getDamage()); // Tomar daño
                break;
            case "topScores":
                enviarTopScores(); // Enviar top scores
                break;
            default:
                logger.warn("Mensaje no válido: " + mensaje); // Advertir mensaje no válido
        }
        tablero.comprobarFinPartida(); // Comprobar si la partida ha acabado
        DatabaseManager.saveTablero(userId, tablero); // Guardar estado del tablero
        if (tablero.isPartidaAcabada())
            DatabaseManager.saveScore(user, tablero); // Guardar puntaje al finalizar la partida

        enviarEstadoJuego(); // Enviar estado del juego al cliente
    }

    // Iniciar combate en la casilla actual
    private void iniciarCombate() {
        Casilla casillaActual = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()];
        if (casillaActual.getEstado() == Casilla.Estado.SIN_ATACAR) {
            tablero.iniciarCombate(casillaActual);
            enviarEstadoJuego();
        } else {
            logger.info("El combate ya está en progreso para esta casilla.");
        }
    }

    // Enviar estado del juego al cliente
    private void enviarEstadoJuego() {
        try {
            String estadoJuego = gson.toJson(tablero); // Convertir estado del juego a JSON
            synchronized (output) {
                output.writeUTF("GAME_STATE"); // Escribir tipo de mensaje
                output.writeUTF(estadoJuego); // Escribir estado del juego
                output.flush(); // Asegurar que los datos se envíen
            }
        } catch (IOException e) {
            logger.error("Error al enviar el estado del juego al cliente: " + e.getMessage()); // Registrar error
        }
    }

    // Enviar top scores al cliente
    private void enviarTopScores() {
        try {
            List<Score> topScores = DatabaseManager.getTopScores(15); // Obtener top scores
            String jsonScores = gson.toJson(topScores); // Convertir a JSON
            synchronized (output) {
                output.writeUTF("TOP_SCORES"); // Escribir tipo de mensaje
                output.writeUTF(jsonScores); // Escribir datos
                output.flush(); // Asegurar que los datos se envíen
            }
        } catch (IOException e) {
            logger.error("Error al enviar los top scores al cliente: " + e.getMessage()); // Registrar error
        }
    }

    // Cerrar recursos
    private void closeResources() {
        try {
            if (input != null) {
                input.close(); // Cerrar flujo de entrada
            }
            if (output != null) {
                output.close(); // Cerrar flujo de salida
            }
        } catch (IOException e) {
            logger.error("Error al cerrar recursos: " + e.getMessage()); // Registrar error
        }
    }
}
