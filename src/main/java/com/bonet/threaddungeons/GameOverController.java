package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.List;

public class GameOverController {
    @FXML
    private Button btn_login;
    @FXML
    private Button btn_register;
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

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        btn_login.setOnAction(event -> mainApp.openLoginView());
        btn_register.setOnAction(event -> mainApp.openRegisterView());

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnEtapaActual.setCellValueFactory(new PropertyValueFactory<>("etapaActual"));
        columnCasillaActual.setCellValueFactory(new PropertyValueFactory<>("casillaActual"));
        columnDmg.setCellValueFactory(new PropertyValueFactory<>("dmg"));
        columnSpeed.setCellValueFactory(new PropertyValueFactory<>("speed"));
        columnOro.setCellValueFactory(new PropertyValueFactory<>("oro"));

        loadTopScores();
    }

    private void loadTopScores() {
        try {
            List<Score> topScores = DatabaseManager.getTopScores(15);
            tableViewTop.getItems().addAll(topScores);
        } catch (Exception e) {
            errorMsg.setText("Error al cargar las puntuaciones.");
            errorMsg.setVisible(true);
        }
    }
}
