package com.bonet.threaddungeons;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private void initialize() {

        FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), vBox);
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);
        fadeInTransition.play();

        btn_login.setOnAction(actionEvent -> {
            try {
                // Cargar la nueva escena desde el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Main-view.fxml"));
                Parent root = fxmlLoader.load();

                MainApp.getStage().getScene().setRoot(root);

            } catch (IOException e) {
                e.printStackTrace();
                // Manejar cualquier excepción que pueda ocurrir al cargar la nueva escena
            }
        });
    }
}