package com.bonet.threaddungeons;

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
        btn_login.setOnAction(actionEvent -> {
            try {
                // Cargar la nueva escena desde el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Main-view.fxml"));
                Parent root = fxmlLoader.load();

                // Obtener el Stage actual desde el botón
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                // Crear una nueva escena con la raíz cargada del archivo FXML
                Scene scene = new Scene(root);

                stage.setMaximized(true);
                // Establecer la nueva escena en el Stage
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Manejar cualquier excepción que pueda ocurrir al cargar la nueva escena
            }
        });

    }
}