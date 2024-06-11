package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

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
    private GridPane mainGridPane;
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
    private ImageView enemy1Image, enemy2Image, enemy3Image, enemy4Image, enemy5Image;
    @FXML
    private ImageView playerImage, attackIcon, speedIcon;
    @FXML
    private Label goldText;
    @FXML
    private Text playerName, attackValue, speedValue, textEtapa;
    @FXML
    private ProgressBar sliderHealth;
    @FXML
    private ImageView botin1Image, botin2Image, botin3Image, botin4Image, botin5Image1, botin5Image2;
    @FXML
    private Label botin1Label, botin2Label, botin3Label, botin4Label, botin5Label1, botin5Label2;
    private Socket sCliente;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Tablero tablero;
    private ProgressBar[] enemyHealthBars;
    private ImageView[] enemyImages;
    private Label[] enemyHpLabels;
    private TitledPane[] niveles, botines;
    private AnchorPane[] nivelContents;
    private MainApp mainApp;
    private Timer combateTimer;
    private Timer circleTimer;
    private boolean jugadorMuerto = false;

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
        enemyImages = new ImageView[]{enemy1Image, enemy2Image, enemy3Image, enemy4Image, enemy5Image};

        btn_attack.setOnAction(event -> iniciarAtaque());
        btn_skip.setOnAction(event -> enviarMensajeAlServidor("2"));
        btn_menu.setOnAction(event -> gameOver());
    }

    private void enviarMensajeAlServidor(String mensaje) {
        if (sCliente != null && !sCliente.isClosed() && !sCliente.isOutputShutdown()) {
            try {
                OutputStream out = sCliente.getOutputStream();
                DataOutputStream flujo_salida = new DataOutputStream(out);
                if (mensaje.equals("actualizarJugador")) {
                    String jugadorJson = gson.toJson(tablero.getJugador());
                    flujo_salida.writeUTF(jugadorJson);
                } else {
                    flujo_salida.writeUTF(mensaje);
                }
                flujo_salida.flush();
                System.out.println("Mensaje enviado al servidor: " + mensaje);
            } catch (IOException e) {
                System.out.println("Error al enviar mensaje al servidor: " + e.getMessage());
            }
        } else {
            System.out.println("El socket está cerrado o no disponible");
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
                        Platform.runLater(() -> {
                            actualizarInterfaz(newTablero);
                        });
                    }
                } catch (EOFException e) {
                    System.out.println("Cliente desconectado del servidor");
                    cerrarConexionYVolverAlLogin();
                    break;
                } catch (IOException | JsonSyntaxException e) {
                    if (!sCliente.isClosed()) {
                        System.out.println(e.getMessage());
                        cerrarConexionYVolverAlLogin();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            if (!sCliente.isClosed()) {
                cerrarConexionYVolverAlLogin();
            }
        }
    }

    private void gameOver() {
        enviarMensajeAlServidor("3"); // Marcar partida como terminada
        detenerCombate();
        cerrarConexion();
        Platform.runLater(() -> mainApp.openGameOverView());
    }

    private void cerrarConexionYVolverAlLogin() {
        cerrarConexion();
        Platform.runLater(() -> mainApp.openLoginView());
    }

    private void cerrarConexion() {
        try {
            if (sCliente != null && !sCliente.isClosed()) {
                sCliente.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void actualizarInterfaz(Tablero newTablero) {
        this.tablero = newTablero;

        Casilla[] casillas = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas();
        Casilla casillaActual = casillas[tablero.getJugador().getCasillaActual()];
        int numCasillaActual = tablero.getJugador().getCasillaActual();

        System.out.println("Interfaz actualizada con nuevo tablero: " + gson.toJson(tablero));

        Jugador jugador = tablero.getJugador();
        sliderHealth.setProgress(jugador.getSalud() / jugador.getSaludMaxima());
        playerName.setText(jugador.getNombre());
        attackValue.setText(String.valueOf(jugador.getDmg()));
        speedValue.setText(String.valueOf(jugador.getVelocidad()));
        goldText.setText(String.valueOf(jugador.getOro()));

        enemyImages[numCasillaActual].setImage(new Image(casillaActual.getIcon()));

        textEtapa.setText("ETAPA " + (jugador.getEtapaActual() + 1) + " DE " + tablero.getEtapas().length);
        for (int i = 0; i < casillas.length; i++) {
            niveles[i].setText("NIVEL " + (i + 1) + " - " + casillas[i].getMode().toString());
        }

        enemyHealthBars[numCasillaActual].setProgress(casillaActual.health / casillaActual.maxHealth);
        enemyHpLabels[numCasillaActual].setText(String.format("%.0f / %.0f", casillaActual.health, casillaActual.maxHealth));
        openActualTitledPane(numCasillaActual);
        openBotinTitledPane(numCasillaActual);

        if ((!jugador.isAlive() && !jugadorMuerto) || tablero.isPartidaAcabada()) {
            jugadorMuerto = true;
            detenerCombate();
            gameOver();
        }

        if (casillaActual.getEstado() != Casilla.Estado.EN_COMBATE) {
            detenerCombate();
            if (casillaActual.getEstado() == Casilla.Estado.MUERTO) {
                avanzarCasilla();
            }
        }

        actualizarBotinActual();
    }

    private void openActualTitledPane(int casillaActual) {
        for (int i = 0; i < niveles.length; i++) {
            niveles[i].setExpanded(i == casillaActual);
            niveles[i].setDisable(i != casillaActual);
        }
    }

    private void openBotinTitledPane(int casillaActual) {
        for (int i = 0; i < botines.length; i++) {
            botines[i].setExpanded(i == casillaActual);
            botines[i].setDisable(i != casillaActual);
        }
    }

    private void actualizarBotinActual() {
        int casillaActual = tablero.getJugador().getCasillaActual();
        Casilla casilla = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[casillaActual];

        switch (casillaActual) {
            case 0:
                if (casilla.getRewardIconUrl() != null) {
                    botin1Image.setImage(new Image(casilla.getRewardIconUrl()));
                }
                if (casilla.getRewardText() != null) {
                    botin1Label.setText(casilla.getRewardText());
                }
                break;
            case 1:
                if (casilla.getRewardIconUrl() != null) {
                    botin2Image.setImage(new Image(casilla.getRewardIconUrl()));
                }
                if (casilla.getRewardText() != null) {
                    botin2Label.setText(casilla.getRewardText());
                }
                break;
            case 2:
                if (casilla.getRewardIconUrl() != null) {
                    botin3Image.setImage(new Image(casilla.getRewardIconUrl()));
                }
                if (casilla.getRewardText() != null) {
                    botin3Label.setText(casilla.getRewardText());
                }
                break;
            case 3:
                if (casilla.getRewardIconUrl() != null) {
                    botin4Image.setImage(new Image(casilla.getRewardIconUrl()));
                }
                if (casilla.getRewardText() != null) {
                    botin4Label.setText(casilla.getRewardText());
                }
                break;
            case 4:
                if (casilla.getRewardIconUrl() != null) {
                    botin5Image1.setImage(new Image(casilla.getRewardIconUrl()));
                }
                if (casilla.getRewardIconUrl1() != null) {
                    botin5Image2.setImage(new Image(casilla.getRewardIconUrl1()));
                }
                if (casilla.getRewardText() != null) {
                    botin5Label1.setText(casilla.getRewardText());
                }
                if (casilla.getRewardText1() != null) {
                    botin5Label2.setText(casilla.getRewardText1());
                }
                break;
        }
    }

    private void generarCirculo() {
        final Random random = new Random();
        final int baseDelay = 1500;
        final int baseLifetime = 1450;

        Casilla casillaActualizada = tablero.getEtapas()[tablero.getJugador().getEtapaActual()].getCasillas()[tablero.getJugador().getCasillaActual()];
        float speedMultiplier = 1.0f - (tablero.getJugador().getVelocidad() / 100.0f);

        final int delayBetweenCircles = (int) (baseDelay * speedMultiplier);
        final int circleLifetime = (int) (baseLifetime * speedMultiplier);

        if (circleTimer != null) {
            circleTimer.cancel();
        }

        circleTimer = new Timer();
        circleTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (jugadorMuerto || tablero.isPartidaAcabada()) {
                        detenerCombate();
                        return;
                    }

                    AnchorPane currentPane = nivelContents[tablero.getJugador().getCasillaActual()];
                    double paneWidth = currentPane.getWidth();
                    double paneHeight = currentPane.getHeight();
                    double diameter = Math.min(paneWidth, paneHeight) * 0.25;

                    currentPane.getChildren().removeIf(node -> node instanceof Circle);

                    Circle circle = new Circle(diameter / 2, Color.RED);
                    circle.setOpacity(0.5);
                    circle.setLayoutX(random.nextDouble() * (paneWidth - diameter) + diameter / 2);
                    circle.setLayoutY(random.nextDouble() * (paneHeight - diameter) + diameter / 2);
                    circle.setManaged(false);

                    circle.setOnMouseClicked(event -> {
                        enviarMensajeAlServidor("4");
                        currentPane.getChildren().remove(circle);
                        actualizarInterfaz(tablero);
                        generarCirculo();
                    });

                    currentPane.getChildren().add(circle);

                    Timer removeCircleTimer = new Timer();
                    removeCircleTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> {
                                if (currentPane.getChildren().contains(circle)) {
                                    enviarMensajeAlServidor("5");
                                    currentPane.getChildren().remove(circle);
                                    actualizarInterfaz(tablero);
                                    generarCirculo();
                                }
                            });
                        }
                    }, circleLifetime);
                });
            }
        }, delayBetweenCircles);
    }

    private void detenerCombate() {
        if (combateTimer != null) {
            combateTimer.cancel();
            combateTimer = null;
        }
        if (circleTimer != null) {
            circleTimer.cancel();
            circleTimer = null;
        }
        btn_attack.setDisable(false);
        btn_skip.setDisable(false);
    }

    private void avanzarCasilla() {
        enviarMensajeAlServidor("2");
        actualizarBotinActual();
    }

    private void iniciarAtaque() {
        enviarMensajeAlServidor("1");
        combateTimer = new Timer();
        btn_attack.setDisable(true);
        btn_skip.setDisable(true);
        generarCirculo();
    }
}
