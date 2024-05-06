package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.Socket;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button btn_login;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private TextField inputUsuario;

    @FXML
    private VBox vBox;

    @FXML
    private Label welcomeText;

    @FXML
    private Text errorMsg;

    @FXML
    private void initialize() {
        btn_login.setOnAction(actionEvent -> {
            openScene("Main-view.fxml");
        });
    }

    private void openScene(String scene) {
        try {
            // Cargar la nueva escena desde el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource(scene));
            Parent root = fxmlLoader.load();
            MainApp.getStage().getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}