package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
    @FXML
    private Pane attackPane;

    private Socket sCliente;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Tablero tablero;
    private ProgressBar[] enemyHealthBars;
    private Label[] enemyHpLabels;
    private TitledPane[] niveles, botines;
    private AnchorPane[] nivelContents;
    private MainApp mainApp;
    private boolean enCombate = false;
    private Timer combateTimer;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSocket(Socket socket) {
        this.sCliente = socket;
        new Thread(this::connectToServer).start();
    }

    @FXML
    private void initialize() {
        niveles = new TitledPane[]{nivel1pane, nivel2pane, nivel3pane, nivel4pane, nivel5pane};
        botines = new TitledPane[]{botin1pane, botin2pane, botin3pane, botin4pane, botin5pane};
        enemyHealthBars = new ProgressBar[]{enemyHealth1, enemyHealth2, enemyHealth3, enemyHealth4, enemyHealth5};
        enemyHpLabels = new Label[]{enemy1HpLabel, enemy2HpLabel, enemy3HpLabel, enemy4HpLabel, enemy5HpLabel};
        nivelContents = new AnchorPane[]{nivel1content, nivel2content, nivel3content, nivel4content, nivel5content};

        btn_attack.setOnAction(event -> iniciarAtaque());
        btn_skip.setOnAction(event -> enviarMensajeAlServidor("2"));
        btn_menu.setOnAction(event -> openScene("Login-view.fxml"));
    }

    private void enviarMensajeAlServidor(String mensaje) {
        if (sCliente != null && !sCliente.isClosed() && !sCliente.isOutputShutdown()) {
            try {
                OutputStream out = sCliente.getOutputStream();
                DataOutputStream flujo_salida = new DataOutputStream(out);
                flujo_salida.writeUTF(mensaje);
                System.out.println("Mensaje enviado al servidor: " + mensaje);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            InputStream in = sCliente.getInputStream();
            DataInputStream flujo_entrada = new DataInputStream(in);

            while (!sCliente.isClosed()) {
                try {
                    String datosServidor = flujo_entrada.readUTF();
                    System.out.println("Datos recibidos del servidor: " + datosServidor);
                    Tablero newTablero = gson.fromJson(datosServidor, Tablero.class);
                    if (newTablero != null) {
                        Platform.runLater(() -> actualizarInterfaz(newTablero));
                    }
                } catch (EOFException e) {
                    System.out.println("Cliente desconectado del servidor");
                    cerrarConexionYVolverAlLogin();
                    break;
                } catch (IOException | JsonSyntaxException e) {
                    if (!sCliente.isClosed()) {
                        e.printStackTrace();
                        cerrarConexionYVolverAlLogin();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            cerrarConexionYVolverAlLogin();
        }
    }

    private void cerrarConexionYVolverAlLogin() {
        try {
            if (sCliente != null && !sCliente.isClosed()) {
                sCliente.close();
            }
            Platform.runLater(() -> openScene("Login-view.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniciarAtaque() {
        enviarMensajeAlServidor("1");
        enCombate = true;
        combateTimer = new Timer();
        generarCirculo();
    }

    public void actualizarInterfaz(Tablero newTablero) {
        this.tablero = newTablero;
        System.out.println("Interfaz actualizada con nuevo tablero: " + tablero);

        sliderHealth.setProgress(tablero.getJugador().getSalud() / tablero.getJugador().getSaludMaxima());
        playerName.setText(tablero.getJugador().getNombre());
        attackValue.setText(String.valueOf(tablero.getJugador().getDmg()));
        speedValue.setText(String.valueOf(tablero.getJugador().getVelocidad()));

        Casilla[] casillas = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas();
        for (int i = 0; i < casillas.length; i++) {
            Casilla casilla = casillas[i];
            enemyHealthBars[i].setProgress(casilla.getHealth() / casilla.getMaxHealth());
            enemyHpLabels[i].setText(String.format("%.0f / %.0f", casilla.getHealth(), casilla.getMaxHealth()));
        }
        openActualTitledPane(tablero.getJugador().getCasillaActual());
    }

    private void openActualTitledPane(int casillaActual) {
        for (int i = 0; i < niveles.length; i++) {
            niveles[i].setExpanded(i == casillaActual);
        }
    }

    private void generarCirculo() {
        Casilla casillaActual = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()];
        AnchorPane currentPane = nivelContents[tablero.getJugador().getCasillaActual()];

        currentPane.getChildren().removeIf(node -> node instanceof Circle);

        Random random = new Random();
        double paneWidth = currentPane.getWidth();
        double paneHeight = currentPane.getHeight();
        double maxDiameter = 50.0;

        final int delayBetweenCircles = 1000;
        final int circleLifetime = 2000; // Tiempo de vida del círculo antes de desaparecer

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Casilla casillaActualizada = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()];

                    if (casillaActualizada.getHealth() > 0 && !tablero.isPartidaAcabada() && enCombate) {
                        Circle circle = new Circle(maxDiameter / 2, Color.RED);
                        circle.setOpacity(0.5);
                        circle.setLayoutX(random.nextDouble() * (paneWidth - maxDiameter) + maxDiameter / 2);
                        circle.setLayoutY(random.nextDouble() * (paneHeight - maxDiameter) + maxDiameter / 2);
                        circle.setManaged(false); // Evitar que se redimensione

                        circle.setOnMouseClicked(event -> {
                            enviarMensajeAlServidor("1"); // Enviar el mensaje de ataque al servidor
                            currentPane.getChildren().remove(circle);
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
                                            enviarMensajeAlServidor("3");
                                            cerrarConexionYVolverAlLogin();
                                        } else {
                                            enviarMensajeAlServidor("1");
                                        }
                                    }
                                });
                            }
                        }, circleLifetime);
                    } else {
                        combateTimer.cancel();
                    }
                });
            }
        };
        combateTimer.scheduleAtFixedRate(task, 0, delayBetweenCircles);
    }
}
