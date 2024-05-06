package com.bonet.threaddungeons;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorTCP {
    private static AtomicInteger counter = new AtomicInteger(0);
    private static final int Puerto = 2000;
    private static String mensajeBienvenida = "Bienvenido a Console Dungeons, " +
            "en este videojuego te enfrentarás a distintos niveles,\nalgunos contienen" +
            " poderosos enemigos y otros recompensas o perjuicios." +
            "\n¡Suerte sobreviviendo!\n";

    private static String guiaJuego = "\nGUÍA DE JUEGO\n" +
            "Los niveles son de izquierda a derecha por filas, hay varios tipos:\n" +
            "O = Mazmorra fácil (hay un enemigo que eliminar).\n" +
            "Ø = Mazmorra media (hay un enemigo y gana un beneficio o perjuicio).\n" +
            "Ô = Mazmorra recompensa (obtienes salud y oro).\n" +
            "R = Mazmorra aleatoria (pelea contra jefe o mercader).";

    public static void main(String[] args) {
        try (ServerSocket skServidor = new ServerSocket(Puerto)) {
            System.out.println("Iniciando [Console Dungeons] - Esperando jugadores...");

            while (true) {
                Socket sCliente = null;
                try {
                    sCliente = skServidor.accept();
                    System.out.println("Cliente " + counter.incrementAndGet() + ": ¡Se ha conectado un nuevo jugador!");
                    try {
                        InputStream in = sCliente.getInputStream();
                        DataInputStream flujo_entrada = new DataInputStream(in);
                        OutputStream out = sCliente.getOutputStream();
                        DataOutputStream flujo_salida = new DataOutputStream(out);

                        flujo_salida.writeUTF(mensajeBienvenida + guiaJuego);

                        Hilo hilo = new Hilo(sCliente, flujo_entrada, flujo_salida, counter.get());
                        hilo.start();
                    } catch (IOException e) {
                        System.out.println("Cliente " + counter.incrementAndGet() + ": + Error en la comunicación con el cliente | ERROR:" + e.getMessage());
                    }
                } catch (IOException e) {
                    System.out.println("Error aceptando el cliente " + counter.get() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error creando el server socket: " + e.getMessage());
        }
    }
}
