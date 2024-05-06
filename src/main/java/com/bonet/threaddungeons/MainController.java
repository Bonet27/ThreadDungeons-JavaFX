package com.bonet.threaddungeons;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;

public class MainController {

    @FXML
    private Button btn_attack, btn_skip, btn_menu;
    @FXML
    private TitledPane nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane;
    @FXML
    private TitledPane tp_money;
    @FXML
    private TitledPane botin1pane, botin2pane, botin3pane, botin4pane, botin5pane;
    @FXML
    private Accordion accordion1, accordion2;
    @FXML
    private Label enemy1HpLabel, enemy2HpLabel, enemy3HpLabel, enemy4HpLabel, enemy5HpLabel;
    @FXML
    private ProgressBar enemyHealth1, enemyHealth2, enemyHealth3, enemyHealth4, enemyHealth5;
    @FXML
    private TextArea textAreaConsole;
    private int i = 0;
    private static Socket sCliente;
    private static final String HOST = "localhost";
    private static final int Puerto = 2000;
    private static String mensaje;

    @FXML
    private void initialize() {
        // Inicia el hilo de red
        new Thread(this::connectToServer).start();

        // Cargar niveles, botines y vidas de enemigos en arrays para su manipulación
        TitledPane[] niveles = {nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane};
        TitledPane[] botines = {botin1pane, botin2pane, botin3pane, botin4pane, botin5pane};
        Label[] enemyHpLabels = {enemy1HpLabel, enemy2HpLabel, enemy3HpLabel, enemy4HpLabel, enemy5HpLabel};

        // Abrir por defecto el primer panel y el primer botín del acordeón
        accordion1.setExpandedPane(nivel1pane);
        accordion2.setExpandedPane(botin1pane);

        // Si pulsa el boton de ataque, manda un '1' al servidor, la respuesta que espera...
        btn_attack.setOnAction(event -> {
            enviarMensajeAlServidor("1");
            openActualTitledPane(niveles, botines);
        });

        // Si pulsa el boton de saltar, manda un '2' al servidor, la respuesta que espera...
        btn_skip.setOnAction(event -> {
            enviarMensajeAlServidor("2");
            openActualTitledPane(niveles, botines);
        });

        btn_menu.setOnAction(event -> {
            openScene("Login-view.fxml");
        });

        // Iterar sobre los niveles y agregar el listener
        for (int i = 0; i < niveles.length; i++) {
            int index = i; // Variable final para usar dentro del lambda
            niveles[i].expandedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    botines[index].setExpanded(true);
                    FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemyHpLabels[index]);
                    fadeInTransition.setFromValue(0.0);
                    fadeInTransition.setToValue(1.0);
                    fadeInTransition.play();
                } else {
                    botines[index].setExpanded(false);
                    FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemyHpLabels[index]);
                    fadeOutTransition.setFromValue(1.0);
                    fadeOutTransition.setToValue(0.0);
                    fadeOutTransition.play();
                }
            });
        }
    }

    public void enviarMensajeAlServidor(String mensaje) {
        if (sCliente == null) {
            textAreaConsole.setText("ERROR: Servidor no disponible");
            return;
        }

        try {
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);
            flujo_salida.writeUTF(mensaje);
        } catch (IOException e) {
            String errorMessage = "ERROR: Al enviar mensaje al servidor: " + e.getMessage();
            System.out.println(errorMessage);
            textAreaConsole.setText(errorMessage);
        }
    }

    private void openActualTitledPane(TitledPane[] niveles, TitledPane[] botines) {
        if (i < 4) {
            accordion1.setExpandedPane(niveles[i + 1]);
            accordion2.setExpandedPane(botines[i + 1]);
            i++;
        } else if (i == 4) {
            accordion1.setExpandedPane(niveles[0]); // Abre la primera TitledPane
            accordion2.setExpandedPane(botines[0]);
            i = 0; // Reinicia el índice
        }
    }

    private void openScene(String scene) {
        try {
            // Cargar la nueva escena desde el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(scene));
            Parent root = fxmlLoader.load();
            MainApp.getStage().getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
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
            mensaje = flujo_entrada.readUTF();
            System.out.println(mensaje);

            // Determina el estado de la partida.
            boolean partidaAcabada = false;

            // Mientras la partida no esté acabada...
            while (!partidaAcabada) {
                // Guarda el mensaje de texto de entrada.
                mensaje = flujo_entrada.readUTF();
                textAreaConsole.setText(mensaje);
                System.out.print(mensaje);
            }
            sCliente.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e.getMessage() != null)
                textAreaConsole.setText("ERROR: Conexión perdida con el servidor: " + e.getMessage());
            else textAreaConsole.setText("ERROR: Conexión perdida con el servidor... ");
        }
    }
}