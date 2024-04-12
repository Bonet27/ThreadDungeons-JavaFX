package com.bonet.threaddungeons;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteTCP {
    static final String HOST = "localhost";
    static final int Puerto = 2000;

    static DataOutputStream flujo_salida;

    public static void main(String[] args) {
        createClientTCP();
    }

    public static void createClientTCP()
    {
        try {
            // Crea un socket cliente en un host y puerto predefinidos.
            Socket sCliente = new Socket(HOST, Puerto);

            // Inicializa el flujo de entrada.
            InputStream aux = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(aux);

            // Guarda el mensaje inicial y lo imprime.
            String mensaje = flujo_entrada.readUTF();
            System.out.println(mensaje);

            // Determina el estado de la partida.
            boolean partidaAcabada = false;

            // Mientras la partida no esté acabada...
            while (!partidaAcabada) {
                // Guarda el mensaje de texto de entrada.
                mensaje = flujo_entrada.readUTF();

                System.out.print(mensaje);
                //System.out.println(flujo_entrada.readUTF());

                if (!mensaje.contains("Partida terminada")) {
                    Scanner scanner = new Scanner(System.in);
                    String respuesta = scanner.next();

                    // Inicializa la salida de datos y envía la fila y columna.
                    OutputStream out = sCliente.getOutputStream();
                    DataOutputStream flujo_salida = new DataOutputStream(out);
                    flujo_salida.writeUTF(respuesta);
                } else {
                    partidaAcabada = true;
                }
            }
            sCliente.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}


