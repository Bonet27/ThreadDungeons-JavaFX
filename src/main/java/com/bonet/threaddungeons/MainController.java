package com.bonet.threaddungeons;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.stage.Stage;

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
    private ImageView playerImage, attackIcon, speedIcon;
    @FXML
    private Text playerName, attackValue, speedValue;
    @FXML
    private VBox playerVbox;
    @FXML
    private ProgressBar sliderHealth;
    @FXML
    private HBox lowHbox, mainHBox, mainHBox1;
    @FXML
    private SplitPane splitPane;
    private Socket sCliente;
    private static final String HOST = "localhost";
    private static final int Puerto = 2000;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Tablero tablero;
    private ProgressBar[] enemyHealthBars;
    private Label[] enemyHpLabels;
    private TitledPane[] niveles, botines;
    private AnchorPane[] nivelContents;
    private int currentEtapaIndex = 0, currentCasillaIndex = 0;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private boolean enCombate = false;

    @FXML
    private void initialize() {
        niveles = new TitledPane[]{ nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane };
        botines = new TitledPane[]{ botin1pane, botin2pane, botin3pane, botin4pane, botin5pane };
        enemyHealthBars = new ProgressBar[]{ enemyHealth1, enemyHealth2, enemyHealth3, enemyHealth4, enemyHealth5 };
        enemyHpLabels = new Label[]{ enemy1HpLabel, enemy2HpLabel, enemy3HpLabel, enemy4HpLabel, enemy5HpLabel };
        nivelContents = new AnchorPane[]{ nivel1content, nivel2content, nivel3content, nivel4content, nivel5content };

        new Thread(this::connectToServer).start();

        btn_attack.setOnAction(event -> { if (!enCombate) iniciarCombate(); });

        btn_skip.setOnAction(event -> enviarMensajeAlServidor("2"));
        btn_menu.setOnAction(event -> openScene("Login-view.fxml"));

        openActualTitledPane(currentCasillaIndex);
        handlerTitledPaneClick();
    }

    private void handlerTitledPaneClick(){
        for (int i = 0; i < niveles.length; i++) {
            final int index = i;
            niveles[i].setOnMouseClicked(event -> {
                currentCasillaIndex = index;
                openActualTitledPane(currentCasillaIndex);
            });
        }
    }

    private void enviarMensajeAlServidor(String mensaje) {
        if (sCliente != null && !sCliente.isClosed() && !sCliente.isOutputShutdown()) {
            executorService.submit(() -> {
                try {
                    if (sCliente != null && !sCliente.isClosed() && !sCliente.isOutputShutdown()) {
                        OutputStream out = sCliente.getOutputStream();
                        DataOutputStream flujo_salida = new DataOutputStream(out);
                        flujo_salida.writeUTF(mensaje);
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar el estado al servidor: " + e.getMessage());
                }
            });
        }
    }

    private void openScene(String scene) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(scene));
            Parent root = fxmlLoader.load();
            Stage stage = MainApp.getStage();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            String errorMessage = "ERROR: Al abrir la escena: " + e.getMessage();
            System.out.println(errorMessage);
        }
    }

    private void connectToServer() {
        try {
            sCliente = new Socket(HOST, Puerto);
            InputStream in = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(in);

            while (!sCliente.isClosed()) {
                try {
                    String datosServidor = flujo_entrada.readUTF();
                    Tablero newTablero = gson.fromJson(datosServidor, Tablero.class);
                    if (newTablero != null) {
                        Platform.runLater(() -> actualizarInterfaz(newTablero));
                    }
                } catch (EOFException e) {
                    System.out.println("Cliente desconectado del servidor");
                    break;
                } catch (IOException | JsonSyntaxException e) {
                    if (!sCliente.isClosed()) {
                        System.out.println("Error al procesar datos del servidor: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: Al conectar con el servidor: " + e.getMessage());
        }
    }

    private void actualizarInterfaz(Tablero newTablero) {
        this.tablero = newTablero;
        sliderHealth.setProgress(tablero.getJugador().getSalud() / 100.0);
        playerName.setText(tablero.getJugador().getNombre());

        Casilla[] casillas = tablero.getEtapas()[currentEtapaIndex].getCasillas();
        for (int i = 0; i < casillas.length; i++) {
            Casilla casilla = casillas[i];
            enemyHealthBars[i].setProgress(casilla.getHealth() / casilla.getMaxHealth());
            enemyHpLabels[i].setText(String.format("%.0f / %.0f", casilla.getHealth(), casilla.getMaxHealth()));
        }
    }

    private void openActualTitledPane(int casillaIndex) {
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
        if (casillaActual.getEstado() == Casilla.Estado.SIN_ATACAR && casillaActual.isAlive()) {
            casillaActual.setEstado(Casilla.Estado.EN_COMBATE);
            enCombate = true;
            generarCirculos();
        } else {
            enCombate = false;
        }
    }

    private void generarCirculos() {
        Casilla casillaActual = tablero.getEtapas()[currentEtapaIndex].getCasillas()[currentCasillaIndex];
        AnchorPane currentPane = nivelContents[currentCasillaIndex];

        currentPane.getChildren().removeIf(node -> node instanceof Circle);

        Random random = new Random();
        double paneWidth = currentPane.getWidth();
        double paneHeight = currentPane.getHeight();
        double maxDiameter = 50.0;

        final int delayBetweenCircles = 1000;
        final int circleLifetime = 2000; // Tiempo de vida del círculo antes de desaparecer

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (casillaActual.getHealth() > 0 && !tablero.isPartidaAcabada() && enCombate) {
                    Platform.runLater(() -> {
                        Circle circle = new Circle(maxDiameter / 2, Color.RED);
                        circle.setOpacity(0.5);
                        circle.setLayoutX(random.nextDouble() * (paneWidth - maxDiameter) + maxDiameter / 2);
                        circle.setLayoutY(random.nextDouble() * (paneHeight - maxDiameter) + maxDiameter / 2);

                        circle.setOnMouseClicked(event -> {
                            tablero.atacar();
                            currentPane.getChildren().remove(circle);
                            actualizarInterfaz(tablero);
                            enviarEstadoAlServidor();

                            if (casillaActual.getHealth() <= 0) {
                                casillaActual.setEstado(Casilla.Estado.MUERTO);
                                enCombate = false;
                                timer.cancel();
                                avanzarANextCasilla();
                            }
                        });

                        currentPane.getChildren().add(circle);

                        Timer removeCircleTimer = new Timer();
                        removeCircleTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Platform.runLater(() -> {
                                    if (currentPane.getChildren().contains(circle)) {
                                        currentPane.getChildren().remove(circle);
                                        tablero.getJugador().takeDamage(casillaActual.getDamage());
                                        actualizarInterfaz(tablero);
                                        if (tablero.getJugador().getSalud() <= 0) {
                                            tablero.setPartidaAcabada(true);
                                            enviarEstadoAlServidor();
                                            cerrarConexionYVolverAlLogin();
                                        } else {
                                            enviarEstadoAlServidor();
                                        }
                                    }
                                });
                            }
                        }, circleLifetime);
                    });
                } else {
                    timer.cancel();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, delayBetweenCircles);
    }

    private void avanzarANextCasilla() {
        Platform.runLater(() -> {
            tablero.avanzar();
            currentEtapaIndex = tablero.getJugador().getEtapaActual();
            currentCasillaIndex = tablero.getJugador().getCasillaActual();
            openActualTitledPane(currentCasillaIndex);

            // Solo iniciar combate si no hemos terminado el juego
            if (!tablero.isPartidaAcabada() && tablero.getEtapas()[currentEtapaIndex].getCasillas()[currentCasillaIndex].isAlive()) {
                iniciarCombate();
            }
        });
    }

    private void enviarEstadoAlServidor() {
        if (sCliente != null && !sCliente.isClosed() && !sCliente.isOutputShutdown()) {
            executorService.submit(() -> {
                try {
                    if (sCliente != null && !sCliente.isClosed() && !sCliente.isOutputShutdown()) {
                        OutputStream out = sCliente.getOutputStream();
                        DataOutputStream flujo_salida = new DataOutputStream(out);
                        String estadoJuego = gson.toJson(tablero);
                        flujo_salida.writeUTF(estadoJuego);
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar el estado al servidor: " + e.getMessage());
                }
            });
        }
    }

    private void verificarFinDeJuego() {
        if (tablero.isPartidaAcabada()) {
            cerrarConexionYVolverAlLogin();
        }
    }

    private void cerrarConexionYVolverAlLogin() {
        executorService.submit(() -> {
            try {
                if (sCliente != null && !sCliente.isClosed())
                    sCliente.close();
            } catch (IOException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                sCliente = null; // Asegurarse de que el socket no sea utilizado nuevamente
            }
            Platform.runLater(() -> openScene("Login-view.fxml"));
        });
    }
}
