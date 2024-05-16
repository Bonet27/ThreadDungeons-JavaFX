package com.bonet.threaddungeons;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.google.gson.Gson;

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
    private HBox topHbox;
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
    @FXML
    private ImageView attackIcon;
    @FXML
    private Label consoleLabel;

    @FXML
    private Text attackValue;
    @FXML
    private ImageView playerImage;
    @FXML
    private Text playerName;
    @FXML
    private VBox playerVbox;
    @FXML
    private ProgressBar sliderHealth;
    @FXML
    private ImageView speedIcon;
    @FXML
    private Text speedValue;
    @FXML
    private HBox lowHbox;
    @FXML
    private HBox mainHBox1;
    @FXML
    private HBox mainHbox;
    @FXML
    private FlowPane nivel1content;
    @FXML
    private FlowPane nivel2content;
    @FXML
    private FlowPane nivel3content;
    @FXML
    private FlowPane nivel4content;
    @FXML
    private FlowPane nivel5content;
    @FXML
    private SplitPane splitPane;

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

        // Agregar el listener para el cambio de tamaño de la ventana
        Scene scene = btn_attack.getScene();
        if (scene != null) {
            scene.widthProperty().addListener((obs, oldVal, newVal) -> adjustAllSizes());
            scene.heightProperty().addListener((obs, oldVal, newVal) -> adjustAllSizes());
        } else {
            btn_attack.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.widthProperty().addListener((obs2, oldVal, newVal) -> adjustAllSizes());
                    newScene.heightProperty().addListener((obs2, oldVal, newVal) -> adjustAllSizes());
                }
            });
        }
    }

    private double calculateFontSize(double width, double height) {
        return Math.min(width, height) / 25;
    }

    private void adjustFontSize(double fontSize) {
        // Adjust font size for all text elements
        btn_attack.setStyle("-fx-font-size: " + fontSize + "pt;");
        btn_skip.setStyle("-fx-font-size: " + fontSize + "pt;");
        btn_menu.setStyle("-fx-font-size: " + fontSize + "pt;");
        nivel1pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        nivel2pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        nivel3pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        nivel4pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        nivel5pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        tp_money.setStyle("-fx-font-size: " + fontSize + "pt;");
        botin1pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        botin2pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        botin3pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        botin4pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        botin5pane.setStyle("-fx-font-size: " + fontSize + "pt;");
        enemy1HpLabel.setStyle("-fx-font-size: " + fontSize + "pt;");
        enemy2HpLabel.setStyle("-fx-font-size: " + fontSize + "pt;");
        enemy3HpLabel.setStyle("-fx-font-size: " + fontSize + "pt;");
        enemy4HpLabel.setStyle("-fx-font-size: " + fontSize + "pt;");
        enemy5HpLabel.setStyle("-fx-font-size: " + fontSize + "pt;");
        textAreaConsole.setStyle("-fx-font-size: " + fontSize + "pt;");
        attackValue.setStyle("-fx-font-size: " + fontSize + "pt;");
        consoleLabel.setStyle("-fx-font-size: " + fontSize + "pt;");
        playerName.setStyle("-fx-font-size: " + fontSize + "pt;");
        speedValue.setStyle("-fx-font-size: " + fontSize + "pt;");
    }

    private void adjustElementSizes(double scaleFactor) {
        // Adjust sizes for other elements like images, progress bars, and containers
        playerImage.setFitWidth(playerImage.getImage().getWidth() * scaleFactor * 0.2);
        playerImage.setFitHeight(playerImage.getImage().getHeight() * scaleFactor * 0.2);

        attackIcon.setFitWidth(attackIcon.getImage().getWidth() * scaleFactor * 0.2);
        attackIcon.setFitHeight(attackIcon.getImage().getHeight() * scaleFactor * 0.2);

        speedIcon.setFitWidth(speedIcon.getImage().getWidth() * scaleFactor * 0.2);
        speedIcon.setFitHeight(speedIcon.getImage().getHeight() * scaleFactor * 0.2);

        enemyHealth1.setScaleX(scaleFactor);
        enemyHealth1.setScaleY(scaleFactor);

        enemyHealth2.setScaleX(scaleFactor);
        enemyHealth2.setScaleY(scaleFactor);

        enemyHealth3.setScaleX(scaleFactor);
        enemyHealth3.setScaleY(scaleFactor);

        enemyHealth4.setScaleX(scaleFactor);
        enemyHealth4.setScaleY(scaleFactor);

        enemyHealth5.setScaleX(scaleFactor);
        enemyHealth5.setScaleY(scaleFactor);

        sliderHealth.setScaleX(scaleFactor);
        sliderHealth.setScaleY(scaleFactor);
    }

    private void adjustAllSizes() {
        Scene scene = btn_attack.getScene();
        if (scene != null) {
            double width = scene.getWidth();
            double height = scene.getHeight();
            double fontSize = calculateFontSize(width, height);
            double scaleFactor = Math.min(width / 800, height / 600); // Assuming 800x600 is the base size

            adjustFontSize(fontSize/2);
            adjustElementSizes(scaleFactor);
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

    // Crear un objeto Gson para la serialización de JSON
    private Gson gson = new Gson();

    public void enviarObjetoAlServidor(Object objeto) {
        String json = gson.toJson(objeto);
        try {
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);
            flujo_salida.writeUTF(json);
        } catch (IOException e) {
            // Manejar errores de IO
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
            Stage stage = MainApp.getStage();
            stage.getScene().setRoot(root);
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
