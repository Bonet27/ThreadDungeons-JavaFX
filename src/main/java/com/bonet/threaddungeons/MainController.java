package com.bonet.threaddungeons;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

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
    private SplitPane splitPane;

    private Socket sCliente;
    private static final String HOST = "localhost";
    private static final int Puerto = 2000;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Tablero tablero;
    private ProgressBar[] enemyHealthBars;
    private Label[] enemyHpLabels;
    private TitledPane[] niveles;
    private TitledPane[] botines;

    private int currentEtapaIndex = 0;
    private int currentCasillaIndex = 0;

    @FXML
    private void initialize() {
        // Inicializar los arrays de TitledPane y ProgressBar
        niveles = new TitledPane[]{nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane};
        botines = new TitledPane[]{botin1pane, botin2pane, botin3pane, botin4pane, botin5pane};
        enemyHealthBars = new ProgressBar[]{enemyHealth1, enemyHealth2, enemyHealth3, enemyHealth4, enemyHealth5};
        enemyHpLabels = new Label[]{enemy1HpLabel, enemy2HpLabel, enemy3HpLabel, enemy4HpLabel, enemy5HpLabel};

        // Iniciar el hilo de red
        new Thread(this::connectToServer).start();

        // Abrir por defecto el primer panel y el primer botín del acordeón
        accordion1.setExpandedPane(nivel1pane);
        accordion2.setExpandedPane(botin1pane);

        // Configurar eventos de botones
        btn_attack.setOnAction(event -> enviarMensajeAlServidor("1"));
        btn_skip.setOnAction(event -> enviarMensajeAlServidor("2"));
        btn_menu.setOnAction(event -> openScene("Login-view.fxml"));

        // Ajustar tamaño de fuente al cambiar tamaño de la ventana
        setupSceneListeners();
    }

    private void setupSceneListeners() {
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
        String fontSizeStyle = "-fx-font-size: " + fontSize + "pt;";
        btn_attack.setStyle(fontSizeStyle);
        btn_skip.setStyle(fontSizeStyle);
        btn_menu.setStyle(fontSizeStyle);
        tp_money.setStyle(fontSizeStyle);
        setStyles(fontSizeStyle, niveles);
        setStyles(fontSizeStyle, botines);
        setStyles(fontSizeStyle, enemyHpLabels);
        textAreaConsole.setStyle(fontSizeStyle);
        attackValue.setStyle(fontSizeStyle);
        consoleLabel.setStyle(fontSizeStyle);
        playerName.setStyle(fontSizeStyle);
        speedValue.setStyle(fontSizeStyle);
    }

    private void setStyles(String style, TitledPane[] panes) {
        for (TitledPane pane : panes) {
            pane.setStyle(style);
        }
    }

    private void setStyles(String style, Label[] labels) {
        for (Label label : labels) {
            label.setStyle(style);
        }
    }

    private void adjustElementSizes(double scaleFactor) {
        adjustImageSize(playerImage, scaleFactor);
        adjustImageSize(attackIcon, scaleFactor);
        adjustImageSize(speedIcon, scaleFactor);
        adjustProgressBarsScale(enemyHealthBars, scaleFactor);
        adjustProgressBarsScale(new ProgressBar[]{sliderHealth}, scaleFactor);
    }

    private void adjustImageSize(ImageView imageView, double scaleFactor) {
        imageView.setFitWidth(imageView.getImage().getWidth() * scaleFactor * 0.2);
        imageView.setFitHeight(imageView.getImage().getHeight() * scaleFactor * 0.2);
    }

    private void adjustProgressBarsScale(ProgressBar[] bars, double scaleFactor) {
        for (ProgressBar bar : bars) {
            bar.setScaleX(scaleFactor);
            bar.setScaleY(scaleFactor);
        }
    }

    private void adjustAllSizes() {
        Scene scene = btn_attack.getScene();
        if (scene != null) {
            double width = scene.getWidth();
            double height = scene.getHeight();
            double fontSize = calculateFontSize(width, height);
            double scaleFactor = Math.min(width / 800, height / 600); // Assuming 800x600 is the base size

            adjustFontSize(fontSize / 2);
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

    private void openScene(String scene) {
        try {
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
            sCliente = new Socket(HOST, Puerto);
            InputStream aux = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(aux);

            while (!Thread.currentThread().isInterrupted()) {
                String mensaje = flujo_entrada.readUTF();
                System.out.println(mensaje);

                Platform.runLater(() -> {
                    try {
                        Tablero nuevoTablero = gson.fromJson(mensaje, Tablero.class);
                        actualizarInterfaz(nuevoTablero);
                    } catch (JsonSyntaxException e) {
                        textAreaConsole.setText("Error al procesar datos del servidor: " + e.getMessage());
                    }
                });
            }
            sCliente.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Platform.runLater(() -> textAreaConsole.setText("ERROR: Conexión perdida con el servidor: " + e.getMessage()));
        }
    }

    private void actualizarInterfaz(Tablero nuevoTablero) {
        if (nuevoTablero == null) {
            return;
        }

        this.tablero = nuevoTablero;
        playerName.setText(tablero.getJugador().getNombre());
        sliderHealth.setProgress(tablero.getJugador().getSalud() / 100.0);

        boolean casillaEncontrada = false;
        for (int i = 0; i < tablero.getEtapas().length && !casillaEncontrada; i++) {
            Etapa etapa = tablero.getEtapas()[i];
            for (int j = 0; j < etapa.getCasillas().length && !casillaEncontrada; j++) {
                Casilla casilla = etapa.getCasillas()[j];
                if (casilla.getHealth() > 0) {
                    currentEtapaIndex = i;
                    currentCasillaIndex = j;
                    casillaEncontrada = true;
                }
            }
        }

        for (int i = 0; i < enemyHealthBars.length; i++) {
            if (i < tablero.getEtapas()[currentEtapaIndex].getCasillas().length) {
                Casilla casilla = tablero.getEtapas()[currentEtapaIndex].getCasillas()[i];
                enemyHealthBars[i].setProgress(casilla.getHealth() / 100.0);
                enemyHpLabels[i].setText(String.format("%.0f%%", casilla.getHealth()));
            } else {
                enemyHealthBars[i].setProgress(0);
                enemyHpLabels[i].setText("0%");
            }
        }

        openActualTitledPane(currentEtapaIndex, currentCasillaIndex);
    }

    private void openActualTitledPane(int etapaIndex, int casillaIndex) {
        for (TitledPane nivel : niveles) {
            nivel.setDisable(true);
        }
        for (TitledPane botin : botines) {
            botin.setDisable(true);
        }

        niveles[casillaIndex].setDisable(false);
        botines[casillaIndex].setDisable(false);
        accordion1.setExpandedPane(niveles[casillaIndex]);
        accordion2.setExpandedPane(botines[casillaIndex]);
    }
}
