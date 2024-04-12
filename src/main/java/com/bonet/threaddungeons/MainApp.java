package com.bonet.threaddungeons;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainApp extends Application {

    static Socket sCliente;
    static final String HOST = "localhost";
    static final int Puerto = 2000;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Login-view.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Thread Dungeons");
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;

        // Inicia el hilo de red
        new Thread(this::connectToServer).start();
    }

    public static void enviarMensajeAlServidor(String mensaje) {
        try {
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);
            flujo_salida.writeUTF(mensaje);
        } catch (IOException e) {
            System.out.println("Error al enviar mensaje al servidor: " + e.getMessage());
        }
    }

    private void connectToServer() {
        try {
            // Crea un socket cliente en un host y puerto predefinidos.
            sCliente = new Socket(HOST, Puerto);

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
            }
            sCliente.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}