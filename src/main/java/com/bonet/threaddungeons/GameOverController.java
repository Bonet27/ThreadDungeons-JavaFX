package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class GameOverController {
    @FXML
    private Button btn_back; // Botón para volver
    @FXML
    private TableView<Score> tableViewTop; // Tabla para mostrar los scores
    @FXML
    private TableColumn<Score, String> columnUsername; // Columna para el nombre de usuario
    @FXML
    private TableColumn<Score, Integer> columnEtapaActual; // Columna para la etapa actual
    @FXML
    private TableColumn<Score, Integer> columnCasillaActual; // Columna para la casilla actual
    @FXML
    private TableColumn<Score, Double> columnDmg; // Columna para el daño
    @FXML
    private TableColumn<Score, Double> columnSpeed; // Columna para la velocidad
    @FXML
    private TableColumn<Score, Integer> columnOro; // Columna para el oro
    @FXML
    private Label errorMsg; // Etiqueta para mensajes de error
    private MainApp mainApp; // Referencia a la aplicación principal
    private Socket sCliente; // Socket de conexión al servidor
    private boolean scoresLoaded = false; // Bandera para evitar cargas múltiples de scores

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSocket(Socket socket) {
        this.sCliente = socket;
        if (!scoresLoaded) {
            loadTopScores(); // Cargar los top scores si no se han cargado aún
            scoresLoaded = true;
        }
    }

    @FXML
    private void initialize() {
        btn_back.setOnAction(event -> {
            closeSocket(); // Cerrar el socket
            mainApp.openLoginView(); // Abrir la vista de login
        });

        // Configurar columnas de la tabla
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnEtapaActual.setCellValueFactory(new PropertyValueFactory<>("etapaActual"));
        columnCasillaActual.setCellValueFactory(new PropertyValueFactory<>("casillaActual"));
        columnDmg.setCellValueFactory(new PropertyValueFactory<>("dmg"));
        columnSpeed.setCellValueFactory(new PropertyValueFactory<>("speed"));
        columnOro.setCellValueFactory(new PropertyValueFactory<>("oro"));
    }

    private void loadTopScores() {
        new Thread(() -> {
            try (DataOutputStream output = new DataOutputStream(sCliente.getOutputStream());
                 DataInputStream input = new DataInputStream(sCliente.getInputStream())) {

                // Enviar solicitud de top scores al servidor
                synchronized (output) {
                    output.writeUTF("topScores");
                    output.flush();
                }

                // Leer la respuesta del servidor
                String messageType = input.readUTF();
                if ("TOP_SCORES".equals(messageType)) {
                    String jsonScores = input.readUTF();
                    List<Score> topScores = new Gson().fromJson(jsonScores, new TypeToken<List<Score>>() {}.getType());

                    // Actualizar la tabla con los top scores
                    Platform.runLater(() -> tableViewTop.getItems().setAll(topScores));
                }

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    errorMsg.setText("Error al cargar las puntuaciones.");
                    errorMsg.setVisible(true);
                });
            } finally {
                closeSocket(); // Cerrar el socket al finalizar
            }
        }).start();
    }

    private void closeSocket() {
        try {
            if (sCliente != null && !sCliente.isClosed()) {
                sCliente.close(); // Cerrar el socket si está abierto
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
