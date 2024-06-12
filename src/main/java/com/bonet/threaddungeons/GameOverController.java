package com.bonet.threaddungeons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class GameOverController {
    @FXML
    private Button btn_back;
    @FXML
    private TableView<Score> tableViewTop;
    @FXML
    private TableColumn<Score, String> columnUsername;
    @FXML
    private TableColumn<Score, Integer> columnEtapaActual;
    @FXML
    private TableColumn<Score, Integer> columnCasillaActual;
    @FXML
    private TableColumn<Score, Double> columnDmg;
    @FXML
    private TableColumn<Score, Double> columnSpeed;
    @FXML
    private TableColumn<Score, Integer> columnOro;
    @FXML
    private Text errorMsg;
    private MainApp mainApp;
    private Socket sCliente;
    private boolean scoresLoaded = false;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSocket(Socket socket) {
        this.sCliente = socket;
        if (!scoresLoaded) {
            loadTopScores();
            scoresLoaded = true;
        }
    }

    @FXML
    private void initialize() {
        btn_back.setOnAction(event -> {
            closeSocket();
            mainApp.openLoginView();
        });

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnEtapaActual.setCellValueFactory(new PropertyValueFactory<>("etapaActual"));
        columnCasillaActual.setCellValueFactory(new PropertyValueFactory<>("casillaActual"));
        columnDmg.setCellValueFactory(new PropertyValueFactory<>("dmg"));
        columnSpeed.setCellValueFactory(new PropertyValueFactory<>("speed"));
        columnOro.setCellValueFactory(new PropertyValueFactory<>("oro"));
    }

    private void loadTopScores() {
        try (DataOutputStream output = new DataOutputStream(sCliente.getOutputStream());
             DataInputStream input = new DataInputStream(sCliente.getInputStream())) {

            // Enviar solicitud de top scores al servidor
            output.writeUTF("topScores");
            output.flush();

            // Leer la respuesta del servidor
            String jsonScores = input.readUTF();
            List<Score> topScores = new Gson().fromJson(jsonScores, new TypeToken<List<Score>>() {}.getType());

            // Actualizar la tabla con los top scores
            tableViewTop.getItems().setAll(topScores);

        } catch (IOException e) {
            e.printStackTrace();
            errorMsg.setText("Error al cargar las puntuaciones.");
            errorMsg.setVisible(true);
        } finally {
            closeSocket();
        }
    }

    private void closeSocket() {
        try {
            if (sCliente != null && !sCliente.isClosed()) {
                sCliente.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
