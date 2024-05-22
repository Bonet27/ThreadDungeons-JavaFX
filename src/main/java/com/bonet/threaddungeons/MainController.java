package com.bonet.threaddungeons;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private AnchorPane nivel1content, nivel2content, nivel3content, nivel4content, nivel5content;
    @FXML
    private ProgressBar enemyHealth1, enemyHealth2, enemyHealth3, enemyHealth4, enemyHealth5;
    @FXML
    private ImageView attackIcon;
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
    private AnchorPane[] nivelContents;
    private int currentEtapaIndex = 0;
    private int currentCasillaIndex = 0;
    private ExecutorService executorService = Executors.newCachedThreadPool(); // Use a thread pool

    @FXML
    private void initialize() {
        niveles = new TitledPane[]{nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane};
        botines = new TitledPane[]{botin1pane, botin2pane, botin3pane, botin4pane, botin5pane};
        enemyHealthBars = new ProgressBar[]{enemyHealth1, enemyHealth2, enemyHealth3, enemyHealth4, enemyHealth5};
        enemyHpLabels = new Label[]{enemy1HpLabel, enemy2HpLabel, enemy3HpLabel, enemy4HpLabel, enemy5HpLabel};
        nivelContents = new AnchorPane[]{nivel1content, nivel2content, nivel3content, nivel4content, nivel5content};

        new Thread(this::connectToServer).start();

        Stage stage = MainApp.getStage();
        stage.setOnCloseRequest(event -> {
            closeConnection();
            executorService.shutdownNow(); // Shutdown the thread pool
            Platform.exit();
            System.exit(0);
        });

        accordion1.setExpandedPane(nivel1pane);
        accordion2.setExpandedPane(botin1pane);

        btn_attack.setOnAction(event -> iniciarCombate());
        btn_skip.setOnAction(event -> enviarMensajeAlServidor("2"));
        btn_menu.setOnAction(event -> openScene("Login-view.fxml"));

        for (int i = 0; i < niveles.length; i++) {
            final int index = i;
            niveles[i].setOnMouseClicked(event -> {
                currentCasillaIndex = index;
                accordion1.setExpandedPane(niveles[currentCasillaIndex]);
                openActualTitledPane(currentEtapaIndex, currentCasillaIndex);
            });
        }
    }

    private void enviarMensajeAlServidor(String mensaje) {
        if (sCliente == null) {
            return;
        }

        try {
            OutputStream out = sCliente.getOutputStream();
            DataOutputStream flujo_salida = new DataOutputStream(out);
            flujo_salida.writeUTF(mensaje);
        } catch (IOException e) {
            String errorMessage = "ERROR: Al enviar mensaje al servidor: " + e.getMessage();
            System.out.println(errorMessage);
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

            String mensaje = flujo_entrada.readUTF();
            System.out.println(mensaje);

            String finalMensaje = mensaje;
            Platform.runLater(() -> {
                try {
                    Tablero nuevoTablero = gson.fromJson(finalMensaje, Tablero.class);
                    actualizarInterfaz(nuevoTablero);
                } catch (JsonSyntaxException e) {
                    System.out.println("Error al procesar datos del servidor: " + e.getMessage());
                }
            });

            boolean partidaAcabada = false;

            while (!partidaAcabada) {
                mensaje = flujo_entrada.readUTF();
                System.out.print(mensaje);

                String finalMensaje1 = mensaje;
                Platform.runLater(() -> {
                    try {
                        Tablero nuevoTablero = gson.fromJson(finalMensaje1, Tablero.class);
                        actualizarInterfaz(nuevoTablero);
                    } catch (JsonSyntaxException e) {
                        System.out.println("Error al procesar datos del servidor: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Platform.runLater(() -> System.out.println("ERROR: Conexión perdida con el servidor: " + e.getMessage()));
        }
    }

    private void closeConnection() {
        try {
            if (sCliente != null) {
                sCliente.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar la conexión: " + e.getMessage());
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
                enemyHealthBars[i].setProgress(casilla.getHealth() / casilla.getMaxHealth());
                enemyHpLabels[i].setText(String.format("%.0f%%", (casilla.getHealth() / casilla.getMaxHealth()) * 100));
            } else {
                enemyHealthBars[i].setProgress(0);
                enemyHpLabels[i].setText("0%");
            }
        }

        openActualTitledPane(currentEtapaIndex, currentCasillaIndex);
    }

    private void openActualTitledPane(int etapaIndex, int casillaIndex) {
        for (int i = 0; i < niveles.length; i++) {
            if (i == casillaIndex) {
                niveles[i].setDisable(false);
                botines[i].setDisable(false);
            } else {
                niveles[i].setDisable(true);
                botines[i].setDisable(true);
            }
        }
        accordion1.setExpandedPane(niveles[casillaIndex]);
        accordion2.setExpandedPane(botines[casillaIndex]);
    }

    private void iniciarCombate() {
        if (tablero == null) {
            System.out.println("ERROR: El tablero no está inicializado.");
            return;
        }

        Casilla casillaActual = tablero.getEtapas()[currentEtapaIndex].getCasillas()[currentCasillaIndex];
        if (casillaActual.getEstado() == Casilla.Estado.SIN_ATACAR) {
            casillaActual.setEstado(Casilla.Estado.EN_COMBATE);
            generarCirculos();
        }

        enviarMensajeAlServidor("1"); // Enviar mensaje de iniciar combate al servidor
    }

    private void generarCirculos() {
        Casilla casillaActual = tablero.getEtapas()[currentEtapaIndex].getCasillas()[currentCasillaIndex];
        AnchorPane currentPane = nivelContents[currentCasillaIndex];

        currentPane.getChildren().removeIf(node -> node instanceof Circle);

        int numCircles;
        switch (casillaActual.getMode()) {
            case NORMAL:
                numCircles = 3;
                break;
            case REWARD:
                numCircles = 5;
                break;
            case RANDOM:
                numCircles = 8;
                break;
            case BOSS:
                numCircles = 20;
                break;
            default:
                numCircles = 5;
        }

        Random random = new Random();
        double paneWidth = currentPane.getWidth();
        double paneHeight = currentPane.getHeight();
        double maxDiameter = 50.0;

        final int delayBetweenCircles = 1000;
        final int[] currentCircle = {0};

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (currentCircle[0] < numCircles) {
                        Circle circle = new Circle(maxDiameter / 2, Color.RED);
                        circle.setOpacity(0.5);
                        circle.setLayoutX(random.nextDouble() * (paneWidth - maxDiameter) + maxDiameter / 2);
                        circle.setLayoutY(random.nextDouble() * (paneHeight - maxDiameter) + maxDiameter / 2);

                        circle.setOnMouseClicked(event -> {
                            enviarMensajeAlServidor("1");
                            currentPane.getChildren().remove(circle);
                            recibirActualizacionDelServidor();
                        });

                        currentPane.getChildren().add(circle);

                        Timer innerTimer = new Timer();
                        innerTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    if (currentPane.getChildren().contains(circle)) {
                                        currentPane.getChildren().remove(circle);
                                        tablero.getJugador().takeDamage(casillaActual.getDamage());
                                        actualizarInterfaz(tablero);
                                        if (tablero.getJugador().getSalud() <= 0) {
                                            enviarMensajeAlServidor("3");
                                        }
                                    }
                                });
                            }
                        }, (long) (2000 / tablero.getJugador().getVelocidad()));
                    }
                    currentCircle[0]++;
                });
            }
        };
        timer.schedule(task, 0, delayBetweenCircles);
    }

    private void recibirActualizacionDelServidor() {
        executorService.submit(() -> {
            try {
                DataInputStream flujo_entrada = new DataInputStream(sCliente.getInputStream());
                String mensaje = flujo_entrada.readUTF();
                Platform.runLater(() -> {
                    try {
                        Tablero nuevoTablero = gson.fromJson(mensaje, Tablero.class);
                        actualizarInterfaz(nuevoTablero);
                    } catch (JsonSyntaxException e) {
                        System.out.println("Error al procesar datos del servidor: " + e.getMessage());
                    }
                });
            } catch (IOException e) {
                System.out.println("Error al recibir la actualización del servidor: " + e.getMessage());
            }
        });
    }

}
