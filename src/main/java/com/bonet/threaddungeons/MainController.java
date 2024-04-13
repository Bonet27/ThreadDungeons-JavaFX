package com.bonet.threaddungeons;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;

public class MainController {
    @FXML
    private Button btn_attack;
    @FXML
    private Button btn_skip;
    @FXML
    private TitledPane nivel1pane;
    @FXML
    private TitledPane nivel2pane;
    @FXML
    private TitledPane nivel3pane;
    @FXML
    private TitledPane nivel4pane;
    @FXML
    private TitledPane nivel5pane;
    @FXML
    private TitledPane tp_money;
    @FXML
    private TitledPane botin1pane;
    @FXML
    private TitledPane botin2pane;
    @FXML
    private TitledPane botin3pane;
    @FXML
    private TitledPane botin4pane;
    @FXML
    private TitledPane botin5pane;
    @FXML
    private Accordion accordion1;
    @FXML
    private Accordion accordion2;
    @FXML
    private Label enemy1HpLabel;
    @FXML
    private Label enemy2HpLabel;
    @FXML
    private Label enemy3HpLabel;
    @FXML
    private Label enemy4HpLabel;
    @FXML
    private Label enemy5HpLabel;
    @FXML
    private Button btn_menu;
    @FXML
    private TextArea textAreaConsole;
    private int i = 0;
    static Socket sCliente;
    static final String HOST = "localhost";
    static final int Puerto = 2000;

    public static String getMensaje() {
        return mensaje;
    }

    private static String mensaje;

    private DataInputStream flujo_entrada;

    @FXML
    private void initialize() {
        // Inicia el hilo de red
        new Thread(this::connectToServer).start();

        TitledPane[] titledPanes = {nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane};
        TitledPane[] botinPanes = {botin1pane, botin2pane, botin3pane, botin4pane, botin5pane};

        accordion1.setExpandedPane(nivel1pane);
        accordion2.setExpandedPane(botin1pane);

        btn_attack.setOnAction(event -> {
            enviarMensajeAlServidor("1");
            if(titledPanes[i].isExpanded() && i < 4) {
                accordion1.setExpandedPane(titledPanes[i+1]);
                accordion2.setExpandedPane(botinPanes[i+1]);
                i++;
            } else if (i == 4) {
                accordion1.setExpandedPane(titledPanes[0]); // Abre la primera TitledPane
                accordion2.setExpandedPane(botinPanes[0]);
                i = 0; // Reinicia el índice
            }
        });

        btn_skip.setOnAction(event -> {
            enviarMensajeAlServidor("2");
            if(titledPanes[i].isExpanded() && i < 5)
            {
                titledPanes[i].setExpanded(false);
                botinPanes[i].setExpanded(false);
                titledPanes[i+1].setExpanded(true);
                botinPanes[i+1].setExpanded(true);
                i++;
            }
            else{
                i = 0;
                accordion1.setExpandedPane(titledPanes[i]);
                accordion2.setExpandedPane(botinPanes[i]);
            }
            textAreaConsole.setText(getMensaje());
        });

        // Agregar evento de clic al botón utilizando una función lambda
        btn_menu.setOnAction(event -> {
            try {
                // Cargar la nueva escena desde el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Login-view.fxml"));
                Parent root = fxmlLoader.load();

                MainApp.getStage().getScene().setRoot(root);

            } catch (IOException e) {
                e.printStackTrace();
                // Manejar cualquier excepción que pueda ocurrir al cargar la nueva escena
            }
        });

        // Agregar listeners para el evento de expansión/cierre de los TitledPane
        nivel1pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin1pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy1HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin1pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy1HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel2pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin2pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy2HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin2pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy2HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel3pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin3pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy3HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin3pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy3HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel4pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin4pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy4HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin4pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy4HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel5pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin5pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy5HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin5pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy5HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });
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
                //System.out.println(flujo_entrada.readUTF());
            }

            sCliente.close();

            // Volver a la escena de login
            try {
                // Cargar la nueva escena desde el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Login-view.fxml"));
                Parent root = fxmlLoader.load();
                MainApp.getStage().getScene().setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
                // Manejar cualquier excepción que pueda ocurrir al cargar la nueva escena
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            if(e.getMessage() != null)
                textAreaConsole.setText("Lost connection with the server: " + e.getMessage());
            else
                textAreaConsole.setText("Lost connection with the server...");
        }
    }

}