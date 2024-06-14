package com.bonet.threaddungeons.server;

import com.bonet.threaddungeons.DatabaseManager;
import com.bonet.threaddungeons.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorTCP {
    private static final int Puerto = 2000; // Puerto del servidor
    private static AtomicBoolean running = new AtomicBoolean(true); // Estado del servidor
    private static ExecutorService threadPool = Executors.newCachedThreadPool(); // Pool de hilos
    private static final Logger logger = LoggerUtility.getLogger(ServidorTCP.class, "server"); // Logger para el servidor

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase(); // Inicializar la base de datos

        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            logger.info("Servidor iniciado en el puerto " + Puerto); // Informar que el servidor ha iniciado

            // Manejar el cierre del servidor
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false); // Cambiar estado a no corriendo
                try {
                    serverSocket.close(); // Cerrar ServerSocket
                    threadPool.shutdownNow(); // Apagar pool de hilos
                    logger.info("Servidor cerrado"); // Informar que el servidor ha cerrado
                } catch (IOException e) {
                    logger.error("Error al cerrar el servidor: " + e.getMessage()); // Registrar error
                }
            }));

            // Esperar conexiones de clientes
            while (running.get()) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Aceptar conexión de cliente
                    logger.info("Nuevo cliente conectado: " + clientSocket.getInetAddress().getHostAddress()); // Informar de nueva conexión
                    threadPool.submit(new ClientHandler(clientSocket)); // Manejar cliente en un nuevo hilo
                } catch (IOException e) {
                    if (running.get()) {
                        logger.error("Error en el servidor: " + e.getMessage()); // Registrar error
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error en el servidor: " + e.getMessage()); // Registrar error
        } finally {
            threadPool.shutdown(); // Apagar pool de hilos
        }
    }
}
