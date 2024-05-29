package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.DatabaseManager;
import com.bonet.threaddungeons.LoggerUtility;
import com.bonet.threaddungeons.Usuario;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTCP {
    private static final int Puerto = 2000;
    private static AtomicBoolean running = new AtomicBoolean(true);
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerUtility.getLogger();

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase(); // Inicializar la base de datos

        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            logger.info("Servidor iniciado en el puerto " + Puerto);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                try {
                    serverSocket.close();
                    threadPool.shutdownNow();
                    logger.info("Servidor cerrado");
                } catch (IOException e) {
                    logger.error("Error al cerrar el servidor: " + e.getMessage());
                    e.printStackTrace();
                }
            }));

            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                    DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                    DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

                    // Autenticación del usuario
                    String login = input.readUTF();
                    String password = input.readUTF();

                    boolean authenticated = DatabaseManager.authenticateUser(login, password);
                    Usuario usuario = DatabaseManager.getUserByLogin(login);

                    output.writeBoolean(authenticated);
                    output.flush();

                    if (authenticated && usuario != null) {
                        logger.info("Usuario autenticado: " + usuario.getNombre());
                        threadPool.submit(new ClientHandler(clientSocket, usuario.getId()));
                    } else {
                        logger.info("Autenticación fallida para el cliente: " + clientSocket.getInetAddress().getHostAddress());
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (running.get()) {
                        logger.error("Error en el servidor: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
